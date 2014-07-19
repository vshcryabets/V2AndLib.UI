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

import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2soft.AndLib.media.CustomizableMediaPlayer;
import com.v2soft.AndLib.media.CustomizableMediaPlayerListener;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.services.ServiceConstants;
import com.v2soft.V2AndLib.demoapp.tasks.DelayTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Audio-Video streaming client sample.
 * @author vshcryabets@gmail.com
 *
 */
public class BackgroundTasksFragment
		extends BaseFragment<DemoApplication, DemoAppSettings> {

    protected LinearLayout mContainer;
    protected EditText mNumberOfTasks;
    protected EditText mTaskDuration;
    protected Map<Integer, TextView> mTextViews;

    public static Fragment newInstance() {
		return new BackgroundTasksFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTextViews = new HashMap<Integer, TextView>();
		View view = View.inflate(getActivity(), R.layout.fragment_background_tasks, null);
        mContainer = (LinearLayout) view.findViewById(R.id.container);
        mNumberOfTasks = (EditText) view.findViewById(R.id.number_of_tasks);
        mTaskDuration = (EditText) view.findViewById(R.id.duration_of_task);
		registerOnClickListener(new int[]{R.id.btnStart, R.id.btnCancelAll}, view);
		return view;
	}

	@Override
	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                mTextViews.clear();
                mContainer.removeAllViews();
                int numberOfTasks = Integer.parseInt(mNumberOfTasks.getEditableText().toString());
                int taskDuration = Integer.parseInt(mTaskDuration.getEditableText().toString());
                for ( int i = 0; i < numberOfTasks; i++ ) {
                    DelayTask task = new DelayTask(getActivity(), taskDuration);
                    TextView view = new TextView(getActivity());
                    mTextViews.put(i, view);
                    mContainer.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    task.setTaskTagObject(i);
                    task.startAtService();
                    updateProgress(i, 0);
                }
                break;
            case R.id.btnCancelAll:
                break;
        }
	}

    private void updateProgress(int tag, int progress) {
        if ( !mTextViews.containsKey(tag)) {
            return;
        }
        TextView view = mTextViews.get(tag);
        view.setText(String.format("Progress %d.", progress));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save tags & ids
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceConstants.ACTION_PROGRESS);
        filter.addAction(ServiceConstants.ACTION_RESULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ( ServiceConstants.ACTION_PROGRESS.equals(action)) {
                Integer tag = (Integer) intent.getSerializableExtra(ServiceConstants.EXTRA_TASK_TAG);
                int progress = intent.getIntExtra(ServiceConstants.EXTRA_PROGRESS, -1);
                updateProgress(tag, progress);
            }
        }
    };

    /**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Background tasks demo";
	}
}
