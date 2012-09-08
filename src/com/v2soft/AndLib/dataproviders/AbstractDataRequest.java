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

/**
 * Abstract data request class
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class AbstractDataRequest<R, Params, RawData> {
    public interface AbstractDataRequestCallback<R> {
        void onDataReady(R result);
    }

    protected AbstractDataRequestCallback<R> mCallback;
    protected R mData;
    protected Params mParams;

    public AbstractDataRequest(AbstractDataRequestCallback<R> callback) {
        mCallback = callback;
    }

    /**
     * Execute request
     * @throws AbstractDataRequestException 
     */
    public void execute() throws AbstractDataRequestException {
        mParams = prepareParameters();
        final RawData rawData = sendRequest(mParams);
        final R res = parseResult(rawData);
        deliveryResult(res);
    }

    /**
     * Delivery data
     * @param data
     */
    private void deliveryResult(R data) {
        if ( mCallback != null ) {
            mCallback.onDataReady(data);
        }
        mData = data;
    }

    protected abstract R parseResult(RawData data) throws AbstractDataRequestException;
    protected abstract RawData sendRequest(Params p) throws AbstractDataRequestException;
    protected abstract Params prepareParameters() throws AbstractDataRequestException;

    public R getResult() {
        return mData;
    }

}
