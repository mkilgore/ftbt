package com.mkilgore.ftbt.utility;

import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.mkilgore.ftbt.singleton.bluetooth.BluetoothManager;

public class ToggleButtonMotorListener implements CompoundButton.OnCheckedChangeListener {
    private int motor;

    public ToggleButtonMotorListener(int _motor)
    {
        motor = _motor;
        BluetoothManager.getInstance().sendMotor(motor, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked)
    {
        BluetoothManager.getInstance().sendMotor(motor, isChecked);
    }
}
