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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.v2soft.AndLib.ui.adapters.BackLoadingAdapter;
import com.v2soft.AndLib.ui.adapters.CursorPaginationAdapter;
import com.v2soft.AndLib.ui.adapters.CustomViewAdapter.CustomViewAdapterFactory;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.AndLib.ui.views.IDataView;
import com.v2soft.AndLib.ui.views.LoadingView;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.database.DemoDataItem;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;

/**
 * Sample work with endless list adapters.
 * @author vshcryabets@gmail.com
 *
 */
public class DemoEndlessList 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private CursorEndlessAdapter mAdapter;
    private static final String[] SELECT_COLUMNS = new String[]{DemoDataItem.FIELD_ID,
        DemoDataItem.FIELD_PUBLISH_DATE,
        DemoDataItem.FIELD_TITLE};


    public static Fragment newInstance() {
        return new DemoEndlessList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new CursorEndlessAdapter(getActivity());
        list.setAdapter(mAdapter);
        mAdapter.getFilter().filter(null);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_endless, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            //            mAdapter.clear();
            mAdapter.tryToLoadMore();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
    }

    private class IntegerView extends TextView implements IDataView<Integer> {
        private Integer mData;
        public IntegerView(Context context) {
            super(context);
        }

        @Override
        public void setData(Integer data) {
            mData = data;
            setText(String.valueOf(data));
        }

        @Override
        public Integer getData() {
            return mData;
        }
    }

    private class DemoDataView extends TextView implements IDataView<Cursor> {
        private Cursor mData;
        public DemoDataView(Context context) {
            super(context);
        }

        @Override
        public void setData(Cursor data) {
            mData = data;
            setText(data.getString(data.getColumnIndex(DemoDataItem.FIELD_TITLE)));
        }

        @Override
        public Cursor getData() {
            return mData;
        }
    }

    private class EndlessAdapter extends BackLoadingAdapter<Integer> {
        private View mLoadIndicator;

        public EndlessAdapter(Context context) {
            super(context, new CustomViewAdapterFactory<Integer, IDataView<Integer>>(){
                @Override
                public IDataView<Integer> createView(Context context, int type) {
                    return new IntegerView(context);
                }
            }, 10);
            mLoadIndicator = new LoadingView(context);
        }

        @Override
        protected List<Integer> getData(int start, int count) {
            List<Integer> result = new LinkedList<Integer>();
            for ( int i = 0; i< count; i++ ) {
                if ( start+i > 500 ) {
                    break;
                }
                result.add(start+i);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onLoadStarted(boolean first) {
            if ( first ) {
                getActivity().setProgressBarIndeterminateVisibility(true);
            } else {
                showLoaderAtBottom(mLoadIndicator);
            }
        }

        @Override
        protected void onLoadFinished() {
            if ( isAdded() ) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                hideLoaderAtBottom();
            }
        }
    }

    private class CursorEndlessAdapter extends CursorPaginationAdapter implements Filterable {

        public CursorEndlessAdapter(Context context) {
            super(context, new CustomViewAdapterFactory<Cursor, IDataView<Cursor>>(){
                @Override
                public IDataView<Cursor> createView(Context context, int type) {
                    return new DemoDataView(context);
                }
            }, null);
        }

        @Override
        protected void onLoadStarted(boolean first) {
            getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onLoadFinished(boolean first) {
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            Cursor result = mContext.getContentResolver().query(DemoListProvider.CONTENT_URI, 
                    SELECT_COLUMNS, null, null, null);
            return result;
        }

        @Override
        protected boolean getNextDataPage() {
            if ( getCount() < 100 ) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                getActivity().getContentResolver().update(DemoListProvider.CONTENT_INSERT_URI, null, null, null);
                return true;
            } else {
                return false;
            }

        }
    }
}
