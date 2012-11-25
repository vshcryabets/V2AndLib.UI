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
package com.v2soft.AndLib.dataproviders.tasks;

import com.v2soft.AndLib.dataproviders.ITask;

/**
 * Abstract class, that handles work with task tags, id's etc.
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class DummyTask implements ITask {
    private int mId;
    private int mTag;
    private Object mTagObj;
    protected boolean isCanceled;
    // ===========================================================
    // Getters
    // ===========================================================
    @Override
    public int getTaskId() {
        return mId;
    }
    @Override
    public int getTaskTag() {
        return mTag;
    }
    @Override
    public Object getTaskTagObject() {
        return mTagObj;
    }
    // ===========================================================
    // Setters
    // ===========================================================
    @Override
    public void setTaskId(int id) {
        mId = id;
    }
    @Override
    public ITask setTaskTag(int id) {
        mTag = id;
        return this;
    }
    @Override
    public ITask setTaskTagObject(Object tag) {
        mTagObj = tag;
        return this;
    }
    // ===========================================================
    // Commands
    // ===========================================================
    public void cancelTask() {
        isCanceled = true;
    }
}
