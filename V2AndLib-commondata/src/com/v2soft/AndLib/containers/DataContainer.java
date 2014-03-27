package com.v2soft.AndLib.containers;


/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DataContainer<T> {
    protected T mData;
    protected Throwable mError;
    
    public DataContainer(T data) {
        setData(data);
    }
    public DataContainer(Throwable error) {
        mError = error;
    }
    
    public T getData() {return mData;}
    public Throwable getError() {return mError;}
    public boolean isSuccesfull(){return mError==null;}
    public void setData(T data) {mData = data;}
    public void setError(Throwable data) {mError = data;}
}
