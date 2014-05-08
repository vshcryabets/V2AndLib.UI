/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.v2soft.AndLib.application.BaseInjector;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.ui.activities.BluetoothList;
import com.v2soft.V2AndLib.demoapp.ui.activities.CameraActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.DialogsActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.EndlessListActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.GCPDemo;
import com.v2soft.V2AndLib.demoapp.ui.activities.NavigationDrawerActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.OpenSLSample;
import com.v2soft.V2AndLib.demoapp.ui.activities.TricksActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.UDPDiscoveryList;
import com.v2soft.V2AndLib.demoapp.ui.activities.WiFiList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that contains list of samples.
 * @author vshcryabets@gmail.com
 *
 */
public class SamplesList
		extends BaseFragment<DemoApplication, DemoAppSettings>
		implements AdapterView.OnItemClickListener {
	private final static Class<?>[] sActivities = new Class[]{
			BluetoothList.class,
			CameraActivity.class,
			UDPDiscoveryList.class,
			OpenSLSample.class,
			GCPDemo.class,
			EndlessListActivity.class,
			DialogsActivity.class,
			WiFiList.class,
			TricksActivity.class,
			NavigationDrawerActivity.class,
			SynchronizationFragment.class,
			OpenHelpersFragment.class,
            AudioStreamClientFragment.class
	};
	private static final String TAG = SamplesList.class.getSimpleName();

	private ArrayAdapter<String> mAdapter;

	public static Fragment newInstance() {
		return new SamplesList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseInjector.getInstance().inject(this);
		View view = View.inflate(getActivity(), R.layout.fragment_samples_list, null);
		ListView list = (ListView) view.findViewById(android.R.id.list);
		final List<String> names= new ArrayList<String>();
		for (Class<?> activity : sActivities) {
			try {
				final Method method = activity.getMethod("getSampleName");
				names.add( (String) method.invoke(null));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Class<?> clazz = sActivities[arg2];
		if ( Activity.class.isAssignableFrom(clazz) ) {
			startActivity(new Intent(getActivity(), sActivities[arg2]));
		} else {
			if ( clazz.equals(SynchronizationFragment.class)) {
				startFragment(R.id.content, SynchronizationFragment.newInstance(),
						true, TAG, 0, 0, 0, 0);
			} else if ( clazz.equals(OpenHelpersFragment.class)) {
				startFragment(R.id.content, OpenHelpersFragment.newInstance(),
						true, TAG, 0, 0, 0, 0);
			} else if ( clazz.equals(AudioStreamClientFragment.class)) {
                startFragment(R.id.content, AudioStreamClientFragment.newInstance(),
                        true, TAG, 0, 0, 0, 0);
            }
		}
	}
}
