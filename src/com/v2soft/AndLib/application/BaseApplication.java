package com.v2soft.AndLib.application;

import android.app.Application;

/**
 * Base application class
 * @author vshcryabets@gmail.com
 *
 */
public class BaseApplication<S extends BaseApplicationSettings> extends Application {
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
        onCreateSettings(null);
    }
    
    protected void onCreateSettings(S settings) {
        if ( settings == null ) {
            throw new NullPointerException("Settings is null");
        }
        mSettings = settings;
    }

    public S getSettings(){return mSettings;}
}
