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
                            getUpdateTask().execute(null);
                        } catch (Exception e) {
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
