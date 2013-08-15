/*
 * Copyright (C) 2012-2013 V.Shcryabets (vshcryabets@gmail.com)
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

/**
 * Abstract data request class
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class AbstractDataRequest<ResultType, Params, RawData> implements Serializable {
    private static final long serialVersionUID = 1L;

    public interface AbstractDataRequestCallback<R> {
        void onDataReady(R result);
    }

    transient protected AbstractDataRequestCallback<ResultType> mCallback;
    protected ResultType mData;

    public AbstractDataRequest() {

    }
    public AbstractDataRequest(AbstractDataRequestCallback<ResultType> callback) {
        mCallback = callback;
    }

    /**
     * Execute request.
     * @throws AbstractDataRequestException
     */
    public AbstractDataRequest<ResultType, Params, RawData> execute() throws AbstractDataRequestException {
        final Params params = prepareParameters();
        final RawData rawData = sendRequest(params);
        final ResultType res = parseResult(rawData);
        deliveryResult(res);
        return this;
    }

    /**
     * Delivery data
     * @param data
     */
    protected void deliveryResult(ResultType data) {
        mData = data;
        if ( mCallback != null ) {
            mCallback.onDataReady(data);
        }
    }

    protected abstract ResultType parseResult(RawData data) throws AbstractDataRequestException;
    protected abstract RawData sendRequest(Params p) throws AbstractDataRequestException;
    protected abstract Params prepareParameters() throws AbstractDataRequestException;
    /**
     * Get result of the data request.
     * @return result of the data request.
     */
    public ResultType getResult() {
        return mData;
    }
}
