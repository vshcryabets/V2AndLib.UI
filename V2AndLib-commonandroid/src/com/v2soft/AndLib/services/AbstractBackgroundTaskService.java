package com.v2soft.AndLib.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.v2soft.AndLib.dataproviders.ITask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vshcryabets@gmail.com
 */
public abstract class AbstractBackgroundTaskService extends Service {

    protected Map<Integer, ITask<?>> mTasksMap;
    protected List<ITask<?>> mTasks;
    protected Integer mCounter;


    @Override
    public void onCreate() {
        super.onCreate();
        mCounter = 1;
        mTasksMap = new HashMap<Integer, ITask<?>>();
        mTasks = new ArrayList<ITask<?>>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
