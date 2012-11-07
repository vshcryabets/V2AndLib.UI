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
package com.v2soft.V2AndLib.demoapp.tasks;

import android.os.Message;

import com.v2soft.AndLib.dataproviders.ITaskHub;
import com.v2soft.AndLib.dataproviders.tasks.DummyTask;

/**
 * Sample task that sends current time stamp every second
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class StartStopSampleTask extends DummyTask {
    public static final int TASK_MESSAGE_NEWTIME = 1;

    @Override
    public void execute(ITaskHub handler) throws Exception {
        Thread.sleep(500);
        while ( true ) {
            Thread.sleep(1000);
            final Message msg = new Message();
            msg.what = TASK_MESSAGE_NEWTIME;
            msg.obj = System.currentTimeMillis();
            if ( handler != null ) {
                handler.sendMessage(this, msg);
            }
        }
    }
}
