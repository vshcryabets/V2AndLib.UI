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
package com.v2soft.V2AndLib.demoapp;

import com.v2soft.V2AndLib.demoapp.ui.activities.BluetoothList;
import com.v2soft.V2AndLib.demoapp.ui.activities.DropBoxUpload;
import com.v2soft.V2AndLib.demoapp.ui.activities.EndlessListActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.GCPDemo;
import com.v2soft.V2AndLib.demoapp.ui.activities.OpenSLSample;
import com.v2soft.V2AndLib.demoapp.ui.activities.UDPDiscoveryList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * Demo start activity
 * @author vshcryabets@gmail.com
 *
 */
public class V2DemoActivity 
extends ListActivity 
implements OnItemClickListener {
    private final static String [] sItems = new String[]{"Bluetooth device list",
        "UDP discovery",
        "Upload to dropbox",
        "OpenSL sample",
        "Google Cloud Printing Demo",
        "Endless list"};
    private static final String LOG_TAG = V2DemoActivity.class.getSimpleName();
    private static final int ITEM_BLUETOOTH_DEVICE = 0;
    private static final int ITEM_UDP_DISCOVERY = 1;
    private static final int ITEM_DROPBOX_UPLOAD = 2;
    private static final int ITEM_OPEN_SL = 3;
    private static final int ITEM_GCP = 4;
    private static final int ITEM_ENDLESSLIST = 5;
    
    private ArrayAdapter<String> mAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sItems);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        switch ((int)arg3) {
        case ITEM_BLUETOOTH_DEVICE:
            startActivity(new Intent(this, BluetoothList.class));
            break;
        case ITEM_UDP_DISCOVERY:
            startActivity(new Intent(this, UDPDiscoveryList.class));
            break;
        case ITEM_DROPBOX_UPLOAD:
            startActivity(new Intent(this, DropBoxUpload.class));
            break;
        case ITEM_OPEN_SL:
            startActivity(new Intent(this, OpenSLSample.class));
            break;
        case ITEM_GCP:
            startActivity(new Intent(this, GCPDemo.class));
            break;
        case ITEM_ENDLESSLIST:
            startActivity(new Intent(this, EndlessListActivity.class));
            break;
        default:
            break;
        }
        Log.d(LOG_TAG, "item="+arg3);
    }
}