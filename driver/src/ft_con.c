
#include "common.h"

#include <avr/interrupt.h>
#include <avr/io.h>

#include "ft_con.h"

void ft_con_init(void)
{
    DDRD |= _BV(DDD3) | _BV(DDD5) | _BV(DDD6);
    DDRB |= _BV(DDB3);

    /* Turn on Timer 0 - controls M12 and M34 PWM */
    TCCR0A = _BV(COM0A1) | _BV(COM0B1) | _BV(WGM00); // | _BV(WGM01) | _BV(WGM00); // _BV(WGM00); 
    TCCR0B = _BV(CS00);

    /* Turn on Timer 2 - controls M56 and M78 PWM */
    TCCR2A = _BV(COM2A1) | _BV(COM2B1) | _BV(WGM20); // _BV(WGM21) | _BV(WGM20);
    TCCR2B = _BV(CS20);

    /* Turn on PWM outputs */
    PORTD |= _BV(DDD3) | _BV(DDD5) | _BV(DDD6);
    PORTB |= _BV(DDB3);

    DDRC = 0x3F;
    PORTC &= ~0x3F;

    /* HACK: V1.0 PCB is wired wrong, fixed by wiring M7_IN and M8_IN to PB4 and PB5 */
    DDRB |= _BV(DDB4) | _BV(DDB5);
    PORTB &= ~(_BV(PORTB4) | _BV(PORTB5));
}

void ft_con_motor(int motor, int enable)
{
    if (motor < 6) {
        if (enable)
            PORTC |= _BV(motor);
        else
            PORTC &= ~_BV(motor);
    /* HACK: V1.0 PCB is wired wrong, fixed by wiring M7_IN and M8_IN to PB4 and PB5 */
    } else if (motor == 6) {
        if (enable)
            PORTB |= _BV(PORTB5);
        else
            PORTB &= ~_BV(PORTB5);
    } else {
        if (enable)
            PORTB |= _BV(PORTB4);
        else
            PORTB &= ~_BV(PORTB4);
    }
}

void ft_con_m12_speed(int speed)
{
    OCR0B = speed;
}

void ft_con_m34_speed(int speed)
{
    OCR0A = speed;
}

void ft_con_m56_speed(int speed)
{
    OCR2A = speed;
}

void ft_con_m78_speed(int speed)
{
    OCR2B = speed;
}

void ft_con_motor_speed(int motor_group, int speed)
{
    switch (motor_group) {
    case 0:
        ft_con_m12_speed(speed);
        break;

    case 1:
        ft_con_m34_speed(speed);
        break;

    case 2:
        ft_con_m56_speed(speed);
        break;

    case 3:
        ft_con_m78_speed(speed);
        break;
    }
}
