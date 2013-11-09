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
 * @param <ResultType> result datat type
 * @param <Params> parameters data type
 * @param <RawData>
 */
public abstract class AbstractServiceRequest<ResultType, Params, RawData> 
    extends AbstractDataRequest<ResultType, Params, RawData> 
    implements ITask{
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
    protected abstract String getServiceAction();
    protected abstract Class<?> getServiceClass();
    
    public void startAtService() {
        if ( mContext == null ) {
            throw new NullPointerException("Context is null");
        }
        Intent intent = new Intent(mContext, getServiceClass());
        intent.setAction(getServiceAction());
        intent.putExtra(EXTRA_TASK, this);
        mContext.startService(intent);
    }

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
    public void execute(ITaskSimpleListener handler) throws AbstractDataRequestException {
        execute();
    }

    @Override
    public void cancelTask() {
    }
}
