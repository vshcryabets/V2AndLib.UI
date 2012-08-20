/*
 * Copyright (C) 2010 V.Shcryabets (vshcryabets@gmail.com)
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
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.v2soft.AndLib.ui.views.IDataView;
import com.v2soft.AndLib.ui.views.LoadingView;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class BackLoadingAdapter<T> 
extends CustomViewAdapter<T> {
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    protected static final String LOG_TAG = BackLoadingAdapter.class.getSimpleName();
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    private int mPartSize;
    private LoadingView mLoadingView;
    private Boolean isLoading;
    private boolean mWantToLoadMore;

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
        mLoadingView = new LoadingView(context);
        mPartSize = partSize;
        isLoading = false;
    }

    @Override
    public int getCount() {
        return ( mWantToLoadMore ? super.getCount()+1 : super.getCount() );
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if ( mWantToLoadMore  && ( position == super.getCount() )) {
            return 1;
        }
        return 0;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if ( getItemViewType(position) == 1 ) {
            // start load part
            if ( !isLoading ) {
                loadNextPart();
            }
            return mLoadingView;
        }
        return super.getView(position, convertView, parent);
    }    

//    public void startUpdate() {
//        isLoading = true;
//        mWantToLoadMore = true;
//        Thread loaderThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mItems = getData(0, Integer.MAX_VALUE);
//                isLoading = false;
//                mWantToLoadMore = false;
//                mHandler.sendEmptyMessage(MSG_DATASET_CHANGED);
//            }
//        });
//        loaderThread.start();
//    }

    private void loadNextPart() {
        isLoading = true;
        Thread back = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<T> part = getData(mItems.size(),mPartSize);
                if ( part.size() == 0 ) {
                    // No more data
                    mWantToLoadMore = false;
                }
                final Message msg = new Message();
                msg.what = MSG_DATASET_ADD_LIST;
                msg.obj = part;
                mHandler.sendMessage(msg);
                isLoading = false;
            }
        }, "LoadAdapterBack");
        back.start();
    }    

    /**
     * 
     * @param start
     * @param count
     * @return data partition
     */
    protected abstract List<T> getData(int start, int count);
}
