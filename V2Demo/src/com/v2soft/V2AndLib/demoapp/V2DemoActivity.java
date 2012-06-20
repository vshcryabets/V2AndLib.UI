package com.v2soft.V2AndLib.demoapp;

import com.v2soft.V2AndLib.demoapp.ui.activities.BluetoothList;

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
    private final static String [] sItems = new String[]{"Bluetooth device list"};
    private static final String LOG_TAG = V2DemoActivity.class.getSimpleName();
    private static final int ITEM_BLUETOOTH_DEVICE = 0;
    
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

        default:
            break;
        }
        Log.d(LOG_TAG, "item="+arg3);
    }
}