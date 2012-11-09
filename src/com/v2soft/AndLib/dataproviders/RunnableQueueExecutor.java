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
    private static final long REQUEST_POLL_TIMEOUT_MS = 250;
    private static final String LOG_TAG = RunnableQueueExecutor.class.getSimpleName();

    private boolean isWorking;
    private ArrayBlockingQueue<ITask> mRequests;
    private TasksMultiplexor mParent;
    private Object mSync;
    private ITask mCurrentTask;

    public RunnableQueueExecutor(String threadName, int maxTasks, TasksMultiplexor parent) {
        super(threadName);
        mRequests = new ArrayBlockingQueue<ITask>(maxTasks);
        mParent = parent;
        mSync = new Object();
    }

    @Override
    public void run() {
        isWorking = true;
        while ( isWorking ) {
            try {
                synchronized (mSync) {
                    mCurrentTask = mRequests.poll(REQUEST_POLL_TIMEOUT_MS, 
                            TimeUnit.MILLISECONDS);
                }
                if ( mCurrentTask != null ) {
                    try {
                        if ( !isInterrupted() ) { 
                            mCurrentTask.execute(mParent);
                        }
                        if ( !isInterrupted() ) {
                            mParent.handleFinished(mCurrentTask);
                        } else {
                            mParent.handleException(mCurrentTask, 
                                    new InterruptedException("Task interrupted (not successfull)"));
                        }
                    } catch (Exception e) {
                        mParent.handleException(mCurrentTask, e);
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

    /**
     * Cancel execution of the specified task
     * @param task
     * @param stopIfRunning stop task if it is already running
     * @return
     */
    public synchronized boolean cancelTask(ITask task, boolean stopIfRunning) {
        // check does this task are executing right now
        if ( task.equals(mCurrentTask)) {
            if ( stopIfRunning ) {
                this.interrupt();
                return true;
            } else {
                return false;
            }
        } else {
            // remove from poll
            return mRequests.remove(task);
        }
    }
}
