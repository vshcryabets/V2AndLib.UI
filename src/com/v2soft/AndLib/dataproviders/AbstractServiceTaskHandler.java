/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/***
 *
 * @author V.Shcryabets<vshcryabets@gmail.com>
 * 
 */
public abstract class AbstractServiceTaskHandler extends Handler {
    private static final String LOG_TAG = AbstractServiceTaskHandler.class.getSimpleName();
    protected Service mService;
    protected String mHandledAction;
    protected String mErrorAction;

    /**
     * 
     * @param looper
     * @param context service object
     * @param handledAction 
     * @param errorAction action that used to notify about error in task
     */
    public AbstractServiceTaskHandler(Looper looper, Service context, String handledAction, String errorAction) {
        super(looper);
        mService = context;
        mHandledAction = handledAction;
        mErrorAction = errorAction;
    }

    @Override
    public void handleMessage(Message msg) {
        final Intent intent = (Intent) msg.obj;
        if ( intent == null ) {
            super.handleMessage(msg);
            return;
        }
        if (mHandledAction.equals(intent.getAction())) {
            final AbstractServiceRequest<?,?,?> task = 
                    (AbstractServiceRequest<?,?,?>) intent.getSerializableExtra(AbstractServiceRequest.EXTRA_TASK);
            task.setContext(mService);
            try {
                task.execute(null);
                sendResult(task);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
                sendErrorMessage(task, e);
            }
        }
        super.handleMessage(msg);
        if ( shouldStop() ) {
            mService.stopSelf(msg.arg1);
        }
    }

    /**
     * Sends result of a request execution
     * @param task
     */
    protected void sendResult(AbstractServiceRequest<?, ?, ?> task) {
        final String action = task.getResultAction();
        if ( action == null ) {
            return;
        }
        final Intent intent = new Intent(action);
        intent.putExtra(AbstractServiceRequest.EXTRA_TASK, task);
        LocalBroadcastManager.getInstance(mService).sendBroadcast(intent);
    }

    /**
     * Send exception
     * @param task
     * @param e
     */
    protected void sendErrorMessage(AbstractServiceRequest<?,?,?> task, Exception e){
        final Intent intent = new Intent(mErrorAction);
        intent.putExtra(AbstractServiceRequest.EXTRA_TASK, task);
        intent.putExtra(AbstractServiceRequest.EXTRA_EXCEPTION, e.toString());
        LocalBroadcastManager.getInstance(mService).sendBroadcast(intent);
    }

    /**
     * Stop service after intent processing
     * @return
     */
    protected abstract boolean shouldStop();
}
