package com.v2soft.AndLib.dataproviders;

/**
 * Task description
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public interface ITask {
    public void execute() throws Exception;
    public void setTaskId(int id);
    public int getTaskId();
    // Task tag - extra piece of information
    public ITask setTaskTag(int id);
    public int getTaskTag();

}
