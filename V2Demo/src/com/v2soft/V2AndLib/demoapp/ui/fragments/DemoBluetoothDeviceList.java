package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;

import com.v2soft.AndLib.UI.R;
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
        Fragment fragment = DemoBluetoothTerminal.newInstance(device);
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.v2andLibFragment, fragment);
        trans.addToBackStack(DemoBluetoothDeviceList.class.getSimpleName());
        trans.commit();
    }
}
