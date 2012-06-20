package com.v2soft.V2AndLib.demoapp.ui.fragments;

import java.util.List;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.bluetooth.BluetoothDevice;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.ui.loaders.BluetoothScannerLoader;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class BluetoothDeviceList 
extends BaseFragment<DemoApplication, DemoAppSettings> 
implements LoaderCallbacks<List<BluetoothDevice>>{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);
        return view;
    }
    
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
    }

    public static Fragment newInstance() {
        return new BluetoothDeviceList();
    }
    // ===================================================================
    // Loader callback
    // ===================================================================

    @Override
    public Loader<List<BluetoothDevice>> onCreateLoader(int id, Bundle args) {
        return new BluetoothScannerLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BluetoothDevice>> arg0, List<BluetoothDevice> arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLoaderReset(Loader<List<BluetoothDevice>> arg0) {
        // TODO Auto-generated method stub
        
    }

}
