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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.v2soft.AndLib.ui.R;
import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.AndLib.ui.fragments.TabsFragmentBackStack;
import com.v2soft.AndLib.ui.fragments.TabsFragmentBackStack.TabsFragmentBackStackListener;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoBackStack extends BaseActivity<DemoApplication, DemoAppSettings> 
implements OnClickListener {
    private static final String TAB_A = "A";
    private static final String TAB_B = "B";
    private static final String TAB_C = "C";
    private static final String TAB_D = "D";
    private TabsFragmentBackStack mStack ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2andlib_fragment_tabs);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        mStack = new TabsFragmentBackStack(mFragmentsListener);
        if ( savedInstanceState == null ) {
            mStack.activateTab(TAB_A);
        }
    }

    private TabsFragmentBackStackListener mFragmentsListener = new TabsFragmentBackStackListener() {
        @Override
        public Fragment onNewTabOpened(String tag) {
            if ( tag.equals(TAB_A)) {
                return newInstance(TAB_A,1);
            } else if ( tag.equals(TAB_B)) {
                return newInstance(TAB_B,1);
            } else if ( tag.equals(TAB_C)) {
                return newInstance(TAB_C,1);
            } else if ( tag.equals(TAB_D)) {
                return newInstance(TAB_D,1);
            }
            return null;
        }

        @Override
        public void onStartFragment(Fragment fragment) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.v2andLibFragment, fragment);
            ft.commit();
        }

        @Override
        public void onActivatedTab(String tag) {
            findViewById(R.id.btn1).setBackgroundColor(0xFF000090);
            findViewById(R.id.btn2).setBackgroundColor(0xFF000090);
            findViewById(R.id.btn3).setBackgroundColor(0xFF000090);
            findViewById(R.id.btn4).setBackgroundColor(0xFF000090);
            if ( tag.equals(TAB_A)) {
                findViewById(R.id.btn1).setBackgroundColor(0xFF009000);
            } else if ( tag.equals(TAB_B)) {
                findViewById(R.id.btn2).setBackgroundColor(0xFF009000);
            } else if ( tag.equals(TAB_C)) {
                findViewById(R.id.btn3).setBackgroundColor(0xFF009000);
            } else if ( tag.equals(TAB_D)) {
                findViewById(R.id.btn4).setBackgroundColor(0xFF009000);
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        FragmentTransaction ft;
        switch (id) {
        case R.id.btn1:
            mStack.activateTab(TAB_A);
            break;
        case R.id.btn2:
            mStack.activateTab(TAB_B);
            break;
        case R.id.btn3:
            mStack.activateTab(TAB_C);
            break;
        case R.id.btn4:
            mStack.activateTab(TAB_D);
            break;
        default:
            break;
        }
    }

    @Override
    public void onBackPressed() {
        if ( !mStack.onBackPressed() ) {
            super.onBackPressed();
        }
    }

    public Fragment newInstance(String text, int id) {
        final Fragment result = new TextFragment();
        final Bundle args = new Bundle();
        args.putString(TextFragment.KEY_STR, text);
        args.putInt(TextFragment.KEY_ID, id);
        result.setArguments(args);
        return result;
    }

    private class TextFragment 
    extends BaseFragment<DemoApplication, DemoAppSettings> {
        public static final String KEY_ID = "id";
        private static final String KEY_STR = "str";
        private String mTag;
        private int mId;

        public Fragment newInstance(String text, int id) {
            final Fragment result = new TextFragment();
            final Bundle args = new Bundle();
            args.putString(KEY_STR, text);
            args.putInt(KEY_ID, id);
            result.setArguments(args);
            return result;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mTag = getArguments().getString(KEY_STR);
            mId = getArguments().getInt(KEY_ID);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_button, null);
            final Button btn = (Button) view.findViewById(R.id.btnStart);
            btn.setOnClickListener(this);
            btn.setText(mTag+"."+mId);
            return view;
        }
        @Override
        public void onClick(View v) {
            Fragment fragment = null;
            if ( mId > 4 ) {
                String next = TAB_A;
                if ( mTag.equals(TAB_A)) {
                    next = TAB_B;
                } else if ( mTag.equals(TAB_B)) {
                    next = TAB_C;
                } else if ( mTag.equals(TAB_C)) {
                    next = TAB_D;
                }
                fragment = newInstance(next, 1);
                mStack.startFragment(fragment, next);
            } else {
                fragment = newInstance(mTag, ++mId);
                mStack.startFragmentInCurrentTab(fragment);
            }

        }
    }
}
