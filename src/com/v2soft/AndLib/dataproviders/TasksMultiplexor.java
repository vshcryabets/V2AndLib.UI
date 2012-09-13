package com.v2soft.AndLib.dataproviders;

import android.os.Handler;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Распределитель фоновых задач
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class TasksMultiplexor {
    private Object mLock = new Object();
    private int mLastId = 1;
    private RunnableQueueExecutor mExecutor;
    private SparseArray<ITaskListener> mListeners;
    private Map<ITaskListener, List<ITask>> mTasksByListener;
    private Handler mHandler;

    public TasksMultiplexor(Handler handler) {
        mExecutor = new RunnableQueueExecutor("BGTasks", 20, this);
        mExecutor.start();
        mListeners = new SparseArray<ITaskListener>();
        mTasksByListener = new HashMap<ITaskListener, List<ITask>>();
        mHandler = handler;
    }

    /**
     * @param task
     * @return new task id
     */
    public int addTask(ITask task, ITaskListener listener) {
        synchronized (mLock) {
            mLastId++;
            task.setTaskId(mLastId);
            mListeners.append(mLastId, listener);
            if ( mTasksByListener.containsKey(listener)) {
                final List<ITask> tasks = mTasksByListener.get(listener);
                tasks.add(task);
            } else {
                final List<ITask> tasks = new LinkedList<ITask>();
                tasks.add(task);
                mTasksByListener.put(listener, tasks);
            }
            mExecutor.post(task);
            return mLastId;
        }
    }

    public void cancelTask(int taskId) {
        // TODO
    }

    public void handleFinished(final ITask task) {
        final ITaskListener listener = mListeners.get(task.getTaskId());
        if ( listener != null ) {
            removeTask(task, listener);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskFinished(task);
                }
            });
        }
    }

    private void removeTask(ITask task, ITaskListener listener) {
        mListeners.remove(task.getTaskId());
        if ( mTasksByListener.containsKey(listener)) {
            final List<ITask> tasks = mTasksByListener.get(listener);
            tasks.remove(task);
        }
    }

    public void handleException(final ITask task, final Exception e) {
        final ITaskListener listener = mListeners.get(task.getTaskId());
        if ( listener != null ) {
            removeTask(task, listener);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskFailed(task, e);
                }
            });
        }
    }

    /**
     * Отсоединение от задач
     * @param listener
     * @return
     */
    public int[] detachFromTasks(ITaskListener listener) {
        if ( mTasksByListener.containsKey(listener)) {
            final List<ITask> tasks = mTasksByListener.get(listener);
            int [] res = new int[tasks.size()];
            for (int i = 0; i < tasks.size(); i++ ) {
                res[i] = tasks.get(i).getTaskId();
                mListeners.remove(res[i]);
            }
            mTasksByListener.remove(listener);
            return res;
        }
        return new int[]{};
    }

    public void attachToTasks(ITaskListener listener, int[] taskIds) {
        // TODO
    }
}
