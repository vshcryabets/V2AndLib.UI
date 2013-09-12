/*
 * Copyright (C) 2010-2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.v2soft.AndLib.ui.Adapters.BackLoadingAdapter.LoadingNotificationType;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 * @version 5
 *
 */
public abstract class CursorPaginationAdapter 
extends CursorAdapter {
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    protected static final String LOG_TAG = CursorPaginationAdapter.class.getSimpleName();
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    protected Context mContext;
    private boolean isLoading;
    private boolean mWantToLoadMore;
    protected LoadingNotificationType mCurrentNotification;
    protected CustomViewAdapter.CustomViewAdapterFactory<Cursor, IDataView<Cursor>> mFactory;
    private Handler mHandler;

    /**
     * 
     * @param context The Context the listview is running in, through which it can access the current theme, resources, etc. 
     * @param partSize
     */
    public CursorPaginationAdapter(Context context, 
            CustomViewAdapter.CustomViewAdapterFactory<Cursor, IDataView<Cursor>> factory,
            Handler handler) {
        super(context, null, true);
        mContext = context;
        mFactory = factory;
        mWantToLoadMore = true;
        isLoading = false;
        mHandler = handler;
        if ( mHandler == null ) {
            mHandler = new Handler();
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        IDataView<Cursor> dataView = (IDataView<Cursor>) view;
        dataView.setData(cursor);
    }
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return (View) mFactory.createView(context, BackLoadingAdapter.VIEW_TYPE_NORMAL);
    }
    
    @Override
    public int getCount() {
        int count = super.getCount();
        if ( count == 0 && mWantToLoadMore && !isLoading ) {
            loadNextPart(true);
        }
        return count;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int count = getCount();
        if ( (position+1) >= count && mWantToLoadMore && !isLoading ) {
            // we reached last element, try to load more items.
            loadNextPart(false);
        }
        return super.getView(position, convertView, parent);
    }

    private synchronized void loadNextPart(final boolean first) {
        isLoading = true;
        Thread back = new Thread(new Runnable() {
            @Override
            public void run() {
                mWantToLoadMore = getNextDataPage();
                isLoading = false;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onLoadFinished(first);
                    }
                });
            }
        }, LOG_TAG);
        back.start();
        onLoadStarted(first);
    }

    /**
     * Adapter will try to load more items.
     */
    public void tryToLoadMore() {
        mWantToLoadMore = true;
    }

    /**
     * 
     * @return true if more data pages can be obtained.
     */
    protected abstract boolean getNextDataPage();
    protected abstract void onLoadStarted(boolean first);
    protected abstract void onLoadFinished(boolean first);
}
