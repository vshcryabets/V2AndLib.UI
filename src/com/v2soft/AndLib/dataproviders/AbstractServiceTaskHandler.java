package com.v2soft.AndLib.dataproviders;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;
import android.os.Message;
import android.util.Log;

/***
 * Класс для получениия данных пользователя.
 * @author Eugen Oleynik jinoleynik@gmail.com
 * 
 */
public class AbstractServiceTaskHandler extends Handler {
    private static final String LOG_TAG = AbstractServiceTaskHandler.class.getSimpleName();
    private Context mContext;
    protected String mHandledAction;
    protected String mErrorAction;

    public AbstractServiceTaskHandler(Looper looper, Context context, String handledAction, String errorAction) {
        super(looper);
        mContext = context;
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
            task.setContext(mContext);
            try {
                task.execute(null);
                sendResult(task);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
                sendErrorMessage(task, e);
            }
        }
        super.handleMessage(msg);
    }

    protected void sendResult(AbstractServiceRequest<?, ?, ?> task) {
        final String action = task.getResultAction();
        if ( action == null ) {
            return;
        }
        final Intent intent = new Intent(action);
        intent.putExtra(AbstractServiceRequest.EXTRA_TASK, task);
        mContext.sendOrderedBroadcast(intent, null);
    }

    protected void sendErrorMessage(AbstractServiceRequest<?,?,?> task, Exception e){
        final Intent intent = new Intent(mErrorAction);
        intent.putExtra(AbstractServiceRequest.EXTRA_TASK, task);
        intent.putExtra(AbstractServiceRequest.EXTRA_EXCEPTION, e.toString());
        mContext.sendOrderedBroadcast(intent, null);
    }
}
