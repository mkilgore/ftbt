package com.mkilgore.ftbt.singleton.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mkilgore.ftbt.singleton.BusProvider;
import com.mkilgore.ftbt.singleton.bluetooth.event.BluetoothConnectedEvent;
import com.mkilgore.ftbt.singleton.bluetooth.event.BluetoothConnectionFailedEvent;
import com.mkilgore.ftbt.singleton.bluetooth.event.BluetoothDataReceivedEvent;
import com.mkilgore.ftbt.singleton.bluetooth.event.BluetoothDisconnectedEvent;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


/**
 * Created by Akexorcist on 4/13/2016 AD.
 */
public class BluetoothManager implements BluetoothSPP.OnDataReceivedListener, BluetoothSPP.BluetoothConnectionListener {
    private static BluetoothManager bluetoothManager;

    public static void init(Context context) {
        bluetoothManager = new BluetoothManager(context);
    }

    public static BluetoothManager getInstance() {
        return bluetoothManager;
    }

    private BluetoothSPP bt;

    public BluetoothManager(Context context) {
        bt = new BluetoothSPP(context);
    }

    public void setupService() {
        bt.setupService();
    }

    public void startService() {
        bt.startService(false);
    }

    public void stopService() {
        bt.stopService();
    }

    public boolean isBluetoothAvailable() {
        return bt.isBluetoothAvailable();
    }

    public boolean isServiceAvailable() {
        return bt.isServiceAvailable();
    }

    public void openBluetoothConnection(Activity activity) {
        Intent intent = new Intent(activity, DeviceList.class);
        activity.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public void enable() {
        bt.enable();
    }

    public void connect(Intent data) {
        bt.connect(data);
        bt.setOnDataReceivedListener(this);
        bt.setBluetoothConnectionListener(this);
    }

    private void send(String message) {
        bt.send(message, true);
    }

    public void sendMotor(int motor, boolean enabled)
    {
        send("m:" + motor + ":" + (enabled? "1": "0"));
    }

    public void sendMotorSpeed(int motorGroup, int speed)
    {
        send("s:" + motorGroup + ":" + speed);
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        Log.d("bluetooth", "Bluetooth message: " + message);
        BusProvider.getInstance().getProvider().post(new BluetoothDataReceivedEvent(data, message));
    }

    @Override
    public void onDeviceConnected(String name, String address) {
        BusProvider.getInstance().getProvider().post(new BluetoothConnectedEvent(name, address));
    }

    @Override
    public void onDeviceDisconnected() {
        BusProvider.getInstance().getProvider().post(new BluetoothDisconnectedEvent());
    }

    @Override
    public void onDeviceConnectionFailed() {
        BusProvider.getInstance().getProvider().post(new BluetoothConnectionFailedEvent());
    }
}
