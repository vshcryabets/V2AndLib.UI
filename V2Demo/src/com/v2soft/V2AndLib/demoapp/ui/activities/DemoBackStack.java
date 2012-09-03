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

import com.v2soft.AndLib.ui.R;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.ui.fragments.DemoBluetoothDeviceList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoBackStack extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2andlib_fragment_tabs);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        if ( savedInstanceState == null ) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.v2andLibFragment, newInstance("1"));
            ft.commit();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        FragmentTransaction ft;
        switch (id) {
        case R.id.btn1:
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.v2andLibFragment, newInstance("1"));
            ft.commit();
            break;
        case R.id.btn2:
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.v2andLibFragment, newInstance("2"));
            ft.commit();

            break;
        case R.id.btn3:
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.v2andLibFragment, newInstance("3"));
            ft.commit();

            break;
        case R.id.btn4:
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.v2andLibFragment, newInstance("4"));
            ft.commit();

            break;
        default:
            break;
        }
        // TODO Auto-generated method stub

    }

    public Fragment newInstance(String text) {
        final Fragment result = new TextFragment();
        final Bundle args = new Bundle();
        args.putString(TextFragment.KEY_STR, text);
        result.setArguments(args);
        return result;
    }

    private class TextFragment 
    extends BaseFragment<DemoApplication, DemoAppSettings> {
        private static final String KEY_STR = "str";
        private String mText;

        public Fragment newInstance(String text) {
            final Fragment result = new TextFragment();
            final Bundle args = new Bundle();
            args.putString(KEY_STR, text);
            result.setArguments(args);
            return result;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mText = getArguments().getString(KEY_STR);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_button, null);
            final Button btn = (Button) view.findViewById(R.id.btnStart);
            btn.setOnClickListener(this);
            btn.setText(mText);
            return view;
        }
        @Override
        public void onClick(View v) {
            startFragment(R.id.v2andLibFragment, 
                    newInstance(mText+"+"), 
                    true, 
                    "");
        }

    }
}
