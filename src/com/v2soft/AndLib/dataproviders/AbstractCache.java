package com.v2soft.AndLib.dataproviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.v2soft.AndLib.dao.ICache;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <ID>
 * @param <T>
 */
public abstract class AbstractCache<ID, T> implements ICache<ID, T>{
    protected Map<ID, T> mCacheItems;

    public AbstractCache() {
        mCacheItems = new HashMap<ID, T>();
    }

    @Override
    public void clear() {
        mCacheItems.clear();
    }

    @Override
    public T getItemById(ID id) {
        return mCacheItems.get(id);
    }
    
    @Override
    public void startUpdateAsync() {
        final Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startUpdate();
                        } catch (AbstractDataRequestException e) {
                            onUpdateFailed(e);
                        }
                    }
                }, this.getClass().getName());
        thread.start();
    }
    
    @Override
    public List<T> getAllItems() {
        return new ArrayList<T>(mCacheItems.values());
    }
}
