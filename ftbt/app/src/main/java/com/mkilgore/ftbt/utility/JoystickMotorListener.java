package com.mkilgore.ftbt.utility;

import android.util.Log;

import com.jmedeisis.bugstick.JoystickListener;
import com.mikepenz.materialize.color.Material;
import com.mkilgore.ftbt.singleton.bluetooth.BluetoothManager;

public class JoystickMotorListener implements JoystickListener {
    private int rightMotorGroup;
    private int leftMotorGroup;
    private long repeatTouchEventDelay = 50;
    private long lastTouchEventMillis;

    public JoystickMotorListener(int _rightMotorGroup, int _leftMotorGroup) {
        rightMotorGroup = _rightMotorGroup;
        leftMotorGroup = _leftMotorGroup;

        stopMotors();
    }

    @Override
    public void onDown() {

    }

    private void stopMotors()
    {
        BluetoothManager manager = BluetoothManager.getInstance();

        manager.sendMotor(rightMotorGroup * 2, false);
        manager.sendMotor(rightMotorGroup * 2 + 1, false);
        manager.sendMotor(leftMotorGroup * 2, false);
        manager.sendMotor(leftMotorGroup * 2 + 1, false);
    }

    private void setMotorDirection(double speed, int motorGroup)
    {
        BluetoothManager manager = BluetoothManager.getInstance();

        if (speed > 1) {
            manager.sendMotor(motorGroup * 2, true);
            manager.sendMotor(motorGroup * 2 + 1, false);
            manager.sendMotorSpeed(motorGroup, (int)(speed) + 54);
        } else if (speed < 1) {
            manager.sendMotor(motorGroup * 2, false);
            manager.sendMotor(motorGroup * 2 + 1, true);
            manager.sendMotorSpeed(motorGroup, (int)(-speed) + 54);
        } else {
            manager.sendMotor(motorGroup * 2, false);
            manager.sendMotor(motorGroup * 2 + 1, false);
        }
    }

    @Override
    public void onDrag(float degrees, float offset)
    {
        if (System.currentTimeMillis() - lastTouchEventMillis < repeatTouchEventDelay)
            return;

        lastTouchEventMillis = System.currentTimeMillis();

        // Translate degrees/offset to tank controls for two motors
        double lr_axis = offset * Math.cos(Math.toRadians((double)degrees));
        double ud_axis = offset * Math.sin(Math.toRadians((double)degrees));

        // Normalize values between [0, 100]
        lr_axis = lr_axis * 200;
        ud_axis = ud_axis * 200;

        double v = (200 - Math.abs(lr_axis)) * (ud_axis / 200) + ud_axis;
        double w = (200 - Math.abs(ud_axis)) * (lr_axis / 200) + lr_axis;

        double r = (v + w) / 2;
        double l = (v - w) / 2;

        Log.i("right", "right: " + r);
        Log.i("left", "left: " + l);

        setMotorDirection(r, rightMotorGroup);
        setMotorDirection(l, leftMotorGroup);
    }

    @Override
    public void onUp() {
        stopMotors();
    }
}
