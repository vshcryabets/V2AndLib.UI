/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.V2AndLib.demoapp;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationModule;
import com.v2soft.V2AndLib.demoapp.ui.fragments.SamplesList;

import dagger.Module;
import dagger.Provides;

/**
 * Demo application dependency injection module.
 * @author vshcryabets@gmail.com
 *
 */
@Module (
        library = true,
        overrides = true,
        includes = {
                BaseApplicationModule.class
        },
        injects= {
                SamplesList.class
        }
)
public class DemoApplicationModule  {
    protected BaseApplication mApplication;
    private DemoAppSettings mSettings;

    public DemoApplicationModule(BaseApplication application, DemoAppSettings settings) {
        if ( settings == null ) {
            throw new NullPointerException("Settings is null");
        }
        mApplication = application;
        mSettings = settings;
    }
    @Provides
    BaseApplication provideApplication() {
        return mApplication;
    }
    @Provides
    DemoAppSettings provideSettings() {
        return mSettings;
    }
}
