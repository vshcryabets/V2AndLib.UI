package com.v2soft.AndLib.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;

/**
 * Base activity class
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 * @param <T>
 * @param <S>
 */
public abstract class BaseActivity<T extends BaseApplication<S>, S extends BaseApplicationSettings> 
extends FragmentActivity {
    protected T mApp;
    protected S mSettings;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (T)getApplication();
        if ( mApp == null ) {
            throw new NullPointerException("Application is null");
        }
        mSettings = mApp.getSettings();
        if ( mSettings == null ) {
            throw new NullPointerException("Settings is null");
        }
    }
    
    protected FragmentManager getFragmentManager() {
        return getSupportFragmentManager();
    }
}
