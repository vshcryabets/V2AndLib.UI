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
//  V.Shcriyabets (vshcryabets@gmail.com)
// Portions created by the Initial Developer are Copyright (C) 2010
// the Initial Developer. All Rights Reserved.
// 
// 
// ***** END LICENSE BLOCK *****
package com.v2soft.AndLib.UI.Adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import com.v2soft.AndLib.UI.Views.IDataView;

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
        public V createView(Context context);
    }
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    protected static final int MSG_DATASET_CHANGED = 1;
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    protected List<T> mItems;
    protected Handler mHandler;
    private CustomViewAdapterFactory<T, IDataView<T>> mFactory;
    protected Context mContext;

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
            view = mFactory.createView(mContext);
        }
        view.setData(mItems.get(position));
        return (View) view;
    }
    
    //---------------------------------------------------------------------------
    // Handler callback
    //---------------------------------------------------------------------------
    @Override
    public boolean handleMessage(Message msg) {
        if ( msg.what == MSG_DATASET_CHANGED ) {
            notifyDataSetChanged();
        }
        return true;
    }
}
