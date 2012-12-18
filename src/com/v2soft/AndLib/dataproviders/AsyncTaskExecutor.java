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
package com.v2soft.AndLib.dataproviders;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Asynchronious executor that periodically start specified task
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 * 
 */
public class AsyncTaskExecutor extends AsyncTask<Void, Void, Void> implements ITaskHub {
    private static final int DEFAULT_DELAY = 30*1000; // 30 seconds 
    private boolean isWorking;
    private int mDelay = DEFAULT_DELAY;
    protected ITaskListener mListener;
    protected ITask mTask = null;
    private int mTaskId = 0;
    private Handler mHandler;

    public AsyncTaskExecutor(ITaskListener listener, Handler handler) {
        if ( listener == null ) {
            throw new NullPointerException("Listener can't be null");
        }
        if ( handler == null ) {
            throw new NullPointerException("Handler can't be null");
        }
        mHandler = handler;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        isWorking = true;
        while ( isWorking ) {
            try {
                if ( mTask != null ) {
                    mTask.execute(mListener);
                }
            } catch (Exception e) {
            }
            if ( isWorking ) {
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                }
            }
        }
        return null;
    }
    
    public void stop() {
        isWorking = false;
    }
    // ===============================================================
    // ITaskHub routines
    // ===============================================================
    @Override
    public int addTask(ITask task, ITaskListener listener) {
        task.setTaskId(mTaskId++);
        mTask = task;
        return mTask.getTaskId();
    }

    @Override
    public void attachToTasks(ITaskListener listener, int[] taskIds) {
        return;
    }

    @Override
    public boolean cancelTask(ITaskListener listener, ITask taskId,
            boolean stopIfRunning) {
        if ( taskId.equals(mTask)) {
            mTask.cancelTask();
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelTask(ITaskListener listener, int taskId,
            boolean stopIfRunning) {
        return false;
    }

    @Override
    public int[] detachFromTasks(ITaskListener listener) {
        return new int[]{};
    }

    @Override
    public void cancelAllTasksByListener(ITaskListener listener,
            boolean stopIfRunning) {
        if (mTask != null ) {
            mTask.cancelTask();
        }
    }

    @Override
    public void onMessageFromTask(final ITask task, final Message message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onMessageFromTask(task, message);
            }
        });
        
    }
}

