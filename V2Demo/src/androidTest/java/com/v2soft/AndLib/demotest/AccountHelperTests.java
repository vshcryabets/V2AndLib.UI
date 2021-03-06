package com.v2soft.AndLib.demotest;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.services.AndroidAccountHelper;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;
import com.v2soft.V2AndLib.demoapp.services.DemoAuthService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Test logic of AndroidAccountHelper class.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class AccountHelperTests extends AndroidTestCase {
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = DemoListProvider.PROVIDER_NAME;

    @SmallTest
    public void testCreateDeleteAccount() throws IOException, NoSuchAlgorithmException, InterruptedException {
        AndroidAccountHelper helper = new AndroidAccountHelper(mContext, DemoAuthService.ACCOUNT_TYPE, AUTHORITY);
        helper.removePrimaryAccount(null);
        assertNull("Primary account wasn't removed", helper.getPrimaryAccount());

        // create account
        String accountName = UUID.randomUUID().toString();
        Account account = new Account(accountName, DemoAuthService.ACCOUNT_TYPE);
        helper.addPrimaryAccount(account, true);
        assertNotNull("Primary account wasn't created", helper.getPrimaryAccount());
        helper = new AndroidAccountHelper(mContext, DemoAuthService.ACCOUNT_TYPE, AUTHORITY);
        assertNotNull("Primary account wasn't created", helper.getPrimaryAccount());
        account = helper.getPrimaryAccount();
        assertEquals("Wrong account name", accountName, account.name);
        final CountDownLatch latch = new CountDownLatch(1);
        helper.removePrimaryAccount(
                new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {
                        latch.countDown();
                    }
                });
        latch.await();
        assertNull("Primary account wasn't removed", helper.getPrimaryAccount());
        helper = new AndroidAccountHelper(mContext, DemoAuthService.ACCOUNT_TYPE, AUTHORITY);
        assertNull("Primary account wasn't removed", helper.getPrimaryAccount());
    }
}
