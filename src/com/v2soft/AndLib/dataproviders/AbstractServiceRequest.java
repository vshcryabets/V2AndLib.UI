package com.v2soft.AndLib.dataproviders;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;

import com.v2soft.AndLib.dataproviders.AbstractDataRequest;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <R>
 * @param <Params>
 * @param <RawData>
 */
public abstract class AbstractServiceRequest<R, Params, RawData> extends AbstractDataRequest<R, Params, RawData> implements ITask{
    private static final long serialVersionUID = 1L;
    public static final String EXTRA_TASK = "com.v2soft.AndLib.dataproviders.TASK";
    public static final String EXTRA_EXCEPTION = "com.v2soft.AndLib.dataproviders.EXCEPTION";
    //===============================================================
    // Fields
    //===============================================================
    transient protected Context mContext;
    protected int mTaskId, mTaskTag;
    private Serializable mTagObj;
    
    public AbstractServiceRequest(Context context) {
        super();
        mContext = context;
    }
    
    public abstract String getResultAction();
    
    
    public void startAtService() {
        Intent intent = new Intent(mContext, getServiceClass());
        intent.setAction(getServiceAction());
        intent.putExtra(EXTRA_TASK, this);
        mContext.startService(intent);
    }
    protected abstract String getServiceAction();
    protected abstract Class<?> getServiceClass();

    public void setContext(Context context) {
        mContext = context;
    }
    // ========================================================
    // ITask interface
    // ========================================================
    @Override
    public int getTaskId() {
        return mTaskId;
    }
    @Override
    public void setTaskId(int id) {
        mTaskId = id;
    }
    @Override
    public int getTaskTag() {
        return mTaskTag;
    }
    @Override
    public ITask setTaskTag(int id) {
        mTaskTag = id;
        return this;
    }
    @Override
    public Serializable getTaskTagObject() {
        return mTagObj;
    }
    @Override
    public ITask setTaskTagObject(Serializable tag) {
        mTagObj = tag;
        return this;
    }

    @Override
    public void execute(ITaskSimpleListener handler) throws Exception {
        execute();
    }

    @Override
    public void cancelTask() {
    }
}
