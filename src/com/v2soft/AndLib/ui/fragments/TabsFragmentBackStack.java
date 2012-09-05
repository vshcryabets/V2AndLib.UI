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

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import com.v2soft.AndLib.ui.activities.BaseActivity.IBackStack;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Specially organized back stack for fragment tabs
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class TabsFragmentBackStack 
implements IBackStack {
    public interface TabsFragmentBackStackListener {
        Fragment onNewTabOpened(String tag);
        void onStartFragment(Fragment fragment);
        void onActivatedTab(String tag);
    }
    private static final String KEY_CURRENT_TAB = "currentTab";
    private static final String KEY_TABSTACK_STATE = "tabStackState";
    private static final String KEY_TAB_NAMES = "tabNames";
    private static final String KEY_FRAGMENT_CLASS = "fragmentClass";
    private static final String KEY_CLASS_NAMES = "classNames";
    private static final String KEY_ARGUMENTS = "arguments";
    private Context mContext;
    private HashMap<String, Stack<Fragment>> mTabs;
    private Stack<Fragment> mCurrentStack;
    private TabsFragmentBackStackListener mListener;
    private String mCurrentTabTag;

    public TabsFragmentBackStack(Context context, TabsFragmentBackStackListener listener) {
        if ( listener == null ) {
            throw new NullPointerException("Listener shpuld not be null");
        }
        mListener = listener;
        mContext = context;
        mTabs = new HashMap<String, Stack<Fragment>>();
    }
    /**
     * Show fragment in current tab
     * @param fragment
     */
    @Override
    public void startFragment(Fragment fragment) {
        startFragment(fragment, null, true);
    }
    /**
     * Show fragment at the specified tab
     * @param fragment
     * @param tabTag
     */
    @Override
    public void startFragmentAt(Fragment fragment, String where) {
        startFragment(fragment, where, true);
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
    @Override
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final Bundle tabStackState = new Bundle();
        // store current tab
        tabStackState.putString(KEY_CURRENT_TAB, mCurrentTabTag);
        // store all tab names
        final Set<String> keys = mTabs.keySet();
        final String [] tabNames = keys.toArray(new String[keys.size()]);
        tabStackState.putStringArray(KEY_TAB_NAMES, tabNames);
        // serialize fragment stacks
        for (String string : keys) {
            final Stack<Fragment> tabStack = mTabs.get(string);
            final Bundle bundle = new Bundle();
            final String [] classNames = new String[tabStack.size()];
            int pos = 0;
            for (Fragment fragment : tabStack) {
                final String classname = fragment.getClass().getName();
                classNames[pos] = classname;
                final Bundle fragmentData = new Bundle();
                fragmentData.putBundle(KEY_ARGUMENTS, fragment.getArguments());
                bundle.putBundle(String.valueOf(pos), fragmentData);
                pos++;
            }
            bundle.putStringArray(KEY_CLASS_NAMES, classNames);
            tabStackState.putBundle(string, bundle);
        }
        outState.putBundle(KEY_TABSTACK_STATE, tabStackState);
    }
    @Override
    public void onRestoreInstanceState(Bundle state) {
        final Bundle bundle = state.getBundle(KEY_TABSTACK_STATE);
        mTabs = new HashMap<String, Stack<Fragment>>();
        if ( bundle != null ) {
            final String []tabNames = bundle.getStringArray(KEY_TAB_NAMES);
            for (String string : tabNames) {
                final Bundle tabBundle = bundle.getBundle(string);
                final String classNames[] = tabBundle.getStringArray(KEY_CLASS_NAMES);
                final Stack<Fragment> stack = new Stack<Fragment>();
                int pos = 0;
                for (String classname : classNames) {
                    final Bundle fragmentData = tabBundle.getBundle(String.valueOf(pos));
                    final Fragment fragment = Fragment.instantiate(mContext, classname);
                    fragment.setArguments(fragmentData.getBundle(KEY_ARGUMENTS));
                    stack.push(fragment);
                    pos++;
                }
                mTabs.put(string, stack);
            }
            activateTab(bundle.getString(KEY_CURRENT_TAB));
        }
    }
}
