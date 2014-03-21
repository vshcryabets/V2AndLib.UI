/*
 * Copyright (C) 2012-2014 V.Shcryabets (vshcryabets@gmail.com)
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
 */
public abstract class AbstractDataRequest<ResultType extends Serializable, Params, RawData>
		implements Serializable, ITask<ResultType> {
    private static final long serialVersionUID = 1L;
    protected ResultType mData;
	protected int mTaskId;
	private Serializable mTagObj;

	public AbstractDataRequest() {
    }

    /**
     * Execute request.
     * @throws AbstractDataRequestException
     */
	@Override
    public AbstractDataRequest<ResultType, Params, RawData> execute(ITaskSimpleListener<?> handler)
			throws AbstractDataRequestException {
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
    }

    protected abstract ResultType parseResult(RawData data) throws AbstractDataRequestException;
    protected abstract RawData sendRequest(Params p) throws AbstractDataRequestException;
    protected abstract Params prepareParameters() throws AbstractDataRequestException;
	// ========================================================
	// ITask interface
	// ========================================================
	/**
	 * Get result of the data request.
	 * @return result of the data request.
	 */
	@Override
	public ResultType getResult() {
		return mData;
	}
	@Override
	public int getTaskId() {
		return mTaskId;
	}
	@Override
	public void setTaskId(int id) {
		mTaskId = id;
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
	public void cancel() {
	}
	@Override
	public boolean canBeCanceled() {
		return true;
	}

	@Override
	public boolean isCanceled() {
		return false;
	}
}
