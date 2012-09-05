package com.v2soft.AndLib.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

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
extends Activity {
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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mCustomStack != null ) {
            mCustomStack.onSaveInstanceState(outState);
        }
    }
    
    public IBackStack getBackStack() {
        return mCustomStack;
    }
}
