package com.v2soft.AndLib.dataproviders;


/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public interface ITaskHub extends ITaskSimpleListener {
    /**
     * Add task to execution queue
     * @param task
     * @return new task id
     */
    public int addTask(ITask task, ITaskListener listener);
    // TODO write description
    public void attachToTasks(ITaskListener listener, int[] taskIds);
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
    /**
     * Detach listener from specified tasks
     * @param listener
     * @return
     */
    public int[] detachFromTasks(ITaskListener listener);
    /**
     * Cancel all task of specified listeners. Note that listener after that operation 
     * will not got any callback call 
     * @param listener
     */
    public void cancelAllTasksByListener(ITaskListener listener, boolean stopIfRunning);
}
