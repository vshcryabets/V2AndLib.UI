package com.v2soft.V2AndLib.demoapp.ui.loaders;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class BluetoothScannerLoader 
extends AsyncTaskLoader<List<BluetoothDevice>> {
    private Context mContext;
    private BluetoothAdapter mAdapter;
    
    public BluetoothScannerLoader(Context context) {
        super(context);
        mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public List<BluetoothDevice> loadInBackground() {
        // If we're already discovering, stop it
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mAdapter.startDiscovery();
        return null;
    }

    @Override
    protected void onStartLoading() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
        super.onStartLoading();
    }
    
    @Override
    protected void onStopLoading() {
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
            } else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) ) {
                // discovery finished
            }
        }
    };
}
