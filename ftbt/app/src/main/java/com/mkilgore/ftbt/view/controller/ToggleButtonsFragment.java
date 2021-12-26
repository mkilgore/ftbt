package com.mkilgore.ftbt.view.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.mkilgore.ftbt.R;
import com.mkilgore.ftbt.utility.SeekBarMotorSpeedListener;
import com.mkilgore.ftbt.utility.ToggleButtonMotorListener;
import com.mkilgore.ftbt.view.ActivityListener;

public class ToggleButtonsFragment extends Fragment {
    private CompoundButton buttons[] = new CompoundButton[8];
    private VerticalSeekBar seekBars[] = new VerticalSeekBar[4];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toggle_buttons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView(view);
        setupView();
    }

    private void bindView(View view) {
        buttons[0] = view.findViewById(R.id.togglebtn_o1);
        buttons[1] = view.findViewById(R.id.togglebtn_o2);
        buttons[2] = view.findViewById(R.id.togglebtn_o3);
        buttons[3] = view.findViewById(R.id.togglebtn_o4);
        buttons[4] = view.findViewById(R.id.togglebtn_o5);
        buttons[5] = view.findViewById(R.id.togglebtn_o6);
        buttons[6] = view.findViewById(R.id.togglebtn_o7);
        buttons[7] = view.findViewById(R.id.togglebtn_o8);

        seekBars[0] = view.findViewById(R.id.m1_speed);
        seekBars[1] = view.findViewById(R.id.m2_speed);
        seekBars[2] = view.findViewById(R.id.m3_speed);
        seekBars[3] = view.findViewById(R.id.m4_speed);
    }

    private void setupView() {
        int i;
        for (i = 0; i < buttons.length; i++)
            buttons[i].setOnCheckedChangeListener(new ToggleButtonMotorListener(i));

        for (i = 0; i < seekBars.length; i++)
            seekBars[i].setOnSeekBarChangeListener(new SeekBarMotorSpeedListener(i));

        ((ActivityListener) getActivity()).setActivityTitle("Toggle Buttons");
    }
}
