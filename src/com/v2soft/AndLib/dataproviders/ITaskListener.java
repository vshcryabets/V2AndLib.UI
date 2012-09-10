package com.v2soft.AndLib.dataproviders;

/**
 * Task listener iterface
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public interface ITaskListener {
    /**
     * Will be called if task finished without exception
     * @param task
     */
    void onTaskFinished(ITask task);
    /**
     * Will be called if task finished with exception
     * @param task
     */
    void onTaskFailed(ITask task, Throwable error);
}
