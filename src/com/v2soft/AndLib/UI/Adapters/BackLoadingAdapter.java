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

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.BaseAdapter;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class BackLoadingAdapter<T> extends BaseAdapter implements Callback {
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
	protected static final int MSG_DATASET_CHANGED = 1;
    //---------------------------------------------------------------------------
    // Class fields
    //---------------------------------------------------------------------------
    protected List<T> mItems;
    protected Context mContext;
    protected Handler mHandler;

	public BackLoadingAdapter(Context context) {
		mContext = context;
		mHandler = new Handler(this);
	}
	
	@Override
	public int getCount() {
	    if ( mItems == null ) {
	        return 0;
	    } else {
	        return mItems.size();
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

    public void startUpdate() {
        Thread searchBg = new Thread(new Runnable() {
            @Override
            public void run() {
                updateData();
                mHandler.sendEmptyMessage(MSG_DATASET_CHANGED);
            }
        });
        searchBg.start();
    }

    protected abstract void updateData();

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
