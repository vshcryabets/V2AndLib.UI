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

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.v2soft.AndLib.UI.R;
import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.ui.Adapters.CustomViewAdapter;
import com.v2soft.AndLib.ui.loaders.BluetoothScannerLoader;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Fragment that shows list of discovered bluetooth devices
 * @author vshcryabets@gmail.com
 *
 */
public abstract class BluetoothDeviceList<T extends BaseApplication<S>, S extends BaseApplicationSettings> 
extends BaseFragment<T, S> 
implements LoaderCallbacks<List<BluetoothDevice>>, 
    OnItemClickListener{
    protected CustomViewAdapter<BluetoothDevice> mAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(com.v2soft.AndLib.UI.R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BluetoothDeviceAdapter(getActivity());
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
    private class BluetoothView extends LinearLayout implements IDataView<BluetoothDevice> {
        private BluetoothDevice mData;

        public BluetoothView(Context context) {
            super(context);
            inflate(context, R.layout.v2andlib_listitem_twolines, this);
        }

        @Override
        public void setData(BluetoothDevice data) {
            mData = data;
            ((TextView)findViewById(R.id.v2andlib_1line)).setText(data.getName());
            ((TextView)findViewById(R.id.v2andlib_2line)).setText(data.getAddress());
        }

        @Override
        public BluetoothDevice getData() {
            return mData;
        }
    };
    
    private class BluetoothDeviceAdapter extends CustomViewAdapter<BluetoothDevice>{
        public BluetoothDeviceAdapter(
                Context context) {
            super(context, new CustomViewAdapterFactory<BluetoothDevice, IDataView<BluetoothDevice>>() {
                @Override
                public IDataView<BluetoothDevice> createView(Context context) {
                    return new BluetoothView(context);
                }
            });
        }
    };
    // ===================================================================
    // Loader callback
    // ===================================================================
    @Override
    public Loader<List<BluetoothDevice>> onCreateLoader(int id, Bundle args) {
        return new BluetoothScannerLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BluetoothDevice>> arg0, List<BluetoothDevice> arg1) {
        mAdapter.setData(arg1);
    }

    @Override
    public void onLoaderReset(Loader<List<BluetoothDevice>> arg0) {
        mAdapter.clear();
    }
}
