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
package com.v2soft.V2AndLib.demoapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.v2soft.AndLib.ui.R;
import com.v2soft.V2AndLib.demoapp.ui.fragments.DemoUDPDiscoveryList;

/**
 * Activity with list of discovered UDP clients
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class UDPDiscoveryList extends FragmentActivity {
    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "UDP discovery sample";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2andlib_single_fragment);
        if ( savedInstanceState == null ) {
            Fragment fragment = DemoUDPDiscoveryList.newInstance();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.v2andLibFragment, fragment);
            trans.commit();
        }
    }
}
