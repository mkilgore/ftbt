
#include "common.h"

#include <stdio.h>

#include <avr/interrupt.h>
#include <avr/io.h>

#include "debug_serial.h"
#include "serial.h"
#include "bt_ft.h"
#include "ft_con.h"

int main(void)
{
    ft_con_init();
    bt_ft_init();
    sei();

    while (1) {
        bt_ft_handle_state();
        bt_ft_apply();

        _delay_ms(16);
    }

    return 0;
}

