package com.v2soft.AndLib.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    // =================================================================
    // Interfaces
    // =================================================================
    public interface IBackStack {
        void startFragmentAt(Fragment fragment, String where);
        void startFragment(Fragment fragment);
        boolean onBackPressed();
        void onSaveInstanceState(Bundle outState);
        void onRestoreInstanceState(Bundle state);
    }
    // =================================================================
    // Class fields
    // =================================================================
    protected T mApp;
    protected S mSettings;
    protected IBackStack mCustomStack;

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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mCustomStack != null ) {
            mCustomStack.onSaveInstanceState(outState);
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if ( mCustomStack != null ) {
            mCustomStack.onRestoreInstanceState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    public IBackStack getBackStack() {
        return mCustomStack;
    }
}
