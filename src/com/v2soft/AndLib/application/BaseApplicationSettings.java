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
    /**
     * Check does the specified key stored in settings
     * @param key key name
     * @return true if does
     */
    public boolean isKeyStored(String key) {
    	return mStringSettings.containsKey(key);
    }
}
