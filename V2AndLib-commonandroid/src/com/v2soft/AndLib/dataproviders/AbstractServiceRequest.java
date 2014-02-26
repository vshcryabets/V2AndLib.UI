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

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <ResultType> result datat type
 * @param <Params> parameters data type
 * @param <RawData>
 */
public abstract class AbstractServiceRequest<ResultType extends Serializable, Params, RawData>
    extends AbstractDataRequest<ResultType, Params, RawData> {
    private static final long serialVersionUID = 1L;
    public static final String EXTRA_TASK = "com.v2soft.AndLib.dataproviders.TASK";
    public static final String EXTRA_EXCEPTION = "com.v2soft.AndLib.dataproviders.EXCEPTION";
    //===============================================================
    // Fields
    //===============================================================
    transient protected Context mContext;

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
}
