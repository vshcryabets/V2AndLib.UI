package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.Fragment;
import android.view.View;

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
        // TODO Auto-generated method stub
        
    }
}
