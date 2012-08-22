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
package com.v2soft.AndLib.ui.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;

/**
 * Base fragment class
 * @author diacht
 *
 */
public abstract class BaseFragment<T extends BaseApplication<S>, S extends BaseApplicationSettings> 
extends Fragment 
implements OnClickListener {
    protected T mApp;
    protected S mSettings;

    public static final int DIALOG_FILE_SEND_SERVICE = 10;

    @Override
    public void onAttach(Activity activity) {
        mApp = (T) activity.getApplication();
        if ( mApp == null ) throw new NullPointerException("Application is null");
        mSettings = mApp.getSettings();
        if ( mSettings == null ) throw new NullPointerException("Settings is null");
        super.onAttach(activity);
    }

    /**
     * Метод для подписки на OnClick события
     * @param is массив идентификаторов вьюшек на которые нужно подписаться
     */
    protected void registerOnClickListener(int[] is, View inview) {
        for (int i : is) {
            final View view = inview.findViewById(i);
            if ( view == null ) throw new NullPointerException("Can't get view with id "+i);
            view.setOnClickListener(this);
        }
    }

    /**
     * Start new fragment in specified container view
     * @param resId container view resource ID
     * @param fragment new fragment object
     * @param addToStack
     * @param stackTag
     */
    protected void startFragment(int resId, Fragment fragment, 
            boolean addToStack, String stackTag) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(resId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if ( addToStack ) {
            ft.addToBackStack(stackTag);
        }
        ft.commit();
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
            int popAnimation, int pushAnimation) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(inAnimation, outAnimation, popAnimation, pushAnimation);
        ft.replace(resId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if ( addToStack ) {
            ft.addToBackStack(stackTag);
        }
        ft.commit();
    }    
}
