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
package com.v2soft.AndLib.dataproviders;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Asynchronious executor that periodically start specified task
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 * 
 */
public class AsyncTaskExecutor<T extends  ITask> extends AsyncTask<T, Object, T> implements ITaskSimpleListener {
    private static final int DEFAULT_DELAY = 30*1000; // 30 seconds 
    private static final String LOG_TAG = AsyncTaskExecutor.class.getSimpleName();
    private boolean isWorking;
    private int mDelay = DEFAULT_DELAY;
    protected ITaskListener<T,?> mListener;
	private int mRepeatCount = Integer.MIN_VALUE;

	/**
	 * Start task one time.
	 */
	public AsyncTaskExecutor() {
		mRepeatCount = 1;
	}

    public AsyncTaskExecutor(T task, ITaskListener<T,?> listener) {
        if ( listener == null ) {
            throw new NullPointerException("Listener can't be null");
        }
        mListener = listener;
    }

    @Override
    protected T doInBackground(T... params) {
		T task = params[0];
        isWorking = true;
		int repeatCount = mRepeatCount;
        while ( isWorking && repeatCount != 0 ) {
            try {
                if ( task != null ) {
                    task.execute(this);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }
			repeatCount--;
			if ( repeatCount > 0 ) {
				if ( isWorking ) {
					try {
						Thread.sleep(mDelay);
					} catch (InterruptedException e) {
					}
				}
			}
        }
        return task;
    }
    
    public void stop() {
        isWorking = false;
    }

	@Override
	public void onMessageFromTask(ITask task, Object message) {
		publishProgress(message);
	}
}

