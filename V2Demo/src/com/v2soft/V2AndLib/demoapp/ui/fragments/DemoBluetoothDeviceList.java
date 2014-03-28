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

import android.bluetooth.BluetoothDevice;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.AndLib.ui.fragments.BluetoothDeviceList;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoBluetoothDeviceList 
extends BluetoothDeviceList<DemoApplication, DemoAppSettings>  {

    
    public static Fragment newInstance() {
        return new DemoBluetoothDeviceList();
    }
    
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        BluetoothDevice device = (BluetoothDevice) mAdapter.getItem(position);
        startFragment(R.id.v2andLibFragment, 
                DemoBluetoothTerminal.newInstance(device), true, 
                DemoBluetoothDeviceList.class.getSimpleName(), 
                -1, -1, -1, -1);
    }
}
