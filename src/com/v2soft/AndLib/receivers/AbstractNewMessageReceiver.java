package com.v2soft.AndLib.receivers;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Abstract new message notification receiver
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class AbstractNewMessageReceiver extends BroadcastReceiver {
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi") 
    protected void notifyNewMessage(Context context, int notificationId)   
            throws IOException {
        final String message = getMessage(context);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();    
        final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, 
                getIntent(context), PendingIntent.FLAG_ONE_SHOT);
        Notification notification = null;
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
            notification = new Notification.Builder(context)
            .setContentText(message)
            .setSmallIcon(getIcon())
            .setContentIntent(contentIntent)
            .setContentTitle(getTitle(context)) 
            .setDefaults(Notification.DEFAULT_ALL) 
            .getNotification();
        } else{
            // FIXME remove this block in msater branch
            notification = new Notification(getIcon(), message, when);
            notification.defaults = Notification.DEFAULT_ALL;
            notification.setLatestEventInfo(context, getTitle(context), message, contentIntent);
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, notification);
    }

    protected abstract CharSequence getTitle(Context ctx);

    protected abstract Intent getIntent(Context context);

    protected abstract int getIcon();

    protected abstract String getMessage(Context ctx);

}
