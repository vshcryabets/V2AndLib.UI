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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.networking.DemoBroadcastDiscovery;
import com.v2soft.V2AndLib.demoapp.networking.DemoBroadcastReceiver;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoUDPDiscoveryList 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private DemoBroadcastDiscovery mDiscovery;
    private DemoBroadcastReceiver mReceiver;
    private List<InetAddress> mLocalAddresses;
    private List<InetAddress> mBroadcastAddresses;

    public static Fragment newInstance() {
        return new DemoUDPDiscoveryList();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mBroadcastAddresses = new LinkedList<InetAddress>();
            mLocalAddresses = new LinkedList<InetAddress>();
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if ( intf.isLoopback() ) continue;
                
                for (InterfaceAddress interfaceAddress : intf.getInterfaceAddresses()) {
                    if ( interfaceAddress.getBroadcast() == null ) continue;
                    mBroadcastAddresses.add(interfaceAddress.getBroadcast());
                    mLocalAddresses.add(interfaceAddress.getAddress());
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(com.v2soft.AndLib.UI.R.layout.fragment_button, null);
        view.findViewById(R.id.btnStart).setOnClickListener(this);
        try {
            mReceiver = new DemoBroadcastReceiver(mLocalAddresses.get(0));
            mDiscovery = new DemoBroadcastDiscovery();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mReceiver.startReceiver();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        mReceiver.stopReceiver();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ( id == R.id.btnStart ) {
            try {
                mDiscovery.startDiscovery(1235, 
                        mLocalAddresses.get(0), mBroadcastAddresses.get(0));
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
