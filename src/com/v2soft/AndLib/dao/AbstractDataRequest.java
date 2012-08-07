package com.v2soft.AndLib.dao;

import java.io.IOException;

/**
 * Abstract data request class
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class AbstractDataRequest<R, Params, RawData> {
	public interface AbstractDataRequestCallback<R> {
		void onDataReady(R result);
	}

	protected AbstractDataRequestCallback mCallback;
	protected R mData;

	public AbstractDataRequest(AbstractDataRequestCallback<R> callback) {
		mCallback = callback;
	}
	
	/**
	 * Execute request
	 * @throws AbstractDataRequestException 
	 */
	public void execute() throws AbstractDataRequestException {
		final Params p = prepareParameters();
		final RawData rawData = sendRequest(p);
		mData = parseResult(rawData);
		deliveryResult(mData);
	}

	/**
	 * Delivery data
	 * @param data
	 */
	private void deliveryResult(R data) {
		if ( mCallback != null ) {
			mCallback.onDataReady(data);
		}
	}

	protected abstract R parseResult(RawData data);
	protected abstract RawData sendRequest(Params p) throws AbstractDataRequestException;
	protected abstract Params prepareParameters();
	
	public R getResult() {
		return mData;
	}

}
