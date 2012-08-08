package com.v2soft.AndLib.dao;


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
