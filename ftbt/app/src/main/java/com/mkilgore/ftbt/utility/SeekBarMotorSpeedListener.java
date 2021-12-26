package com.mkilgore.ftbt.utility;

import android.util.Log;
import android.widget.SeekBar;

import com.mkilgore.ftbt.singleton.bluetooth.BluetoothManager;

public class SeekBarMotorSpeedListener implements SeekBar.OnSeekBarChangeListener {
    private int motorGroup;

    public SeekBarMotorSpeedListener(int _motorGroup)
    {
        motorGroup = _motorGroup;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Log.d("Progress", "" + progress);
        BluetoothManager.getInstance().sendMotorSpeed(motorGroup, progress + 135);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
