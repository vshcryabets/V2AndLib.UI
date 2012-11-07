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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.v2soft.V2AndLib.demoapp.ui.activities.BluetoothList;
import com.v2soft.V2AndLib.demoapp.ui.activities.DemoBackStack;
import com.v2soft.V2AndLib.demoapp.ui.activities.DropBoxUpload;
import com.v2soft.V2AndLib.demoapp.ui.activities.EndlessListActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.GCPDemo;
import com.v2soft.V2AndLib.demoapp.ui.activities.OpenSLSample;
import com.v2soft.V2AndLib.demoapp.ui.activities.TaskStartStopActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.UDPDiscoveryList;

/**
 * Demo start activity
 * @author vshcryabets@gmail.com
 *
 */
public class V2DemoActivity 
extends ListActivity 
implements OnItemClickListener {
    private final static Class<?>[] sActivities = new Class[]{
        BluetoothList.class,
        UDPDiscoveryList.class,
        DropBoxUpload.class,
        OpenSLSample.class,
        GCPDemo.class,
        EndlessListActivity.class,
        DemoBackStack.class,
        TaskStartStopActivity.class
    };
    private static final String LOG_TAG = V2DemoActivity.class.getSimpleName();

    private ArrayAdapter<String> mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final List<String> names= new ArrayList<String>();
        for (Class<?> activity : sActivities) {
            try {
                final Method method = activity.getMethod("getSampleName");
                names.add( (String) method.invoke(null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        startActivity(new Intent(this, sActivities[arg2]));
    }
}