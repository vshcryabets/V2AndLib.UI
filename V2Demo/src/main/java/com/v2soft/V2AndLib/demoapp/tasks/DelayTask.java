package com.v2soft.V2AndLib.demoapp.tasks;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;
import com.v2soft.V2AndLib.demoapp.services.DemoExecutionService;
import com.v2soft.V2AndLib.demoapp.services.ServiceConstants;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DelayTask extends AbstractServiceRequest<Integer, Void, Void> {
    protected int mDelay;
    protected int mProgress;

    public DelayTask(Context context, int delay) {
        super(context);
        mDelay = delay;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_RESULT;
    }

    @Override
    protected String getServiceAction() {
        return ServiceConstants.ACTION_RECIVE;
    }

    @Override
    protected Class<?> getServiceClass() {
        return DemoExecutionService.class;
    }

    @Override
    protected Integer parseResult(Void aVoid) throws AbstractDataRequestException {
        return null;
    }

    @Override
    protected Void sendRequest(Void p) throws AbstractDataRequestException {
        mProgress = 0;
        int delayCnt = mDelay;
        while ( !isCanceled() && delayCnt -- > 0 ) {
            mProgress ++;
            sendProgress();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void sendProgress() {
        Intent intent = new Intent(ServiceConstants.ACTION_PROGRESS);
        intent.putExtra(ServiceConstants.EXTRA_PROGRESS, getProgress());
        intent.putExtra(ServiceConstants.EXTRA_TASK_TAG, getTaskTagObject());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private int getProgress() {
        return mProgress;
    }

    @Override
    protected Void prepareParameters() throws AbstractDataRequestException {
        return null;
    }
}
