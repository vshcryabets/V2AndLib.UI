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

import java.util.ArrayList;
import java.util.List;

import com.v2soft.AndLib.dataproviders.ITask;

/**
 * Task allows to group few tasks in single execution flow
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class ListTask extends DummyTask {
    private List<ITask> mSubTasks;
    
    public ListTask() {
        mSubTasks = new ArrayList<ITask>();
    }
    
    public ListTask(ITask ... tasks) {
        mSubTasks = new ArrayList<ITask>();
        for (ITask iTask : tasks) {
            mSubTasks.add(iTask);
        }
    }
    /**
     * Add sub task
     * @param subtask
     * @return
     */
    public boolean addSubTask(ITask subtask) {
        return mSubTasks.add(subtask);
    }

    @Override
    public void execute() throws Exception {
        for (ITask subtask : mSubTasks) {
            subtask.execute();
        }
    }

}
