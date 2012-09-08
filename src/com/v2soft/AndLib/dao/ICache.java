package com.v2soft.AndLib.dao;

import java.util.List;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Base cache interface
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <ID> identity
 * @param <T> cahce item type
 */
public interface ICache<ID,T> {
    /**
     * Update cache items
     */
    public void startUpdate() throws AbstractDataRequestException;
    /**
     * Asynchronous update
     */
    public void startUpdateAsync();
    /**
     * Clear cache
     */
    public void clear();
    /**
     * 
     * @param id
     * @return get item by specified id
     */
    public T getItemById(ID id);
    public void onUpdateFailed(AbstractDataRequestException ex);
    /**
     * 
     * @return all items from cache
     */
    public List<T> getAllItems();
}
