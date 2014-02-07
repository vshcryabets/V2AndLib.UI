/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.v2soft.AndLib.services.AndroidAccountHelper;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;
import com.v2soft.V2AndLib.demoapp.providers.DemoSyncAdapter;
import com.v2soft.V2AndLib.demoapp.services.DemoAuthService;

/**
 * Fragment that show syncadapter samples.
 * @author vshcryabets@gmail.com
 *
 */
public class SynchronizationFragment
		extends BaseFragment<DemoApplication, DemoAppSettings> implements CompoundButton.OnCheckedChangeListener {
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = DemoListProvider.PROVIDER_NAME;
	// The account name
	public static final String ACCOUNT_NAME = "dummyaccount";
	private StringBuilder mInfoText;
	private TextView mInfoTextView;
	private AndroidAccountHelper mHelper;

	public static Fragment newInstance() {
		return new SynchronizationFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_synchronization, null);
		registerOnClickListener(new int[]{R.id.registerAccount, R.id.deleteAccount, R.id.requestSync}, view);
		((CheckBox)view.findViewById(R.id.autoSync)).setOnCheckedChangeListener(this);
		((CheckBox)view.findViewById(R.id.startSync40)).setOnCheckedChangeListener(this);
		((CheckBox)view.findViewById(R.id.startSync60)).setOnCheckedChangeListener(this);
		mInfoText = new StringBuilder();
		mInfoTextView = (TextView) view.findViewById(R.id.infoText);
		mHelper = new AndroidAccountHelper(getActivity(), DemoAuthService.ACCOUNT_TYPE, AUTHORITY);
		showInfo("Primary account="+mHelper.getPrimaryAccount());
		return view;
	}

	@Override
	public void onClick(View v) {
		Account account = mHelper.getPrimaryAccount();
		switch (v.getId()) {
			case R.id.registerAccount:
				if ( account != null ) {
					showInfo("Account already registered.");
					return;
				}
				// Create the account type and default account
				Account newAccount = new Account(ACCOUNT_NAME, DemoAuthService.ACCOUNT_TYPE);
				boolean result = mHelper.addPrimaryAccount(newAccount, true);
				if ( result ) {
					showInfo("Account created.");
				} else {
					showInfo("Can't create account");
				}
				break;
			case R.id.deleteAccount:
				if ( account != null ) {
					AccountManagerFuture<Boolean> result2 = mHelper.removePrimaryAccount();
					showInfo("Delete account " + result2.isDone());
				} else {
					showInfo("Can't find account");
				}
				break;
			case R.id.requestSync:
				showInfo("Request sync");
				mHelper.requestSync();
				break;
		}
	}


	private void showInfo(String s) {
		mInfoText.append(s);
		mInfoText.append('\n');
		mInfoTextView.setText(mInfoText.toString());
	}


	private void startSync(Account account) {
		showInfo("Request sync operation.");
		Bundle arg = new Bundle();
		arg.putString(DemoSyncAdapter.EXTRA_TYPE, "manualSync");
		ContentResolver.requestSync(
				account,
				AUTHORITY,
				arg);
	}

//	private Account createAccount() {
//		Account account = mHelper.getPrimaryAccount();
//		if ( account != null ) {
//			return account;
//		}
//		// Create the account type and default account
//		Account newAccount = new Account(ACCOUNT_NAME, DemoAuthService.ACCOUNT_TYPE);
//        /*
//         * Add the account and account type, no password or user data
//         * If successful, return the Account object, otherwise report an error.
//         */
//		if (mAccountManager.addAccountExplicitly(newAccount, null, null)) {
//			// Inform the system that this account supports sync
//			ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
//			// Inform the system that this account is eligible for auto sync when the network is up
//			ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
////            // Recommend a schedule for automatic synchronization. The system may modify this based
////            // on other scheduled syncs and network utilization.
////            ContentResolver.addPeriodicSync(
////                    newAccount, AUTHORITY, new Bundle(), 60000);
//			showInfo("Account created.");
//		} else {
//			showInfo("Can't create account");
//            /*
//             * The account exists or some other error occurred. Log this, report it,
//             * or handle it internally.
//             */
//		}
//		return newAccount;
//	}

	/**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Synchronization adapter sample";
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		switch ( id ) {
			case R.id.autoSync:
				mHelper.enableAutoSync(isChecked);
				break;
			case R.id.startSync40:
				mHelper.enablePeriodicSync(105, isChecked);
				break;
			case R.id.startSync60:
				mHelper.enablePeriodicSync(60, isChecked);
				break;
		}
	}
}
