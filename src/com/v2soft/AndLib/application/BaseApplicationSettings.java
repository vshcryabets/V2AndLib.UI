package com.v2soft.AndLib.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Base application settings class 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class BaseApplicationSettings {
    //-----------------------------------------------------------------------
    // Private fields
    //-----------------------------------------------------------------------
    protected SharedPreferences mSettings;
    protected Context mContext;

    public BaseApplicationSettings(Context context) {
        if ( context == null ) {
            throw new NullPointerException("Context is null");
        }
        mContext = context;
        mSettings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Load settings from store
     */
    protected abstract void loadSettings();
    /**
     * Save settings to the store
     */
    public abstract void saveSettings();
}
