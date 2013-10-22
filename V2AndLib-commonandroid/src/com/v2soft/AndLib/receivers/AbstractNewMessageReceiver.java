/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.receivers;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Abstract new message notification receiver
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class AbstractNewMessageReceiver extends BroadcastReceiver {
    public static final int NO_NUMBER = Integer.MIN_VALUE;
    /**
     * 
     * @param context
     * @param notificationId 
     * @param number the large number at the right-hand side of the notification. (pass AbstractNewMessageReceiver.AbstractNewMessageReceiver if you don't need it)
     * @throws IOException
     */
    protected void notifyNewMessage(Context context, int notificationId, int number)
            throws IOException {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        //        long when = System.currentTimeMillis();    
        final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, 
                getIntent(context), PendingIntent.FLAG_ONE_SHOT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText(getMessage(context));
        builder.setSmallIcon(getIcon());
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(getTitle(context));
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        if ( number != NO_NUMBER ) {
            builder.setNumber(number);
        }
        Uri sound = getSound(context);
        if ( sound != null ) {
            builder.setSound(sound);
        }
        notificationManager.notify(notificationId, builder.build());
    }
    /**
     * 
     * @param ctx
     * @return Uri to the custom notification sound, or null.
     */
    protected abstract Uri getSound(Context ctx);
    /**
     * 
     * @param ctx
     * @return notification title
     */
    protected abstract CharSequence getTitle(Context ctx);

    protected abstract Intent getIntent(Context context);
    /**
     * 
     * @return notification icon resource id
     */
    protected abstract int getIcon();
    /**
     * 
     * @param ctx
     * @return notification message text
     */
    protected abstract String getMessage(Context ctx);

}
