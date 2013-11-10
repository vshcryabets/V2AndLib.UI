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
package com.v2soft.V2AndLib.demoapp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;
import com.v2soft.V2AndLib.demoapp.providers.DemoSyncAdapter;
import com.v2soft.V2AndLib.demoapp.services.DemoAuthService;
import com.v2soft.V2AndLib.demoapp.ui.activities.BluetoothList;
import com.v2soft.V2AndLib.demoapp.ui.activities.CameraActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.DialogsActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.DropBoxUpload;
import com.v2soft.V2AndLib.demoapp.ui.activities.EndlessListActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.GCPDemo;
import com.v2soft.V2AndLib.demoapp.ui.activities.NavigationDrawerActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.OpenSLSample;
import com.v2soft.V2AndLib.demoapp.ui.activities.TricksActivity;
import com.v2soft.V2AndLib.demoapp.ui.activities.UDPDiscoveryList;
import com.v2soft.V2AndLib.demoapp.ui.activities.WiFiList;

/**
 * Demo start activity
 * @author vshcryabets@gmail.com
 *
 */
public class V2DemoActivity 
extends ListActivity 
implements OnItemClickListener {
    private final static Class<?>[] sActivities = new Class[]{
        BluetoothList.class,
        CameraActivity.class,
        UDPDiscoveryList.class,
        DropBoxUpload.class,
        OpenSLSample.class,
        GCPDemo.class,
        EndlessListActivity.class,
        DialogsActivity.class,
        WiFiList.class,
        TricksActivity.class,
        NavigationDrawerActivity.class
    };

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = DemoListProvider.PROVIDER_NAME;
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    private Account mAccount;
    private ArrayAdapter<String> mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final List<String> names= new ArrayList<String>();
        for (Class<?> activity : sActivities) {
            try {
                final Method method = activity.getMethod("getSampleName");
                names.add( (String) method.invoke(null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        mAccount = createAccount();
        startSync(mAccount);
    }

    private void startSync(Account account) {
        Bundle arg = new Bundle();
        arg.putString(DemoSyncAdapter.EXTRA_TYPE, "manualSync");
        ContentResolver.requestSync(
                account,
                AUTHORITY,
                arg);
    }

    private Account createAccount() {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, DemoAuthService.ACCOUNT_TYPE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
         // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
//            // Recommend a schedule for automatic synchronization. The system may modify this based
//            // on other scheduled syncs and network utilization.
//            ContentResolver.addPeriodicSync(
//                    newAccount, AUTHORITY, new Bundle(), 60000);
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        startActivity(new Intent(this, sActivities[arg2]));
    }
}