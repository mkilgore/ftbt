package com.mkilgore.ftbt.view.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.mkilgore.ftbt.R;
import com.mkilgore.ftbt.utility.ButtonMotorListener;
import com.mkilgore.ftbt.utility.JoystickMotorListener;
import com.mkilgore.ftbt.utility.SeekBarMotorSpeedListener;
import com.mkilgore.ftbt.view.ActivityListener;
import com.mkilgore.ftbt.widget.JoyStickPad;

public class JoyStickFragment extends Fragment {
    private JoyStickPad leftJoyStick;

    private VerticalSeekBar m2SeekBar;
    private VerticalSeekBar m4SeekBar;

    private AppCompatButton o3Button;
    private AppCompatButton o4Button;
    private AppCompatButton o7Button;
    private AppCompatButton o8Button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_joystick_4buttons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView(view);
        setupView();
    }

    private void bindView(View view) {
        leftJoyStick = (JoyStickPad) view.findViewById(R.id.js_analog_left);
        m2SeekBar = (VerticalSeekBar) view.findViewById(R.id.m2_speed);
        m4SeekBar = (VerticalSeekBar) view.findViewById((R.id.m4_speed));
        o3Button = (AppCompatButton) view.findViewById(R.id.btn_o3);
        o4Button = (AppCompatButton) view.findViewById(R.id.btn_o4);
        o7Button = (AppCompatButton) view.findViewById(R.id.btn_o7);
        o8Button = (AppCompatButton) view.findViewById(R.id.btn_o8);
    }

    private void setupView() {
        leftJoyStick.setJoystickListener(new JoystickMotorListener(0, 2));

        m2SeekBar.setOnSeekBarChangeListener(new SeekBarMotorSpeedListener(1));
        m4SeekBar.setOnSeekBarChangeListener(new SeekBarMotorSpeedListener(3));

        o3Button.setOnTouchListener(new ButtonMotorListener(2));
        o4Button.setOnTouchListener(new ButtonMotorListener(3));
        o7Button.setOnTouchListener(new ButtonMotorListener(6));
        o8Button.setOnTouchListener(new ButtonMotorListener(7));

        ((ActivityListener) getActivity()).setActivityTitle("JoyStick");
    }
}
