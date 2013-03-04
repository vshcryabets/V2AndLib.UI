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
package com.v2soft.AndLib.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.v2soft.AndLib.ui.R;

/**
 * Abstract class that shows dialog fragment with list and allow users to choice some items
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class AbstractSelectDialogFragment<T> 
extends DialogFragment implements OnItemClickListener  {
    // =========================================================
    // Class fields
    // =========================================================
    protected BaseAdapter mAdapter;
    protected T mSelectedItem = null;
    protected ListView mListView;
    // =========================================================
    // Constructors
    // =========================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.v2andlib_fragment_list, null);
        getDialog().setTitle(getDialogTitle());
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = prepareAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mSelectedItem = (T) mListView.getItemAtPosition(arg2);
        getDialog().dismiss();
    }
    /**
     * 
     * @return Selected object or null if nothing was clicked
     */
    public Object getSelectedObject() {
        return mSelectedItem;
    }
    // =========================================================
    // Abstract members
    // =========================================================
    /**
     * 
     * @return dialog title string
     */
    protected abstract CharSequence getDialogTitle();
    /**
     * 
     * @param context current activity context
     * @return data adapter for the list
     */
    protected abstract BaseAdapter prepareAdapter(Context context);
}
