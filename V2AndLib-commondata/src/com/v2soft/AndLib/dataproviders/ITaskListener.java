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
package com.v2soft.AndLib.dataproviders;


/**
 * Task listener iterface
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public interface ITaskListener<T extends ITask,M> extends ITaskSimpleListener<M> {
    /**
     * Will be called if task finished without exception
     * @param task
     */
    void onTaskFinished(T task);
    /**
     * Will be called if task finished with exception
     * @param task
     */
    void onTaskFailed(T task, Throwable error);
}
