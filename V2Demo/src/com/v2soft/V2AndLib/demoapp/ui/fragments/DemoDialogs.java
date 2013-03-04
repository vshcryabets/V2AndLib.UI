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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.v2soft.AndLib.ui.dialogs.TimePickerDialogFragment;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoDialogs 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private static final int DLG_SELECT_TIME = 1;
    private static final String LOG_TAG = DemoDialogs.class.getSimpleName();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
    private TextView mSelectedTime;
    private Date mTime, mDate;

    public static Fragment newInstance() {
        return new DemoDialogs();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dialogs_demo, null);
        registerOnClickListener(new int[]{R.id.btnSelectTime}, view);
        mSelectedTime = (TextView) view.findViewById(R.id.txtTime);
        mTime = new Date();
        mDate = new Date();
        mSelectedTime.setText(SDF.format(mTime));
        return view;
    }
    
    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
        case R.id.btnSelectTime:
            final TimePickerDialogFragment timeDlg = TimePickerDialogFragment.newInstance(mTime, false, null);
            timeDlg.setTargetFragment(this, DLG_SELECT_TIME);
            timeDlg.show(getFragmentManager(), LOG_TAG);
            break;

        default:
            break;
        }
    }
    
    @Override
    public void onFragmentResult(Object data, int requestCode) {
        if ( requestCode == DLG_SELECT_TIME ) {
            mTime = (Date) data;
            mSelectedTime.setText(SDF.format(mTime));
        }
        super.onFragmentResult(data, requestCode);
    }

}
