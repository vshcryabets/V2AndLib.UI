package com.v2soft.AndLib.tricks;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * System tricks class.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class DataConnection {

    /**
     * Disable/enable data connection over Mobile network.
     * This code sample should work for android phones running gingerbread and higher.
     * http://stackoverflow.com/questions/11555366/enable-disable-data-connection-in-android-programmatically
     * Dont forget to add this line to your manifest file
     * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
     * @param context
     * @param enabled
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static void setMobileDataEnabled(Context context, boolean enabled) 
            throws ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, 
            NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class<?> conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class<?> iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }
}
