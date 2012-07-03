/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v2soft.AndLib.ui.loaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
    protected static final int START_DELAY_MS = 5000; // reload every 5 seconds
    protected static final int MAX_TTL = 5;
    protected Context mContext;
    protected BluetoothAdapter mAdapter;
    protected List<BluetoothDevice> mDeviceList;
    protected List<Integer> mDeviceTTL;
    private Timer mTimer;  
    private int mRestartDiscoverDelay = START_DELAY_MS;

    public BluetoothScannerLoader(Context context) {
        super(context);
        mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceList = new ArrayList<BluetoothDevice>();
        mDeviceTTL = new ArrayList<Integer>();
    }

    @Override
    protected void onStartLoading() {
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
        restartDiscovery();
        super.onStartLoading();
    }

    private void restartDiscovery() {
        // get bonded devices
        final Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            addOrUpdateDevice(device);
        }
        // If we're already discovering, stop it
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mAdapter.startDiscovery();
        deliverResult(new ArrayList<BluetoothDevice>(mDeviceList));
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
            final String action = intent.getAction();
            if ( BluetoothDevice.ACTION_FOUND.equals(action)) {               
                // found new device
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    addOrUpdateDevice(device);
                }
                deliverResult(new ArrayList<BluetoothDevice>(mDeviceList));
            } else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) ) {
                // recalcualte TTLs
                int i = 0;
                while ( i < mDeviceTTL.size() ) {
                    int value = mDeviceTTL.get(i);
                    if ( value > 0 ) {
                        value--;
                        mDeviceTTL.set(i, value);
                        i++;
                    } else {
                        mDeviceList.remove(i);
                        mDeviceTTL.remove(i);
                    }
                }
                deliverResult(new ArrayList<BluetoothDevice>(mDeviceList));
                // discovery finished
                mTimer = new Timer("SessionLoader", true);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {restartDiscovery();}
                }, mRestartDiscoverDelay);                
            }
        }
    };

    /**
     * Change delay between discovery restart
     * @param timeout
     */
    public void setDiscoveryDelay(int timeout) {
        mRestartDiscoverDelay = timeout;
    }
    
    private void addOrUpdateDevice(BluetoothDevice device) {
        int pos = mDeviceList.indexOf(device);
        if ( pos < 0 ) {
            mDeviceList.add(device);
            mDeviceTTL.add(MAX_TTL);
        } else {
            mDeviceTTL.set(pos, MAX_TTL);
        }
    }
}
