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
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.v2soft.AndLib.ui.adapters.CustomViewAdapter;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.AndLib.ui.loaders.UDPScannerLoader;
import com.v2soft.AndLib.ui.views.DataView;
import com.v2soft.AndLib.ui.views.IDataView;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.networking.DemoBroadcastDiscovery;
import com.v2soft.V2AndLib.demoapp.networking.DemoBroadcastReceiver;
import com.v2soft.V2AndLib.demoapp.networking.DemoUDPHost;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoUDPDiscoveryList 
extends BaseFragment<DemoApplication, DemoAppSettings> 
implements LoaderCallbacks<List<DemoUDPHost>>{
    private DemoBroadcastReceiver mReceiver;
    private List<InetAddress> mLocalAddresses;
    private List<InetAddress> mBroadcastAddresses;
    protected CustomViewAdapter<DemoUDPHost> mAdapter;

    public static Fragment newInstance() {
        return new DemoUDPDiscoveryList();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mBroadcastAddresses = new LinkedList<InetAddress>();
            mLocalAddresses = new LinkedList<InetAddress>();
            for (Enumeration<NetworkInterface> en = 
                    NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if ( intf.isLoopback() ) continue;

                for (InterfaceAddress interfaceAddress : intf.getInterfaceAddresses()) {
                    if ( interfaceAddress.getBroadcast() == null ) continue;
                    mBroadcastAddresses.add(interfaceAddress.getBroadcast());
                    mLocalAddresses.add(interfaceAddress.getAddress());
                }
            }
            getLoaderManager().initLoader(0, null, this);
            LoaderManager.enableDebugLogging(true);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BluetoothDeviceAdapter(getActivity());
        //        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);        
        try {
            mReceiver = new DemoBroadcastReceiver(mLocalAddresses.get(0));
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
    // ===================================================================
    // Bluetooth device adapter
    // ===================================================================
    private class BluetoothView extends DataView<DemoUDPHost> {
        public BluetoothView(Context context) {
            super(context, R.layout.v2andlib_listitem_twolines);
        }
    };

    private class BluetoothDeviceAdapter extends CustomViewAdapter<DemoUDPHost>{
        public BluetoothDeviceAdapter(
                Context context) {
            super(context, new CustomViewAdapterFactory<DemoUDPHost, IDataView<DemoUDPHost>>() {
                @Override
                public IDataView<DemoUDPHost> createView(Context context, int type) {
                    return new BluetoothView(context);
                }
            });
        }
    };
    // ===================================================================
    // Loader callback
    // ===================================================================
    @Override
    public Loader<List<DemoUDPHost>> onCreateLoader(int id, Bundle args) {
        return new UDPScannerLoader<DemoUDPHost>(getActivity(),
        		new DemoBroadcastDiscovery(
                        mLocalAddresses.get(0), 
                        mBroadcastAddresses.get(0)));
    }

    @Override
    public void onLoadFinished(Loader<List<DemoUDPHost>> arg0, List<DemoUDPHost> arg1) {
        mAdapter.setData(arg1);
    }

    @Override
    public void onLoaderReset(Loader<List<DemoUDPHost>> arg0) {
        mAdapter.clear();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}
