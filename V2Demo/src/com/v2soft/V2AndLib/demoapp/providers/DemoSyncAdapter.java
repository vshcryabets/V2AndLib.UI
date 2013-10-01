package com.v2soft.V2AndLib.demoapp.providers;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * This class demonstrates work of the sync adapter.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com> 
 */
public class DemoSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String EXTRA_TYPE = "type";
    private static final String LOG_TAG = DemoSyncAdapter.class.getSimpleName();

    public DemoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        String type = extras.getString(EXTRA_TYPE);
        for ( int i = 0 ;i < 50; i++ ) {
            Log.d(LOG_TAG, i+" "+type);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }
}
