/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import com.v2soft.AndLib.ui.views.IDataView;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class CustomViewAdapter<T> 
extends BaseAdapter 
implements Callback {
    //---------------------------------------------------------------------------
    // Interfaces
    //---------------------------------------------------------------------------
    public interface CustomViewAdapterFactory<T, V extends IDataView<T>> {
        public V createView(Context context, int viewType);
    }
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    protected static final int MSG_DATASET_CHANGED = 1;
    protected static final int MSG_DATASET_CLEAR = 2;
    protected static final int MSG_DATASET_ADD = 3;
    protected static final int MSG_DATASET_REMOVE = 4;
    protected static final int MSG_DATASET_ADD_LIST = 5;
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    protected List<T> mItems;
    protected Handler mHandler;
    private CustomViewAdapterFactory<T, IDataView<T>> mFactory;
    protected Context mContext;
    //---------------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------------
    public CustomViewAdapter(Context context) {
        super();
        if ( context == null ) throw new NullPointerException("Context is null");
        mItems = new ArrayList<T>();
        mHandler = new Handler(this);
        mContext = context;
    }

    public CustomViewAdapter(Context context, CustomViewAdapterFactory<T, IDataView<T>> factory) {
        this(context);
        mFactory = factory;
    }

    /**
     * Set adapter data
     * @param data
     */
    public void setData(List<T> data) {
        if ( data == null ) throw new NullPointerException("Data is null");
        final Message message = new Message();
        message.what = MSG_DATASET_CHANGED;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    @Override
    public int getCount() {
        return mItems.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        IDataView<T> view = (IDataView<T>) convertView;
        if ( view == null ) {
            view = mFactory.createView(mContext, getItemViewType(position));
        }
        view.setData(mItems.get(position));
        return (View) view;
    }

    /**
     * Add item to the list
     * @param item
     */
    public void addItem(final T item) {
        Message msg = new Message();
        msg.what = MSG_DATASET_ADD;
        msg.obj = item;
        mHandler.sendMessage(msg);
    }

    /**
     * Remove specified item from the list
     * @param item
     */
    public void removeItem(final T item) {
        final Message msg = new Message();
        msg.what = MSG_DATASET_REMOVE;
        msg.obj = item;
        mHandler.sendMessage(msg);
    }

    /**
     * Remove all items from list
     */
    public void clear() {
        mHandler.sendEmptyMessage(MSG_DATASET_CLEAR);
    }    
    //---------------------------------------------------------------------------
    // Handler callback
    //---------------------------------------------------------------------------
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_DATASET_CHANGED:
            mItems.clear();
            if ( msg.obj != null ) {
                mItems.addAll((List<T>)msg.obj);
            }
            notifyDataSetChanged();
            break;
        case MSG_DATASET_CLEAR:
            mItems.clear();
            notifyDataSetChanged();
            break;
        case MSG_DATASET_ADD:
            if ( msg.obj != null ) {
                mItems.add((T) msg.obj);
            }
            notifyDataSetChanged();
            break;
        case MSG_DATASET_REMOVE:
            if ( msg.obj != null ) {
                mItems.remove(msg.obj);
            }
            notifyDataSetChanged();
            break;
        case MSG_DATASET_ADD_LIST:
            if ( msg.obj != null ) {
                List<T> list = (List<T>) msg.obj;
                if ( list.size() > 0 ) {
                    mItems.addAll(list);
                }
            }
            notifyDataSetChanged();
            break;

        default:
            break;
        }
        return true;
    }


}
