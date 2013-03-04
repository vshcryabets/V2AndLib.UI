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
package com.v2soft.V2AndLib.demoapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.actionbarsherlock.view.MenuItem;
import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.ui.fragments.DemoEndlessList;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class EndlessListActivity extends BaseActivity<DemoApplication, DemoAppSettings> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.v2andlib_single_fragment);
        if ( savedInstanceState == null ) {
            Fragment fragment = DemoEndlessList.newInstance();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.v2andLibFragment, fragment);
            trans.commit();
        }
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "Endless list demo";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//        case android.R.id.home:
//            finish();
//            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void showError(String message) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void setLoadingProcess(boolean value, Object tag) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void setBlockingProcess(boolean value, Object tag) {
        // TODO Auto-generated method stub
        
    }
}
