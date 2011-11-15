// ***** BEGIN LICENSE BLOCK *****
// Version: MPL 1.1
// 
// The contents of this file are subject to the Mozilla Public License Version 
// 1.1 (the "License"); you may not use this file except in compliance with 
// the License. You may obtain a copy of the License at 
// http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
// for the specific language governing rights and limitations under the
// License.
// 
// The Initial Developer of the Original Code is 
//  2V Software (vshcryabets@2vsoft.com).
// Portions created by the Initial Developer are Copyright (C) 2010
// the Initial Developer. All Rights Reserved.
// 
// 
// ***** END LICENSE BLOCK *****
package com.v2soft.AndLib.UI.Adapters;

import java.util.ArrayList;
import java.util.List;

import com.v2soft.AndLib.UI.Views.LoadingView;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class BackLoadingAdapter<T> 
extends BaseAdapter 
implements Callback {
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    protected static final int MSG_DATASET_CHANGED = 1;
    protected static final int MSG_NEW_DATA_PART = 2;
    
    public static final int ALL_DATA = -1;
    public static final int UNLIMITED_COUNT = -2;
    public static final int NOT_INITIALIZED = -3;
    private static final int DEFAULT_PAGE_SIZE = 15;
    protected static final String LOG_TAG = BackLoadingAdapter.class.getSimpleName();
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    protected List<T> mItems;
    protected Context mContext;
    protected Handler mHandler;
    private int mTotalCount;
    private int mLoadedCount;
    private int mPartSize;
    private LoadingView mLoadingView;
    private Boolean isLoading; 

    public BackLoadingAdapter(Context context) {
        this(context, DEFAULT_PAGE_SIZE);
    }

    /**
     * 
     * @param context The Context the listview is running in, through which it can access the current theme, resources, etc. 
     * @param partSize
     */
    public BackLoadingAdapter(Context context, int partSize) {
        super();
        mContext = context;
        mItems = new ArrayList<T>();
        mLoadingView = new LoadingView(context);
        mHandler = new Handler(this);
        mPartSize = partSize;
        mTotalCount = NOT_INITIALIZED;
        mLoadedCount = NOT_INITIALIZED;
        isLoading = false;
    }	

    @Override
    public int getCount() {
        if ( mTotalCount == NOT_INITIALIZED ) {
            return 0;
        } else {
            if ( mLoadedCount == mTotalCount )
            	// everything was already loaded
                return mTotalCount;
            else
                return mLoadedCount+1;
        }
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if ( ((mTotalCount == UNLIMITED_COUNT) || ( mLoadedCount < mTotalCount )) 
                && ( position == mLoadedCount )) {
            // start load part
            if ( !isLoading )
                loadNextPart();
            return mLoadingView;
        } else {
            if ( ( convertView == null ) || (convertView.getClass().equals(LoadingView.class)))
                return getInternalView(position, null);
            else
                return getInternalView(position, convertView);
        }
    }    
    
    public void startUpdate() {
        Thread searchBg = new Thread(new Runnable() {
            @Override
            public void run() {
                if ( mTotalCount == NOT_INITIALIZED ) {
                    mTotalCount = getTotalDataCount();
                }
                if ( mPartSize == ALL_DATA ) {
                    mItems = getData(0, mTotalCount);
                    mTotalCount = mItems.size();
                    mLoadedCount = mTotalCount;
                } else {
                    mLoadedCount = 0;
                }
                mHandler.sendEmptyMessage(MSG_DATASET_CHANGED);
            }
        });
        searchBg.start();
    }

    private void loadNextPart() {
        isLoading = true;
        Thread back = new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d(LOG_TAG, "Loading next part");
                int count = mPartSize;
                
                if ( mTotalCount > 0 ) {
                    int rest = mTotalCount-mLoadedCount;
                    if ( rest < count ) 
                        count = rest;
                }
                List<T> part = getData(mLoadedCount,count);
//                Log.d(LOG_TAG, "Loaded next part");
                final Message msg = new Message();
                msg.what = MSG_NEW_DATA_PART;
                msg.obj = part;
                mHandler.sendMessage(msg);
                isLoading = false;
            }
        }, "LoadAdapterBack");
        back.start();
    }    

    /**
     * 
     * @return total items count, or BackLoadingAdapter.UNLIMITED_COUNT if total count is unknown
     */
    protected abstract int getTotalDataCount();
    protected abstract List<T> getData(int start, int count);
    protected abstract View getInternalView(int position, View convertView);    

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_DATASET_CHANGED:
            notifyDataSetChanged();
            break;
        case MSG_NEW_DATA_PART:
            List<T> part = (List<T>) msg.obj;
            if ( part.size() == 0 ) {
                // No more data
                mTotalCount = mLoadedCount;
            } else {
                mItems.addAll(part);
                mLoadedCount += part.size();
            }
            notifyDataSetChanged();
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * Remove all items from inner list
     */
    public void clear() {
        mItems.clear();
        mHandler.sendEmptyMessage(MSG_DATASET_CHANGED);
    }

}
