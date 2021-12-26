
#include "common.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "bt_ft.h"
#include "ft_con.h"
#include "serial.h"


/* We implement queue as a ring buffer of messages from the serial input. As
 * characters come in, they are placed into the buffer at msg_write_idx. When
 * we recieve a newline character, we increment msg_write_idx to the next
 * value.
 *
 * Separately, msg_write_idx and msg_read_idx are checked, and if they differ,
 * then we will start processing all the messages in the queue until
 * msg_read_idx equals msg_write_idx.
 *
 * This works assuming MSG_BUF_CNT is high enough that we don't run out of
 * queue spots when getting lots of input.
 */
#define MSG_BUF_CNT 20
#define MSG_LEN 20

static volatile char msg_buf[MSG_BUF_CNT][MSG_LEN];
static volatile uint16_t msg_len[MSG_BUF_CNT];

static volatile uint16_t msg_read_idx;
static volatile uint16_t msg_write_idx;

static void handle_serial_char(char ch)
{
    /* This handles characters from the serial. Note we're called in an
     * interrupt context so we can't do the processing here and have to finish
     * up really fast. */
    if (ch == '\n') {
        if (msg_len[msg_write_idx] > 0) {
            msg_write_idx++;
            if (msg_write_idx == MSG_BUF_CNT)
                msg_write_idx = 0;
        }
    } else if (msg_len[msg_write_idx] < MSG_LEN - 1) {
        msg_buf[msg_write_idx][msg_len[msg_write_idx]] = ch;
        msg_len[msg_write_idx]++;
    }
}

struct motor_state {
    uint8_t enabled; /* bitfield, coresponds to FT_CON_M* bits */
    uint8_t changed;
    uint8_t speed[4];
    uint8_t speed_changed;
};

static struct motor_state motor_state;

static FILE *serial_out;

static int serial_write(char c, FILE *f)
{
    serial_send_char(c);
    return c;
}

void bt_ft_init(void)
{
    serial_init(handle_serial_char);

    serial_out = fdevopen(serial_write, NULL);
}

void bt_ft_handle_state(void)
{
    /* Handle any messages in the queue */
    while (msg_read_idx != msg_write_idx) {
        /* We turn interrupts off for this part so that the serial interrupt
         * can't touch any of these variables while we work with them */
        cli();
        char tmp_msg[MSG_LEN] = { 0 };
        uint16_t len = msg_len[msg_read_idx];
        uint16_t i;

        for (i = 0; i < len; i++)
            tmp_msg[i] = msg_buf[msg_read_idx][i];

        msg_len[msg_read_idx] = 0;
        for (i = 0; i < MSG_BUF_CNT; i++)
            msg_buf[msg_read_idx][i] = 0;

        msg_read_idx++;
        if (msg_read_idx == MSG_BUF_CNT)
            msg_read_idx = 0;

        sei();

        char *stringp = tmp_msg;

        char *id = strsep(&stringp, ":");

        if (!id)
            return ;

        /*
         * The data from the BT controller is one of:
         *
         * m:N:E
         * s:N:V
         */
        if (strcmp(id, "m") == 0) {
            id = strsep(&stringp, ":");
            char *enabled = strsep(&stringp, ":");

            if (!id || !enabled)
                continue;

            int m = atoi(id);
            if (m < 0 || m > 8)
                continue;

            int e = atoi(enabled);
            if (e < 0 || e > 1)
                continue;
            fprintf(serial_out, "Motor string, id: %s, enabled: %s, m: %d, e: %d\n", id, enabled, m, e);

            if (e)
                motor_state.enabled |= (1 << m);
            else
                motor_state.enabled &= ~(1 << m);

            motor_state.changed |= (1 << m);
        } else if (strcmp(id, "s") == 0) {
            id = strsep(&stringp, ":");
            char *speed = strsep(&stringp, ":");

            if (!id || !speed)
                continue;

            int m = atoi(id);
            if (m < 0 || m > 4)
                continue;

            int s = atoi(speed);
            if (s < 0 || s > 255)
                continue;

            motor_state.speed[m] = s;
            motor_state.speed_changed |= (1 << m);
        }
    }
}

