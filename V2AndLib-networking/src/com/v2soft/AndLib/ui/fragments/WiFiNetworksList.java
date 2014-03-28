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
package com.v2soft.AndLib.ui.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.networking.R;
import com.v2soft.AndLib.ui.adapters.CustomViewAdapter;
import com.v2soft.AndLib.ui.loaders.WiFiScannerLoader;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Fragment that shows list of discovered bluetooth devices
 * @author vshcryabets@gmail.com
 *
 */
public abstract class WiFiNetworksList<T extends BaseApplication<S>, S extends BaseApplicationSettings<?>> 
extends BaseFragment<T, S> 
implements LoaderCallbacks<List<WifiConfiguration>>, 
    OnItemClickListener{
    protected CustomViewAdapter<WifiConfiguration> mAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new WiFiAdapter(getActivity());
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);
        return view;
    }
    
    @Override
    public void onAttach(Activity activity) {
        getLoaderManager().initLoader(0, null, this);
        LoaderManager.enableDebugLogging(true);
        super.onAttach(activity);
    }
    // ===================================================================
    // Bluetooth device adapter
    // ===================================================================
    private class WiFiNetworkView extends LinearLayout implements IDataView<WifiConfiguration> {
        private WifiConfiguration mData;

        public WiFiNetworkView(Context context) {
            super(context);
            inflate(context, R.layout.v2andlib_listitem_twolines, this);
        }

        @Override
        public void setData(WifiConfiguration data) {
            mData = data;
            ((TextView)findViewById(R.id.v2andlib_1line)).setText(data.SSID);
            ((TextView)findViewById(R.id.v2andlib_2line)).setText(data.BSSID);
        }

        @Override
        public WifiConfiguration getData() {
            return mData;
        }
    };
    
    private class WiFiAdapter extends CustomViewAdapter<WifiConfiguration>{
        public WiFiAdapter(
                Context context) {
            super(context, new CustomViewAdapterFactory<WifiConfiguration, IDataView<WifiConfiguration>>() {
                @Override
                public IDataView<WifiConfiguration> createView(Context context, int type) {
                    return new WiFiNetworkView(context);
                }
            });
        }
    };
    // ===================================================================
    // Loader callback
    // ===================================================================
    @Override
    public Loader<List<WifiConfiguration>> onCreateLoader(int id, Bundle args) {
        return new WiFiScannerLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<WifiConfiguration>> arg0, List<WifiConfiguration> arg1) {
        mAdapter.setData(arg1);
    }

    @Override
    public void onLoaderReset(Loader<List<WifiConfiguration>> arg0) {
        mAdapter.clear();
    }
}
