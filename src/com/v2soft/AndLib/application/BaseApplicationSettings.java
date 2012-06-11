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