void bt_ft_apply(void)
{
    if (motor_state.changed) {
        int i;
        for (i = 0; i < 8; i++) {
            if (motor_state.changed & (1 << i)) {
                fprintf(serial_out, "Setting motor: m: %d, e: %d\n", i, motor_state.enabled & (1 << i));
                ft_con_motor(i, motor_state.enabled & (1 << i));
            }
        }

        motor_state.changed = 0;
    }

    if (motor_state.speed_changed) {
        int i;
        for (i = 0; i < 4; i++)
            if (motor_state.speed_changed & (1 << i))
                ft_con_motor_speed(i, motor_state.speed[i]);

        motor_state.speed_changed = 0;
    }
}

#if 0

static float normalize(int8_t v)
{
    return (float)v / 128 * 100;
}

static float abs8(float v)
{
    if (v < 0)
        return -v;

    return v;
}

void bt_gamepad_apply(struct car_state *car)
{
    /* Yay, math
     *
     * This convert the axis values into RL motor speeds. s*/
    float ud = -normalize(gamepad_state.ud_axis);
    float lr = -normalize(gamepad_state.lr_axis);

    float v = ((float)100 - abs8(lr)) * (ud / (float)100) + ud;
    float w = ((float)100 - abs8(ud)) * (lr / (float)100) + lr;

    float r = ((v + w) / 2);
    float l = ((v - w) / 2);

    if (r > 1) {
        ft_con_motor(FT_CON_M5, 0);
        ft_con_motor(FT_CON_M6, 1);
        ft_con_m56_speed((uint8_t)(r * 2) + 50);
        // car_state_right_motor_set(car, MOTOR_FOR);
        // car_state_motor_right_speed_set(car, (uint8_t)(r * 2) + 50);
    } else if (r < -1) {
        ft_con_motor(FT_CON_M5, 1);
        ft_con_motor(FT_CON_M6, 0);
        ft_con_m56_speed((uint8_t)(r * 2) + 50);
        // car_state_right_motor_set(car, MOTOR_BACK);
        // car_state_motor_right_speed_set(car, (uint8_t)(-r * 2) + 50);
    } else {
        ft_con_motor(FT_CON_M5, 0);
        ft_con_motor(FT_CON_M6, 0);
        // car_state_right_motor_set(car, MOTOR_STOPPED);
    }

    if (l > 1) {
        ft_con_motor(FT_CON_M1, 0);
        ft_con_motor(FT_CON_M2, 1);
        ft_con_m12_speed((uint8_t)(l * 2) + 50);
        // car_state_left_motor_set(car, MOTOR_FOR);
        // car_state_motor_left_speed_set(car, (uint8_t)(l * 2) + 50);
    } else if (l < -1) {
        ft_con_motor(FT_CON_M1, 1);
        ft_con_motor(FT_CON_M2, 0);
        ft_con_m12_speed((uint8_t)(l * 2) + 50);
        // car_state_left_motor_set(car, MOTOR_BACK);
        // car_state_motor_left_speed_set(car, (uint8_t)(-l * 2) + 50);
    } else {
        ft_con_motor(FT_CON_M1, 0);
        ft_con_motor(FT_CON_M2, 0);
        // car_state_left_motor_set(car, MOTOR_STOPPED);
    }

    ft_con_m34_speed(128);
    ft_con_m78_speed(128);

    if (gamepad_state.buttons[0])
        ft_con_motor(FT_CON_M3, 1);
    else
        ft_con_motor(FT_CON_M3, 0);

    if (gamepad_state.buttons[1])
        ft_con_motor(FT_CON_M4, 1);
    else
        ft_con_motor(FT_CON_M4, 0);

    if (gamepad_state.buttons[2])
        ft_con_motor(FT_CON_M7, 1);
    else
        ft_con_motor(FT_CON_M7, 0);

    if (gamepad_state.buttons[3])
        ft_con_motor(FT_CON_M8, 1);
    else
        ft_con_motor(FT_CON_M8, 0);

    // int servo_degree = car->servo_degree;
    // if (gamepad_state.buttons[1])
    //     servo_degree = 192;

    // if (gamepad_state.buttons[3])
    //     servo_degree = 64;

    // if (gamepad_state.buttons[2])
    //     servo_degree = 128;

    // car_state_servo_degree_set(car, servo_degree);
}
#endif
