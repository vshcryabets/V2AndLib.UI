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

import dagger.ObjectGraph;

/**
 * @author vshcryabets@gmail.com
 */
public abstract class BaseInjector {
    protected ObjectGraph mGraph;
    protected static BaseInjector INSTANCE;

    public static void setInjector(BaseInjector injector) {
        INSTANCE = injector;
    }
    public static BaseInjector getInstance() {
        return INSTANCE;
    }
    public void init(Object... modules) {
        mGraph = ObjectGraph.create(modules);
    }

    public void inject(Object target) {
        mGraph.inject(target);
    }
}
