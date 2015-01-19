/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.ui.fragments.DemoTricks;

/**
 * Demonstration for library tricks.
 * @author vshcryabets@gmail.com
 *
 */
public class TricksActivity extends DemoBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2andlib_single_fragment);
        if ( savedInstanceState == null ) {
            Fragment fragment = DemoTricks.newInstance();
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.v2andLibFragment, fragment);
            trans.commit();
        }
    }
    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "System tricks demo";
    }
}