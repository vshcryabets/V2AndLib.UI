package com.v2soft.V2AndLib.demoapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.v2soft.AndLib.UI.R;
import com.v2soft.V2AndLib.demoapp.ui.fragments.DemoBluetoothDeviceList;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class BluetoothList extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2andlib_single_fragment);
        if ( savedInstanceState == null ) {
            Fragment fragment = DemoBluetoothDeviceList.newInstance();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.v2andLibFragment, fragment);
            trans.commit();
        }
    }
}
