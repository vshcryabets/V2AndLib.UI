package com.v2soft.AndLib.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.dataproviders.ITaskHub;
import com.v2soft.AndLib.ui.fonts.FontManager;

/**
 * Base activity class
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 * @param <T> application class
 * @param <S> settings class
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
    protected ITaskHub mBackgroundExecutor;

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
    /**
     * @return assigned background xecution thread
     */
    public ITaskHub getExecutor() {
        return mBackgroundExecutor;
    }
    // =================================================================
    // UI routines
    // =================================================================
    /**
     * @return application custom font manager
     */
    public FontManager getFontManager() {
        return mApp.getFontManager();
    }
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
