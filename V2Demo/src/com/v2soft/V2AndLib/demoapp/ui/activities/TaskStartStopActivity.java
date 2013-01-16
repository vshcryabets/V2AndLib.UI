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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskListener;
import com.v2soft.AndLib.dataproviders.TasksMultiplexor;
import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.tasks.StartStopSampleTask;

/**
 * Task sample
 * @author vshcryabets@gmail.com
 *
 */
public class TaskStartStopActivity 
    extends BaseActivity<DemoApplication, DemoAppSettings> 
    implements ITaskListener, OnClickListener {
    private static final String LOG_TAG = TaskStartStopActivity.class.getSimpleName()
            ;
    private TasksMultiplexor mMultiplexor;
    private TextView mTxtData;
    private ITask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_startstop);
        mMultiplexor = new TasksMultiplexor(new Handler());
        mTxtData = (TextView) findViewById(R.id.txtData);
        findViewById(R.id.btnStartTaskA).setOnClickListener(this);
        findViewById(R.id.btnStartTaskB).setOnClickListener(this);
        findViewById(R.id.btnStopTaskA).setOnClickListener(this);
        findViewById(R.id.btnStopTaskB).setOnClickListener(this);
        // TODO fix this later
        findViewById(R.id.btnStartTaskB).setVisibility(View.GONE);
        findViewById(R.id.btnStopTaskB).setVisibility(View.GONE);
    }

    public static String getSampleName() {
        return "Task start stop sample";
    }

    @Override
    public void showError(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLoadingProcess(boolean value, Object tag) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlockingProcess(boolean value, Object tag) {
        // TODO Auto-generated method stub
        
    }
    // ==========================================================
    // Task listener
    // ==========================================================
    @Override
    public void onMessageFromTask(ITask task, Message message) {
        if ( message.what == StartStopSampleTask.TASK_MESSAGE_NEWTIME) {
            mTxtData.setText(message.obj.toString());
        }
    }

    @Override
    public void onTaskFinished(ITask task) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTaskFailed(ITask task, Throwable error) {
        Log.d(LOG_TAG, error.toString(), error);
    }
    // ==========================================================
    // OnClick listener
    // ==========================================================
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnStartTaskA:
            mTask = new StartStopSampleTask();
            mMultiplexor.addTask(mTask, this);
            break;
        case R.id.btnStopTaskA:
            mMultiplexor.cancelTask(this, mTask, false);
            break;
        default:
            break;
        }
    }
}
