package com.v2soft.AndLib.demotest;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.v2soft.AndLib.services.AndroidAccountHelper;
import com.v2soft.AndLib.ui.adapters.InjectionAdapter;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;
import com.v2soft.V2AndLib.demoapp.services.DemoAuthService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Test logic of InjectAdapter class.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class InjectAdapterTests extends AndroidTestCase {

    @SmallTest
    public void testNestedNotifyDataSetChanged()  {
        ArrayList<Integer> testData = new ArrayList<Integer>();
        testData.add(1);
        testData.add(2);
        testData.add(3);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(), 0, testData);
        TestInjectAdapter injectAdapeter = new TestInjectAdapter(adapter);
        assertEquals("Wrong number of count", 4, injectAdapeter.getCount());
        adapter.add(4);
        assertEquals("Wrong number of count", 5, injectAdapeter.getCount());
        TestListView view = new TestListView(getContext());
        view.setAdapter(injectAdapeter);
        view.update();
        adapter.add(5);
        assertEquals("Wrong number of count", 6, injectAdapeter.getCount());
        view.update();
    }

    private class TestListView extends ListView {

        public TestListView(Context context) {
            super(context);
        }
        public void update() {
            layoutChildren();
        }
    }

    private class TestInjectAdapter extends InjectionAdapter {

        public TestInjectAdapter(BaseAdapter wrappedAdapter) {
            super(wrappedAdapter);
        }

        @Override
        protected boolean isInjectedView(int position) {
            return position % 4 == 0;
        }

        @Override
        protected int getAdditionalCount(int innerCount) {
            return 1;
        }

        @Override
        protected View innerGetView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        protected int convertToInnerPosition(int position) {
            return position-1;
        }
    }

}
