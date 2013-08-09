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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.v2soft.AndLib.tricks.DataConnection;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * System tricks demo fragment.
 * @author vshcryabets@gmail.com
 *
 */
public class DemoTricks 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private static final String LOG_TAG = DemoTricks.class.getSimpleName();
    private CheckBox mEnableMobileData;

    public static Fragment newInstance() {
        return new DemoTricks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tricks, null);
        mEnableMobileData = (CheckBox) view.findViewById(R.id.chkEnableData);
        mEnableMobileData.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
        case R.id.chkEnableData:
            boolean value = mEnableMobileData.isChecked();
            try {
                DataConnection.setMobileDataEnabled(getActivity(), value);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }
            break;

        default:
            break;
        }
    }
}
