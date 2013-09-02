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
package com.v2soft.V2AndLib.demoapp;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.v2soft.AndLib.application.BaseApplication;

/**
 * Demo application class
 * @author vshcryabets@gmail.com
 *
 */
@ReportsCrashes(formKey = "4EACFC6E-B77D-4555-ACE9-F7B3A96E13C5", mailTo = "info@2vsoft.com")
public class DemoApplication extends BaseApplication<DemoAppSettings> {
    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }

    @Override
    protected DemoAppSettings createApplicationSettings() {
        return new DemoAppSettings(this);
    }

}
