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

import android.app.Application;

/**
 * Base application class
 * @author vshcryabets@gmail.com
 *
 */
public abstract class BaseApplication<S extends BaseApplicationSettings> extends Application {
    //-----------------------------------------------------------------------
    // Constants
    //-----------------------------------------------------------------------
    public static final String LOG_TAG = BaseApplication.class.getSimpleName();
    //-----------------------------------------------------------------------
    // Private fields
    //-----------------------------------------------------------------------
    private S mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (DEV)
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectDiskReads()
//        .detectDiskWrites()
//        .detectNetwork()   // or .detectAll() for all detectable problems
//        .penaltyLog()
//        .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectLeakedSqlLiteObjects()
//        .detectLeakedClosableObjects()
//        .penaltyLog()
//        .penaltyDeath()
//        .build());        
        onCreateSettings(createApplicationSettings());
    }

    protected void onCreateSettings(S settings) {
        if ( settings == null ) {
            throw new NullPointerException("Settings is null");
        }
        mSettings = settings;
    }

    public S getSettings(){return mSettings;}

    protected abstract S createApplicationSettings();
}
