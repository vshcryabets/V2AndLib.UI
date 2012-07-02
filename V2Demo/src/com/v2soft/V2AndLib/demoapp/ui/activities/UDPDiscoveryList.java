package com.v2soft.V2AndLib.demoapp.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.v2soft.AndLib.UI.R;
import com.v2soft.V2AndLib.demoapp.ui.fragments.DemoUDPDiscoveryList;

/**
 * Activity with list of discovered UDP clients
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class UDPDiscoveryList 
extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2andlib_single_fragment);
        if ( savedInstanceState == null ) {
            Fragment fragment = DemoUDPDiscoveryList.newInstance();
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.v2andLibFragment, fragment);
            trans.commit();
        }
    }
}
