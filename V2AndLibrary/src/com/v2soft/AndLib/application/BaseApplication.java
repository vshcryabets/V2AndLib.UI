/*
 * Copyright (C) 2012-2013 V.Shcryabets (vshcryabets@gmail.com)
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

import com.v2soft.AndLib.ui.fonts.FontManager;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;

/**
 * Base application class
 * @author vshcryabets@gmail.com
 *
 */
public abstract class BaseApplication<S extends BaseApplicationSettings<?>> extends Application {
    //-----------------------------------------------------------------------
    // Constants
    //-----------------------------------------------------------------------
    public static final String LOG_TAG = BaseApplication.class.getSimpleName();
    //-----------------------------------------------------------------------
    // Private fields
    //-----------------------------------------------------------------------
    private S mSettings;
    protected FontManager mFontManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mFontManager = new FontManager(this);
        onCreateSettings(createApplicationSettings());
        mSettings.loadSettings();
    }

    protected void onCreateSettings(S settings) {
        if ( settings == null ) {
            throw new NullPointerException("Settings is null");
        }
        mSettings = settings;
    }
    /**
     * Initialize development mode (in this mode many leaks will be logged)
     */
    protected void initDevMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectAll()
        .penaltyLog()
        .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
    }
    
    /**
     * 
     * @return The version name of this package, as specified by the <manifest> tag's versionName attribute.
     */
    public String getApplicationBuildVersion() {
        try {
            PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
            return manager.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * @return application custom font manager
     */
    public FontManager getFontManager() {
        return mFontManager;
    }
    /**
     * 
     * @return settings holder object
     */
    public S getSettings(){return mSettings;}

    protected abstract S createApplicationSettings();
}
