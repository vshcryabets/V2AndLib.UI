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
package com.v2soft.AndLib.application;


import com.v2soft.AndLib.ui.fonts.FontManager;
import com.v2soft.AndLib.ui.views.TextViewWithFont;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application dependency injection module.
 * @author vshcryabets@gmail.com
 *
 */
@Module (
        library = true, complete = false,
        injects = {
                TextViewWithFont.class
        }
)
public class BaseApplicationModule {

    @Provides @Singleton
    FontManager provideFontManager(BaseApplication application) {
        return new FontManager(application);
    }
}
