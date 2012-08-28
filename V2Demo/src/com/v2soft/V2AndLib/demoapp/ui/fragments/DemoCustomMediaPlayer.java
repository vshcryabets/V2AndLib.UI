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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.v2soft.AndLib.ui.Adapters.BackLoadingAdapter;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.AndLib.ui.views.IDataView;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoCustomMediaPlayer 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private EndlessAdapter mAdapter;

    public static Fragment newInstance() {
        return new DemoCustomMediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(com.v2soft.AndLib.ui.R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new EndlessAdapter(getActivity());
        list.setAdapter(mAdapter);
        return view;
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

    private class EndlessAdapter extends BackLoadingAdapter<Integer> {

        public EndlessAdapter(Context context) {
            super(context, new CustomViewAdapterFactory<Integer, IDataView<Integer>>(){
                @Override
                public IDataView<Integer> createView(Context context, int type) {
                    return new IntegerView(context);
                }
            }, 10);
        }

        @Override
        protected List<Integer> getData(int start, int count) {
            List<Integer> result = new LinkedList<Integer>();
            for ( int i = 0; i< count; i++ ) {
                if ( start+i > 100 ) {
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
    }
}
