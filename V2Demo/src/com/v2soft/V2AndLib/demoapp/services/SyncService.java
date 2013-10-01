package com.v2soft.V2AndLib.demoapp.services;

import com.v2soft.V2AndLib.demoapp.providers.DemoSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Demo service.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class SyncService extends Service {

    private static DemoSyncAdapter mSyncAdapter = null;

    public SyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mSyncAdapter == null) {
            mSyncAdapter = new DemoSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
