package com.v2soft.AndLib.ui.adapters;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * Created by V.Shcryabets on 5/7/14.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public abstract class InjectionAdapter extends BaseAdapter {
    protected BaseAdapter mWrappedAdapter;
    protected int mInjectViewType;
    protected DataSetObserver mDataSetObserver;

    public InjectionAdapter(BaseAdapter wrappedAdapter) {
        setAdapter(wrappedAdapter);
        mInjectViewType = mWrappedAdapter.getViewTypeCount();
    }

    protected void setAdapter(BaseAdapter adapter) {
        if (mWrappedAdapter != null && mDataSetObserver != null) {
            mWrappedAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mWrappedAdapter = adapter;
        if ( mWrappedAdapter != null ) {
            mDataSetObserver = new InjectionDataSetObserver();
            mWrappedAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public int getCount() {
        int innerCount = mWrappedAdapter.getCount();
        return innerCount + getAdditionalCount(innerCount);
    }

    @Override
    public Object getItem(int position) {
        position = convertToInnerPosition(position);
        return mWrappedAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        position = convertToInnerPosition(position);
        return mWrappedAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = null;
        if ( isInjectedView(position)) {
            result = innerGetView(position, convertView, parent);
        } else {
            position = convertToInnerPosition(position);
            result = mWrappedAdapter.getView(position, convertView, parent);
        }
        return result;
    }


    @Override
    public int getViewTypeCount() {
        return mWrappedAdapter.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if ( isInjectedView(position) ) {
            return mInjectViewType;
        } else {
            return super.getItemViewType(position);
        }
    }

    private class InjectionDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }
    };

    protected abstract boolean isInjectedView(int position);
    protected abstract int getAdditionalCount(int innerCount);
    protected abstract View innerGetView(int position, View convertView, ViewGroup parent);
    protected abstract int convertToInnerPosition(int position);

}
