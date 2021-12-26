
package app.akexorcist.bluetotohspp.library;

import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

@SuppressLint("NewApi")
public class BluetoothService2 {
    private static final String TAG = "Bluetooth2 Service";

    private final Handler mHandler;
    private int mState;

    public BluetoothService2(Context context, Handler handler) {
        mHandler = handler;
    }

    public synchronized int getState() {
        return mState;
    }

    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(BluetoothState.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized void connect(BluetoothDevice device) {

    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {

    }

    public synchronized void stop() {

    }

    public void write(byte[] out) {

    }

    private class BluetoothThread extends Thread {
        private BluetoothAdapter mAdapter;
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutSTream;

        public BluetoothThread() {
            mAdapter = null;
        }

        public void run() {
            setName("BluetoothThread");

            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }
}
