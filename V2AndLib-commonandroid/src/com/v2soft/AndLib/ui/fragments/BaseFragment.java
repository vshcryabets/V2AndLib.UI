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
package com.v2soft.AndLib.ui.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.ui.activities.IBaseActivity;
import com.v2soft.AndLib.ui.fonts.FontManager;

/**
 * Base fragment class
 * @author diacht
 * @author V.Shcryabets<vshcryabets@gmail.com>
 * @param <T> application class
 * @param <S> settings class
 */
public abstract class BaseFragment<T extends BaseApplication<S>, S extends BaseApplicationSettings<?>> 
extends SherlockFragment 
implements OnClickListener {
    protected T mApp;
    protected S mSettings;

    public static final int DIALOG_FILE_SEND_SERVICE = 10;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Activity activity) {
        mApp = (T) activity.getApplication();
        if ( mApp == null ) {
            throw new NullPointerException("Application is null");
        }
        mSettings = mApp.getSettings();
        if ( mSettings == null ) {
            throw new NullPointerException("Settings is null");
        }
        super.onAttach(activity);
    }

    /**
     * Subscribe to specified OnClick events
     * @param ids array of view identifiers.
     */
    protected void registerOnClickListener(int[] ids, View inview) {
		registerOnClickListener(ids, inview, this);
    }
	/**
	 * Subscribe to specified OnClick events
	 * @param ids array of view identifiers.
	 */
	protected void registerOnClickListener(int[] ids, View inview, OnClickListener listener) {
		for (int i : ids) {
			final View view = inview.findViewById(i);
			if ( view == null ) {
				throw new NullPointerException("Can't found view with id "+i);
			}
			view.setOnClickListener(listener);
		}
	}
    /**
     * Start new fragment in specified container view with custom animations
     * @param resId container view resource ID
     * @param fragment new fragment object
     * @param addToStack
     * @param stackTag
     * @param inAnimation animation resources to run for the fragments that are entering
     * @param outAnimation animation resources to run for the fragments that are exiting
     * @param popAnimation animation resources to run for the fragments that are pushing to backstack
     * @param pushAnimation animation resources to run for the fragments that are popping from backstack
     */
    protected void startFragment(int resId, Fragment fragment, 
            boolean addToStack, String stackTag,
            int inAnimation, int outAnimation, 
            int pushAnimation, int popAnimation) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(inAnimation, outAnimation, pushAnimation, popAnimation);
        ft.replace(resId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if ( addToStack ) {
            ft.addToBackStack(stackTag);
        }
        ft.commit();
    }
    /**
     * Handle result that was returned from sub-fragment
     * @param data
     * @param requestCode
     */
    public void onFragmentResult(Object data, int requestCode) {
        
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
    public void showError(String message) {
        if ( getActivity() instanceof IBaseActivity ) {
            final IBaseActivity<?> activity = (IBaseActivity<?>) getActivity();
            activity.showError(message);
        }
    }
    /**
     * Show error to user 
     * @param messageResource
     */
    public void showError(int messageResource) {
        showError(getString(messageResource));
    }
    /**
     * This method will show some kind of a unblocking progress view, that means that background data loading process is ongoing
     * @param value
     */
    public void setLoadingProcess(boolean value, Object tag) {
        if ( getActivity() instanceof IBaseActivity) {
            final IBaseActivity<?> activity = (IBaseActivity<?>) getActivity();
            activity.setLoadingProcess(value, tag);
        }
    }
    /**
     * This method will show some kind of a progress dialog, that block the user
     * @param value
     */
    public void setBlockingProcess(boolean value, Object tag) {
        if ( getActivity() instanceof IBaseActivity) {
            final IBaseActivity<?> activity = (IBaseActivity<?>) getActivity();
            activity.setBlockingProcess(value, tag);
        }
    }

	/**
	 * Clear framents back stack.
	 */
	public void clearBackStack() {
		// clear back stack
		FragmentManager fm = getFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}
	}
}
