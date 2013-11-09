/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.content.Loader;

/**
 * WiFi device discovery loader
 * @author vshcryabets@gmail.com
 *
 */
public class WiFiScannerLoader 
extends Loader<List<WifiConfiguration>> {
    protected static final int START_DELAY_MS = 5000; // reload every 5 seconds
    protected static final int MAX_TTL = 5;
    protected Context mContext;
    protected WifiManager mAdapter;
    protected Map<String, WifiConfiguration> mAPMap;
    protected Map<String, Integer> mAPTTL;
    private Timer mTimer;  
    private int mRestartDiscoverDelay = START_DELAY_MS;

    public WiFiScannerLoader(Context context) {
        super(context);
        mContext = context;
        mAdapter = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mAPTTL = new HashMap<String, Integer>();
        mAPMap = new HashMap<String, WifiConfiguration>();
    }

    @Override
    protected void onStartLoading() {
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mReceiver, filter);
        restartDiscovery();
        super.onStartLoading();
    }

    private void restartDiscovery() {
        // get bonded devices
        final List<WifiConfiguration> devices = mAdapter.getConfiguredNetworks();
        for (WifiConfiguration device : devices) {
            addOrUpdateDevice(device);
        }
        // Request discover from BluetoothAdapter
        mAdapter.startScan();
        deliverResult(new ArrayList<WifiConfiguration>(mAPMap.values()));
    }

    @Override
    protected void onStopLoading() {
        if ( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
        mContext.unregisterReceiver(mReceiver);
        super.onStopLoading();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if ( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action) ) {
                // get bonded devices
                final List<WifiConfiguration> devices = mAdapter.getConfiguredNetworks();
                for (WifiConfiguration device : devices) {
                    addOrUpdateDevice(device);
                }
                // recalcualte TTLs
                Set<String> keys = mAPTTL.keySet();
                for (String string : keys) {
                    int value = mAPTTL.get(string);
                    if ( value > 0 ) {
                        value--;
                        mAPTTL.put(string, value);
                    } else {
                        mAPMap.remove(string);
                        mAPTTL.remove(string);
                    }
                }
                deliverResult(new ArrayList<WifiConfiguration>(mAPMap.values()));
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

    private void addOrUpdateDevice(WifiConfiguration device) {
        if ( mAPTTL.containsKey(device.SSID)) {
            mAPTTL.put(device.SSID, MAX_TTL);
        } else {
            mAPMap.put(device.SSID, device);
            mAPTTL.put(device.SSID, MAX_TTL);
        }
    }
}
