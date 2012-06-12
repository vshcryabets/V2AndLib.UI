package com.v2soft.AndLib.application;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
    protected HashMap<String, String> mStringSettings;

    public BaseApplicationSettings(Context context) {
        if ( context == null ) {
            throw new NullPointerException("Context is null");
        }
        mContext = context;
        mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        mStringSettings = new HashMap<String, String>();
    }

    /**
     * Load all string values from SharedPreferences
     * @param keyNames
     */
    protected void loadStringSettings(String[] keyNames) {
        for (String string : keyNames) {
            mStringSettings.put(string, mSettings.getString(string, ""));
        }
    }
    /**
     * Load all string values from SharedPreferences
     * @param keyNames
     */
    protected void saveStringSettings() {
        final Editor editor = mSettings.edit();
        for (String key : mStringSettings.keySet()) {
            editor.putString(key, mStringSettings.get(key));
        }
        editor.commit();
    }

    /**
     * Load settings from store
     */
    protected abstract void loadSettings();
    /**
     * Save settings to the store
     */
    public void saveSettings() {
        saveStringSettings();
    }
    /**
     * Get string settings value
     * @param key key name
     * @return
     */
    public String getString(String key) {
        return mStringSettings.get(key);
    }
    /**
     * Put string settings value
     * @param key key name
     * @param value
     */
    public void putString(String key, String value) {
        mStringSettings.put(key, value);
    }
}
