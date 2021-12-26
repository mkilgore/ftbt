package com.mkilgore.ftbt;

import android.app.Application;

import com.mkilgore.ftbt.singleton.bluetooth.BluetoothManager;
import com.mkilgore.ftbt.singleton.Contextor;

/**
 * Created by Akexorcist on 4/13/2016 AD.
 */
public class FTBTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());
        BluetoothManager.init(Contextor.getInstance().getContext());
    }
}
