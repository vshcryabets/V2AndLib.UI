package com.v2soft.V2AndLib.demoapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;

import com.v2soft.AndLib.dataproviders.AbstractDataRequest;

/**
 * Background request execution service.
 *
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DemoExecutionService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mHandler;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Message msg = new Message();
        msg.arg1 = startId;
        msg.obj = intent;
        mHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("WoundedWarriorServiceHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mHandler = new ServiceHandler(mServiceLooper, this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        DemoExecutionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DemoExecutionService.this;
        }
    }

    public int addTask(AbstractDataRequest<?,?,?> task) {
        return 0;
    }
}
