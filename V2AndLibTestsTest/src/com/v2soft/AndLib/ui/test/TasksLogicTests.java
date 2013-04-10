/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.test;

import android.os.Handler;
import android.os.Message;
import android.test.AndroidTestCase;

import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskListener;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;
import com.v2soft.AndLib.dataproviders.TasksMultiplexor;
import com.v2soft.AndLib.dataproviders.tasks.DummyTask;

/**
 * Unit tests for background tasks classes.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class TasksLogicTests extends AndroidTestCase {
    private class SimpleTask extends DummyTask {
        @Override
        public void execute(ITaskSimpleListener handler) throws Exception {
            Thread.sleep(500);
        }
    }
    private ITaskListener mListener1;
    private ITaskListener mListener2;
    private ITaskListener mListener3;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mListener1 = new ITaskListener() {
            @Override
            public void onMessageFromTask(ITask task, Message message) {
            }
            
            @Override
            public void onTaskFinished(ITask task) {
            }
            
            @Override
            public void onTaskFailed(ITask task, Throwable error) {
            }
        };
    }

    /**
     * Add to tasks
     */
    public void testAddToTaskMultiplexor() {
        try {
            TasksMultiplexor hub = new TasksMultiplexor(new Handler());
            hub.addTask(new SimpleTask(), mListener1);
            assertTrue(hub.hasTaskListener(mListener1));
            hub.addTask(new SimpleTask(), mListener1);
            hub.addTask(new SimpleTask(), mListener1);
            Thread.sleep(5000);
            assertFalse(hub.hasTaskListener(mListener1));
        } catch (Exception e) {
        }
    }
}
