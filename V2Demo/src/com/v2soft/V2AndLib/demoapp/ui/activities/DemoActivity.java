package com.v2soft.V2AndLib.demoapp.ui.activities;

import android.widget.Toast;

import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * Base class for all project activities
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class DemoActivity extends BaseActivity<DemoApplication, DemoAppSettings> {

    public abstract String getSampleName();
    
    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
