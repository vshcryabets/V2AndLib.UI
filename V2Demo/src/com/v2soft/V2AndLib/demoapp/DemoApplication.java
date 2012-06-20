package com.v2soft.V2AndLib.demoapp;

import com.v2soft.AndLib.application.BaseApplication;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoApplication extends BaseApplication<DemoAppSettings> {

    @Override
    protected DemoAppSettings createApplication() {
        return new DemoAppSettings(this);
    }

}
