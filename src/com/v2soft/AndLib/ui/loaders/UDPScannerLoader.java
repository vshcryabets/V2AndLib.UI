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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.Loader;
import android.util.Log;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.v2soft.AndLib.networking.UDPBroadcastDiscovery;
import com.v2soft.AndLib.networking.UDPBroadcastDiscovery.UDPBroadcastListener;

/**
 * UDP peer discovery loader
 * @author vshcryabets@gmail.com
 *
 */
public class UDPScannerLoader<T> 
extends Loader<List<T>> {
    protected static final int START_DELAY_MS = 5000; // reload every 5 seconds
    protected static final int MAX_TTL = 5;
    private static final String LOG_TAG = UDPScannerLoader.class.getSimpleName();
    protected Context mContext;
    protected List<T> mDeviceList;
    protected List<Integer> mDeviceTTL;
    protected UDPBroadcastDiscovery mDiscovery;
    private Timer mTimer;
    private int mRestartDiscoverDelay = START_DELAY_MS;

    public UDPScannerLoader(Context context, 
            UDPBroadcastDiscovery discovery) {
        super(context);
        mContext = context;
        mDeviceList = new ArrayList<T>();
        mDeviceTTL = new ArrayList<Integer>();
        mDiscovery = discovery;
    }

    @Override
    protected void onStartLoading() {
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mDiscovery.setListener(mListener);
        try {
            restartDiscovery();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString(), e);
        }
        super.onStartLoading();
    }

    protected void restartDiscovery() throws SocketException {
        // If we're already discovering, stop it
        if (mDiscovery.isDiscovering()) {
            mDiscovery.stopDiscovery();
        }
        // Request discover from BluetoothAdapter
        mDiscovery.startDiscovery();
        deliverResult(new ArrayList<T>(mDeviceList));
    }

    @Override
    protected void onStopLoading() {
        if ( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
        mDiscovery.stopDiscovery();
        super.onStopLoading();
    }

    private UDPBroadcastListener mListener = new UDPBroadcastListener(){
        @Override
        public void onDiscoveryStarted() {
        }
        @Override
        public void onDiscoveryFinished() {
            // recalculate TTLs
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
            deliverResult(new ArrayList<T>(mDeviceList));
            if ( UDPScannerLoader.this.isStarted() ) {
                // discovery finished
                mTimer = new Timer("UDPPeersLoader", true);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            restartDiscovery();
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.toString(), e);
                        }
                    }
                }, mRestartDiscoverDelay);
            }
        }

        @Override
        public void onNewServer(Object item) {
            // If it's already found, skip it, because it's been listed already
            if ( addOrUpdateDevice((T) item) ) {
                deliverResult(new ArrayList<T>(mDeviceList));
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

    /**
     * 
     * @param device
     * @return true if this is new item
     */
    protected boolean addOrUpdateDevice(T device) {
        int pos = mDeviceList.indexOf(device);
        if ( pos < 0 ) {
            mDeviceList.add(device);
            mDeviceTTL.add(MAX_TTL);
            return true;
        } else {
            mDeviceTTL.set(pos, MAX_TTL);
            return false;
        }
    }
}
