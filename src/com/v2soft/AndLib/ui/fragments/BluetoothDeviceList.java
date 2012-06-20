package com.v2soft.AndLib.ui.fragments;

import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.bluetooth.BluetoothDevice;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.ui.loaders.BluetoothScannerLoader;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public abstract class BluetoothDeviceList<T extends BaseApplication<S>, S extends BaseApplicationSettings> 
extends BaseFragment<T, S> 
implements LoaderCallbacks<List<BluetoothDevice>>{
    protected ArrayAdapter<BluetoothDevice> mAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(com.v2soft.AndLib.UI.R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), android.R.layout.simple_list_item_1);
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
    // Loader callback
    // ===================================================================

    @Override
    public Loader<List<BluetoothDevice>> onCreateLoader(int id, Bundle args) {
        return new BluetoothScannerLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BluetoothDevice>> arg0, List<BluetoothDevice> arg1) {
        mAdapter.clear();
        mAdapter.addAll(arg1);
    }

    @Override
    public void onLoaderReset(Loader<List<BluetoothDevice>> arg0) {
        mAdapter.clear();
    }
}
