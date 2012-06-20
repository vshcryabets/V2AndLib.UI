package com.v2soft.V2AndLib.demoapp.ui.loaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;

/**
 * Bluetooth device discovery loader
 * @author vshcryabets@gmail.com
 *
 */
public class BluetoothScannerLoader 
extends Loader<List<BluetoothDevice>> {
    protected static final long START_DELAY_MS = 5000; // reload every 5 seconds
    protected Context mContext;
    protected BluetoothAdapter mAdapter;
    protected List<BluetoothDevice> mDeviceList;
    private Timer mTimer;  

    public BluetoothScannerLoader(Context context) {
        super(context);
        mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceList = new ArrayList<BluetoothDevice>();
    }

    @Override
    protected void onStartLoading() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
        restartDiscovery();
        super.onStartLoading();
    }

    private void restartDiscovery() {
        mDeviceList.clear();
        // If we're already discovering, stop it
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mAdapter.startDiscovery();
    }

    @Override
    protected void onStopLoading() {
        if ( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
        mAdapter.cancelDiscovery();
        mContext.unregisterReceiver(mReceiver);
        super.onStopLoading();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ( BluetoothDevice.ACTION_FOUND.equals(action)) {               
                // found new device
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDeviceList.add(device);
                }
                deliverResult(new ArrayList<BluetoothDevice>(mDeviceList));
            } else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) ) {
                deliverResult(new ArrayList<BluetoothDevice>(mDeviceList));
                // discovery finished
                mTimer = new Timer("SessionLoader", true);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {restartDiscovery();}
                }, START_DELAY_MS);                
            }
        }
    };
}
