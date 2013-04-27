package com.v2soft.AndLib.dao;

import java.util.List;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.ITask;

/**
 * Base cache interface
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <ID> identity
 * @param <T> cahce item type
 */
public interface ICache<ID,T> {
    /**
     * Update cache task
     */
    public ITask getUpdateTask();
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
    public void onUpdateFailed(Exception ex);
    /**
     * 
     * @return all items from cache
     */
    public List<T> getAllItems();
}
