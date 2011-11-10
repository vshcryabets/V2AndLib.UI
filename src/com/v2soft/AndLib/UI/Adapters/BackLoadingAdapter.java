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

import java.util.Collections;
import java.util.List;

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
    public static final int ALL_DATA = -1;
    public static final int UNLIMITED_COUNT = -2;
    public static final int NOT_INITIALIZED = -3;
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    protected List<T> mItems = Collections.emptyList();
    protected Context mContext;
    protected Handler mHandler;
    private int mTotalCount;
    private int mLoadedCount;
    private int mPartSize;
    private LoadingView mLoadingView;

    public BackLoadingAdapter(Context context) {
        this(context, UNLIMITED_COUNT);
    }

    public BackLoadingAdapter(Context context, int partSize) {
        super();
        //        mLoadingView = new LoadingView(context);
        mHandler = new Handler(this);
        mPartSize = partSize;
        mTotalCount = NOT_INITIALIZED;
        mLoadedCount = NOT_INITIALIZED;
    }	

    @Override
    public int getCount() {
        if ( mTotalCount == NOT_INITIALIZED ) {
            return 0;
        } else {
            if ( mLoadedCount == mTotalCount )
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (( mLoadedCount < mTotalCount ) && ( position == mLoadedCount )) {
            // start load part
            loadNextPart();
            return mLoadingView;
        } else {
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
                if ( mTotalCount == UNLIMITED_COUNT ) {
                    mItems = getData(0, ALL_DATA);
                    mTotalCount = mItems.size();
                    mLoadedCount = mTotalCount;
                } else {
                    mLoadedCount = 0;
                    // show Loading message
                }
                mHandler.sendEmptyMessage(MSG_DATASET_CHANGED);
            }
        });
        searchBg.start();
    }

    private void loadNextPart() {
        Thread back = new Thread(new Runnable() {
            @Override
            public void run() {
                if ( mTotalCount == 0 )
                    mTotalCount = getTotalDataCount();
                if ( mTotalCount > 0 ) {
                    int count = mTotalCount-mLoadedCount;
                    if ( count > mPartSize ) 
                        count = mPartSize;
                    List<T> part = getData(mLoadedCount,count);
                    mEntries.addAll(part);
                    mLoadedCount = mEntries.size();
                } else {
                    noData = true;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, "LoadAdapterBack");
        back.start();
    }    

    protected abstract int getTotalDataCount();
    protected abstract List<T> getData(int start, int count);
    protected abstract View getInternalView(int position, View convertView);    

    @Override
    public boolean handleMessage(Message msg) {
        if ( msg.what == MSG_DATASET_CHANGED ) {
            notifyDataSetChanged();
        }
        return true;
    }

    public void clear() {
        mItems.clear();
        mHandler.sendEmptyMessage(MSG_DATASET_CHANGED);
    }

}
