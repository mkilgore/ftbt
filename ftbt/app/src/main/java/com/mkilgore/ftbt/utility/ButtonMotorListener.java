package com.mkilgore.ftbt.utility;

import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.materialize.color.Material;
import com.mkilgore.ftbt.singleton.bluetooth.BluetoothManager;


public class ButtonMotorListener implements View.OnTouchListener {
    private boolean motorState = false;
    private int motor;

    public ButtonMotorListener(int _motor)
    {
        motor = _motor;
        // Turn motor off when listener is created
        BluetoothManager.getInstance().sendMotor(motor, motorState);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            BluetoothManager.getInstance().sendMotor(motor, true);
        } else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL) {
            BluetoothManager.getInstance().sendMotor(motor, false);
        }

        return false;
    }
}
