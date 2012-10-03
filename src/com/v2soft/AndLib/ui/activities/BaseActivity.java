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
 * @param <T> application class
 * @param <S> settings class
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
    // =================================================================
    // UI routines
    // =================================================================
    /**
     * Show error to user 
     * @param message
     */
    public abstract void showError(String message);
    /**
     * Show error to user 
     * @param message
     */
    public void showError(int messageResource) {
        showError(getString(messageResource));
    }
    /**
     * This method will show some kind of a unblocking progress view, that means that background data loading process is ongoing
     * @param value
     */
    public abstract void setLoadingProcess(boolean value, Object tag);
    /**
     * This method will show some kind of a progress dialog, that block the user
     * @param value
     */
    public abstract void setBlockingProcess(boolean value, Object tag);
}
