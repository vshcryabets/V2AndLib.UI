/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.RemoteViews;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.v2soft.AndLib.other.R;

/**
 * Background DropbBox service
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class DropboxService 
extends Service {
    // =========================================================
    // Constants
    // =========================================================
    private static final String TAG = DropboxService.class.getSimpleName();
    public static final String KEY_DROPBOXAPIKEY = "dropboxApiKey";
    public static final String KEY_DROPBOXSECRET = "dropboxSecret";
    public static final String KEY_DROPBOXAT1 = "dropboxAT1";
    public static final String KEY_DROPBOXAT2 = "dropboxAT2";
    public static final String KEY_LOCAL = "local";
    public static final String KEY_REMOTE = "remote";
    public static final String KEY_ACTION = "action";
    private static final int HELLO_ID = 1;
    // =========================================================
    // Class fields
    // =========================================================
    private Looper mServiceLooper;
    private ServiceHandler mHandler;
    private DropboxAPI<AndroidAuthSession> mDropboxApi;
    private final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    private UploadRequest mUploadRequest;
    private NotificationManager mNoticationService;

    private class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            Log.d("ServiceHandler", "Got messge "+msg);
            Bundle extras = ((Intent)msg.obj).getExtras();
            int id= 0;
            try {
                id++;
                File localFile = new File(extras.getString(KEY_LOCAL));
                final Notification notification = showNotification(localFile, id, 0,
                        extras.getString(KEY_ACTION));
                startUpload(localFile, extras.getString(KEY_REMOTE), 
                        new DropboxProgressListener(notification, id, localFile.length()));
                updateNotification(1.0f, notification, id);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DropboxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ( mDropboxApi == null ) {
            // initialize dropbox API
            final String dropBoxApiKey = intent.getExtras().getString(KEY_DROPBOXAPIKEY);
            final String dropBoxSecret = intent.getExtras().getString(KEY_DROPBOXSECRET);
            final String dropBoxAT1 = intent.getExtras().getString(KEY_DROPBOXAT1);
            final String dropBoxAT2 = intent.getExtras().getString(KEY_DROPBOXAT2);
            mDropboxApi = createApi(dropBoxApiKey, dropBoxSecret, dropBoxAT1, dropBoxAT2);
            if ( ! mDropboxApi.getSession().authenticationSuccessful() ) {
                throw new IllegalAccessError("Can't connect to dropbox");
            }
        }
        Message msg = new Message();
        msg.arg1 = startId;
        msg.obj = intent;
        mHandler.sendMessage(msg);
        return START_STICKY;
    }

    private DropboxAPI<AndroidAuthSession> createApi(String dropBoxApiKey, String dropBoxSecret, 
            String dropBoxAT1, String dropBoxAT2) {
        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession(dropBoxApiKey, dropBoxSecret, 
                new String[]{dropBoxAT1, dropBoxAT2});
        final DropboxAPI<AndroidAuthSession> dropboxApi = new DropboxAPI<AndroidAuthSession>(session);
        return dropboxApi;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mNoticationService = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        HandlerThread thread = new HandlerThread("TestHandler", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mHandler = new ServiceHandler(mServiceLooper);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    private AndroidAuthSession buildSession(String dropBoxAPIKey, String dropBoxSecretKey, String[] keys) {
        if (keys == null ) {
            throw new NullPointerException("Keys shouldn't be null");
        }
        final AppKeyPair appKeyPair = new AppKeyPair(dropBoxAPIKey, dropBoxSecretKey);
        AndroidAuthSession session;

        final AccessTokenPair accessToken = new AccessTokenPair(keys[0], keys[1]);
        session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        return session;
    } 

    private void startUpload(File localFile, String remote, ProgressListener listener) 
            throws FileNotFoundException, DropboxException {
        long mFileSize = localFile.length();
        final InputStream fin = new FileInputStream(localFile);
        mUploadRequest = mDropboxApi.putFileOverwriteRequest(remote, fin, 
                mFileSize, 
                listener);
        mUploadRequest.upload();
    }

    private class DropboxProgressListener extends ProgressListener {
        private Notification mNotification;
        private int mNotificationId;
        private long mFileSize;

        public DropboxProgressListener(Notification  notification, int id, long fileSize) {
            mNotification = notification;
            mNotificationId = id;
            mFileSize = fileSize;
        }

        @Override
        public long progressInterval() {
            return 500;
        }
        @Override
        public void onProgress(long bytes, long total) {
            //			Log.d(TAG, "Progress="+(int) (bytes/1024));
            updateNotification((float)bytes/(float)mFileSize, mNotification, mNotificationId );
        }
    };

    /**
     * Create and show ongoing notification
     * @param file local file object
     * @param id new notification id
     * @return new notification object
     */
    private Notification showNotification(File file, int id, int pendingRequestCode, String action) {
        final String text = String.format(getString(R.string.v2andlib_uploading_file), 
                file.getName());
        //		.setContentTitle(getString(R.string.v2andlib_dropbox_operation))
        final Notification notification = new Notification(android.R.drawable.ic_dialog_alert, text, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notification.contentView = new RemoteViews(getApplication().getPackageName(), 
                R.layout.v2andlib_ongoing_notification);
        notification.contentView.setImageViewResource(R.id.v2andlib_status_icon, android.R.drawable.ic_dialog_alert);
        notification.contentView.setTextViewText(R.id.v2andlib_status_text, text);
        notification.contentView.setProgressBar(R.id.v2andlib_status_progress, 100, 0, false);

        if ( action != null ) {
            Intent notificationIntent = new Intent(action);
            PendingIntent contentIntent = PendingIntent.getActivity(this, pendingRequestCode, notificationIntent, 0);
            notification.contentIntent = contentIntent;
        }

        mNoticationService.notify(id, notification);
        return notification;
    }

    private void updateNotification(float progress, Notification notification, int notificationId) {
        notification.contentView.setProgressBar(R.id.v2andlib_status_progress, 100, (int) (100*progress), false);
        mNoticationService.notify(notificationId, notification);
    }
}
