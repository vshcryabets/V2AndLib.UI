package com.v2soft.AndLib.dataproviders;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.AsyncTaskLoader;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class TaskExecutorLoader extends AsyncTaskLoader<Void> implements ITaskSimpleListener{
    protected ITaskListener mListener;
    protected ITask mTask;
    protected Handler mHandler;

    public TaskExecutorLoader(Context context, Handler handler, ITaskListener listener, ITask task) {
        super(context);
        mHandler = handler;
        mListener = listener;
        setTask(task);
    }

    @Override
    public Void loadInBackground() {
        if ( mTask != null ) {
            try {
                mTask.execute(this);
            } catch (Exception e) {
                final Message msg = new Message();
                msg.what = ITask.MESSAGE_TASK_EXCEPTION;
                msg.obj = e;
                onMessageFromTask(mTask, msg);
                mListener.onTaskFailed(mTask, e);
                return null;
            }
            final Message msg = new Message();
            msg.what = ITask.MESSAGE_TASK_FINISHED_SUCCESS;
            onMessageFromTask(mTask, msg);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onTaskFinished(mTask);
                }
            });
        }
        return null;
    }

    public ITask getTask() {
        return mTask;
    }

    public void setTask(ITask mTask) {
        this.mTask = mTask;
    }

    @Override
    public void onMessageFromTask(ITask task, final Object message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onMessageFromTask(mTask, message);
            }
        });
    }
}
