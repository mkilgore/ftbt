#ifndef INCLUDE_FT_CON_H
#define INCLUDE_FT_CON_H

#define FT_M12_EN_PIN PORTD5
#define FT_M34_EN_PIN PORTD6
#define FT_M56_EN_PIN PORTB3
#define FT_M78_EN_PIN PORTD3

enum {
    FT_CON_M1,
    FT_CON_M2,
    FT_CON_M3,
    FT_CON_M4,
    FT_CON_M5,
    FT_CON_M6,
    FT_CON_M7,
    FT_CON_M8,
};

/* M1-8 enable/disable */
void ft_con_motor(int motor, int enable);

void ft_con_m12_speed(int);
void ft_con_m34_speed(int);
void ft_con_m56_speed(int);
void ft_con_m78_speed(int);

void ft_con_motor_speed(int motor_group, int speed);

void ft_con_init(void);

#define L298N_ENA_DDR  DDRD
#define L298N_ENA_PORT PORTD
#define L298N_ENA_PIN  PORTD5

#define L298N_ENB_DDR  DDRD
#define L298N_ENB_PORT PORTD
#define L298N_ENB_PIN  PORTD6

#define L298N_LEFT_FOR_DDR  DDRC
#define L298N_LEFT_FOR_PORT PORTC
#define L298N_LEFT_FOR_PIN  PORTC0

#define L298N_LEFT_BACK_DDR  DDRC
#define L298N_LEFT_BACK_PORT PORTC
#define L298N_LEFT_BACK_PIN  PORTC1

#define L298N_RIGHT_BACK_DDR  DDRC
#define L298N_RIGHT_BACK_PORT PORTC
#define L298N_RIGHT_BACK_PIN  PORTC2

#define L298N_RIGHT_FOR_DDR  DDRC
#define L298N_RIGHT_FOR_PORT PORTC
#define L298N_RIGHT_FOR_PIN  PORTC4

void l298n_init(void);

void l298n_left_enable(void);
void l298n_left_disable(void);
void l298n_left_toggle(void);
void l298n_left_stop(void);
void l298n_left_forward(void);
void l298n_left_backward(void);

void l298n_right_enable(void);
void l298n_right_disable(void);
void l298n_right_toggle(void);
void l298n_right_stop(void);
void l298n_right_forward(void);
void l298n_right_backward(void);

#endif
