package com.v2soft.V2AndLib.demoapp.ui.activities;

import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.AndLib.ui.fonts.FontManager;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * Base activity for this project
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class DemoBaseActivity extends BaseActivity<DemoApplication, DemoAppSettings> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
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
