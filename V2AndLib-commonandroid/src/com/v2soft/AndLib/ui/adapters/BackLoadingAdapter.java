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
package com.v2soft.AndLib.ui.adapters;

import java.util.List;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.v2soft.AndLib.ui.views.IDataView;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 * @version 5
 *
 */
public abstract class BackLoadingAdapter<T> 
extends CustomViewAdapter<T> {
    //---------------------------------------------------------------------------
    // Interfaces
    //---------------------------------------------------------------------------
    public interface BackLoadingAdapterListener {
        public void onLoadStarted(boolean first);
        public void onLoadFinished();
    }
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    protected static final String LOG_TAG = BackLoadingAdapter.class.getSimpleName();
    public enum LoadingNotificationType {
        AtTheEndOfList,
        Custom
    };
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_LOADER = 1;
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    private int mPartSize;
    private View mLoadingView;
    private boolean isLoading;
    private boolean mWantToLoadMore;
    protected LoadingNotificationType mCurrentNotification;

    /**
     * 
     * @param context The Context the listview is running in, through which it can access the current theme, resources, etc. 
     * @param partSize
     */
    public BackLoadingAdapter(Context context, 
            CustomViewAdapterFactory<T, IDataView<T>> factory,
            int partSize) {
        super(context, factory);
        mWantToLoadMore = true;
        mPartSize = partSize;
        isLoading = false;
    }

    @Override
    public int getCount() {
        int res = super.getCount();
        if ( res < 1 && mWantToLoadMore && !isLoading ) {
            loadNextPart(true);
        }
        if ( mLoadingView != null ) {
            res++;
        }
        return res;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if ( position == super.getCount() && mLoadingView != null ) {
            return VIEW_TYPE_LOADER;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( position == super.getCount()-1 && mWantToLoadMore && !isLoading ) {
            // all items was shown
            loadNextPart(false);
        }
        if ( getItemViewType(position) == VIEW_TYPE_LOADER ) {
            return mLoadingView;
        }
        return super.getView(position, convertView, parent);
    }    

    private void loadNextPart(boolean first) {
        isLoading = true;
        Thread back = new Thread(new Runnable() {
            @Override
            public void run() {
                final Message msg = new Message();
                msg.what = MSG_DATASET_ADD_LIST;
                msg.obj = getData(mItems.size(),mPartSize);
                mHandler.sendMessage(msg);
            }
        }, "LoadAdapterBack");
        back.start();
        onLoadStarted(first);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if ( msg.what == MSG_DATASET_ADD_LIST ) {
            final List<T> part = (List<T>) msg.obj;
            if ( part == null || part.size() == 0 ) {
                // No more data
                mWantToLoadMore = false;
            }
            onLoadFinished();
            isLoading = false;
        }
        return super.handleMessage(msg);
    }

    @Override
    public void clear() {
        super.clear();
        mWantToLoadMore = true;
    }
    /**
     * Show specified view at the end of list
     * @param loader load indicator view
     */
    protected void showLoaderAtBottom(View loader) {
        mLoadingView = loader;
        notifyDataSetChanged();
    }
    /**
     * Hide load indicator that was shown at the bottom of list 
     */
    protected void hideLoaderAtBottom() {
        showLoaderAtBottom(null);
    }
    /**
     * Adapter will try to load more items.
     */
    public void tryToLoadMore() {
        mWantToLoadMore = true;
    }

    /**
     * 
     * @param start
     * @param count
     * @return data partition
     */
    protected abstract List<T> getData(int start, int count);
    protected abstract void onLoadStarted(boolean first);
    protected abstract void onLoadFinished();
}
