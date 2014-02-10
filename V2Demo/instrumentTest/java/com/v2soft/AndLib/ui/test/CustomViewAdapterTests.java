/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.v2soft.AndLib.ui.adapters.CustomViewAdapter;
import com.v2soft.V2AndLib.demoapp.V2DemoActivity;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Unit tests for custom view adapter.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class CustomViewAdapterTests extends ActivityInstrumentationTestCase2<V2DemoActivity> {
	private CustomViewAdapterTest mAdapter;
	
    public CustomViewAdapterTests() {
        super(V2DemoActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Add collection test.
     * @author V.Shcryabets<vshcryabets@gmail.com>
     * @throws InterruptedException 
     */
    @MediumTest
    public void testAddCollection() throws InterruptedException {
    	int N = 100;
    	
    	final Collection<Integer> collection = new LinkedList<Integer>();
    	Random rnd = new Random();
    	for ( int i = 0; i < N; i++) {
    		collection.add(rnd.nextInt());
    	}
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	mAdapter = new CustomViewAdapterTest(getActivity());
            	mAdapter.setData(collection);
            }
        });
        getInstrumentation().waitForIdleSync();
        Thread.sleep(6000);
    	// check data
        int i = 0 ;
        for (Integer integer : collection) {
        	assertEquals("Wrong item at "+i, integer, mAdapter.getItem(i++));
		}
    }
    
    private class CustomViewAdapterTest extends CustomViewAdapter<Integer> {

		public CustomViewAdapterTest(Context context) {
			super(context, null);
		}
    }
}
