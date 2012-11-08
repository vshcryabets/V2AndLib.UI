package com.v2soft.AndLib.dataproviders;

import android.os.Message;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public interface ITaskHub {
    /**
     * Send message to task listener
     * @param from
     * @param message
     */
    void sendMessage(ITask from, Message message);
    /**
     * Cancel execution of the specified task
     * @return true if task was canceled
     */
    boolean cancelTask(ITaskListener listener, ITask taskId, boolean stopIfRunning);
    /**
     * Cancel execution of the task with specified id
     * @return true if task was canceled
     */
    boolean cancelTask(ITaskListener listener, int taskId, boolean stopIfRunning);
}
