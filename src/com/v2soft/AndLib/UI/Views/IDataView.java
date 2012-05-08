package com.v2soft.AndLib.UI.Views;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 * @param <T>
 */
public interface IDataView<T> {
    public void setData(T data);
    public T getData();
}
