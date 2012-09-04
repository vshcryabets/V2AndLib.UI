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

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Stack;


/**
 * Specially organized back stack for fragment tabs
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class TabsFragmentBackStack {
    public interface TabsFragmentBackStackListener {
        Fragment onNewTabOpened(String tag);
        void onStartFragment(Fragment fragment);
        void onActivatedTab(String tag);
    }
    private HashMap<String, Stack<Fragment>> mTabs;
    private Stack<Fragment> mCurrentStack;
    private TabsFragmentBackStackListener mListener;
    private String mCurrentTabTag;

    public TabsFragmentBackStack(TabsFragmentBackStackListener listener) {
        if ( listener == null ) {
            throw new NullPointerException("Listener shpuld not be null");
        }
        mListener = listener;
        mTabs = new HashMap<String, Stack<Fragment>>();
    }
    /**
     * Show fragment in current tab
     * @param fragment
     */
    public void startFragmentInCurrentTab(Fragment fragment) {
        startFragment(fragment, null, true);
    }
    /**
     * Show fragment at the specified tab
     * @param fragment
     * @param tabTag
     */
    public void startFragment(Fragment fragment, String tabTag) {
        startFragment(fragment, tabTag, true);
    }
    /**
     * Show fragment at the specified tab
     * @param fragment new fragment
     * @param tabTag tab tag
     * @param pushToStack true if you want push this fragment to back stack
     */
    public void startFragment(Fragment fragment, String tabTag, boolean pushToStack) {
        if ( tabTag != null && !mCurrentTabTag.equals(tabTag)) {
            activateTab(tabTag);
        }
        mListener.onStartFragment(fragment);
        if ( pushToStack ) {
            mCurrentStack.push(fragment);
        }
    }

    /**
     * Handle back button
     * @return true if button was handled
     */
    public boolean onBackPressed() {
        if ( mCurrentStack.size() > 1 ) {
            mCurrentStack.pop();
            startFragment(mCurrentStack.lastElement(), null, false);
            return true;
        }
        return false;
    }
    
    /**
     * Show specified tab
     * @param tag tab tag
     */
    public void activateTab(String tag) {
        if ( tag.equals(mCurrentTabTag) ) {
            return; // already at this tab
        }
        if ( !mTabs.containsKey(tag)) {
            final Fragment tabHome = mListener.onNewTabOpened(tag);
            if ( tabHome == null ) {
                throw new NullPointerException("Tab home is null");
            }
            final Stack<Fragment> tabStack = new Stack<Fragment>();
            tabStack.push(tabHome);
            mTabs.put(tag, tabStack);
        }
        mCurrentTabTag = tag;
        mCurrentStack = mTabs.get(tag);
        startFragment(mCurrentStack.lastElement(), mCurrentTabTag, false);
        mListener.onActivatedTab(tag);
    }
}
