/*
 * Copyright (C) 2012-2014 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.V2AndLib.demoapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.v2soft.V2AndLib.demoapp.ui.fragments.SamplesList;

/**
 * Demo start activity
 * @author vshcryabets@gmail.com
 *
 */
public class V2DemoActivity extends SherlockFragmentActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
		if ( savedInstanceState == null ) {
			final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.content, SamplesList.newInstance());
			transaction.commit();
		}
    }

}