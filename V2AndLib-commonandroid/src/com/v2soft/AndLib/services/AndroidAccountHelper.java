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
package com.v2soft.AndLib.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

/**
 * Utils for system account management.
 * @author vshcryabets@gmail.com
 *
 */
public class AndroidAccountHelper {
	private AccountManager mAccountManager;
	private String mAccountType;
	private Account mPrimaryAccount;
	private String mAuthority;

	public AndroidAccountHelper(Context context, String accountType, String authority) {
		mAccountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
		mAccountType = accountType;
		mAuthority = authority;
		updatePrimaryAccount();
	}

	public Account getPrimaryAccount() {
		return mPrimaryAccount;
	}
	public AccountManagerFuture<Boolean> removePrimaryAccount() {
		return  removeAccount(mPrimaryAccount);
	}
	/**
	 * @param number
	 * @return
	 */
	public Account getAccountByType(int number) {
		Account[] accounts = mAccountManager.getAccountsByType(mAccountType);
		if ( accounts.length > number ) {
			return accounts[number];
		}
		return null;
	}

	/**
	 *
	 * @param account
	 * @return
	 */
	public AccountManagerFuture<Boolean> removeAccount(Account account) {
		AccountManagerFuture<Boolean> result = mAccountManager.removeAccount(account, null, null);
		updatePrimaryAccount();
		return result;
	}

	/**
	 * Add acount without password and no arguments.
	 * @param account
	 * @return
	 */
	public boolean addPrimaryAccount(Account account, boolean isSyncable) {
		if ( mPrimaryAccount != null ) {
			throw new IllegalStateException("Can't regisrter more accounts. Primary account already present.");
		}
		boolean result = mAccountManager.addAccountExplicitly(account, null, null);
		if ( result && isSyncable ) {
			// Inform the system that this account supports sync
			ContentResolver.setIsSyncable(account, mAuthority, 1);
		}
		updatePrimaryAccount();
		return result;
	}

	private void updatePrimaryAccount() {
		mPrimaryAccount = getAccountByType(0);
	}

	public void enableAutoSync(boolean enabled) {
		ContentResolver.setSyncAutomatically(mPrimaryAccount, mAuthority, enabled);
	}

	/**
	 *
	 * @param pollFrequency how frequently the sync should be performed, in seconds.
	 * @param enabled
	 */
	public void enablePeriodicSync(long pollFrequency, boolean enabled) {
		if ( enabled ) {
			ContentResolver.addPeriodicSync(mPrimaryAccount, mAuthority, new Bundle(), pollFrequency);
		} else {
			ContentResolver.removePeriodicSync(mPrimaryAccount, mAuthority, new Bundle());
		}
	}

	public void requestSync() {
		ContentResolver.requestSync(mPrimaryAccount, mAuthority, new Bundle());
	}
}
