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

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class RunnableQueueExecutor extends Thread {
    private static final long REQUEST_POLL_TIMEOUT_MS = 10000;
    private static final String LOG_TAG = RunnableQueueExecutor.class.getSimpleName();

    private boolean isWorking;
    private ArrayBlockingQueue<ITask> mRequests;
    private TasksMultiplexor mParent;

    public RunnableQueueExecutor(String threadName, int maxTasks, TasksMultiplexor parent) {
        super(threadName);
        mRequests = new ArrayBlockingQueue<ITask>(maxTasks);
        mParent = parent;
    }

    @Override
    public void run() {
        isWorking = true;
        while ( isWorking ) {
            try {
                final ITask task = mRequests.poll(REQUEST_POLL_TIMEOUT_MS, 
                        TimeUnit.MILLISECONDS);
                if ( task != null ) {
                    try {
                        task.execute();
                        mParent.handleFinished(task);
                    } catch (Exception e) {
                        mParent.handleException(task, e);
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }

        }
    }

    public void stopQueueProcessing() {
        isWorking = false;
    }

    /**
     * Add new task to processing queue
     * @param runnable
     * @return true if runnable was added, otherwise false
     */
    public boolean post(ITask runnable) {
        return mRequests.offer(runnable);
    }
}
