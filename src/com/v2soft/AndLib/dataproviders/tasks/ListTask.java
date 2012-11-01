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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskHub;

/**
 * Task allows to group few tasks in single execution flow
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class ListTask extends DummyTask implements Collection<ITask> {
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
     * 
     * @return number of subtasks
     */
    public int size() {
        return mSubTasks.size();
    }
    public ITask get(int location){
        return mSubTasks.get(location);
    }

    @Override
    public void execute(ITaskHub hub) throws Exception {
        for (ITask subtask : mSubTasks) {
            subtask.execute(hub);
        }
    }
    // ===================================================================
    // Collection interface
    // ===================================================================
    /**
     * Add sub task
     * @param subtask
     * @return
     */
    @Override
    public boolean add(ITask arg0) {
        return mSubTasks.add(arg0);
    }

    @Override
    public boolean addAll(Collection<? extends ITask> arg0) {
        return mSubTasks.addAll(arg0);
    }

    @Override
    public void clear() {
        mSubTasks.clear();
    }

    @Override
    public boolean contains(Object arg0) {
        return mSubTasks.contains(arg0);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return mSubTasks.containsAll(arg0);
    }

    @Override
    public boolean isEmpty() {
        return mSubTasks.isEmpty();
    }

    @Override
    public Iterator<ITask> iterator() {
        return mSubTasks.iterator();
    }

    @Override
    public boolean remove(Object arg0) {
        // TODO Auto-generated method stub
        return mSubTasks.remove(arg0);
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return mSubTasks.removeAll(arg0);
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return mSubTasks.retainAll(arg0);
    }

    @Override
    public Object[] toArray() {
        return mSubTasks.toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return mSubTasks.toArray(arg0);
    }
}
