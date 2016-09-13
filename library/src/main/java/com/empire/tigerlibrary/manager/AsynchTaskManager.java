package com.empire.tigerlibrary.manager;

import android.os.AsyncTask;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * handle AsyncTask	instance
 * Created by lordvader on 16. 9. 13..
 */
public class AsynchTaskManager implements InstantSingletonManager.SingleTon {
    private static AsynchTaskManager sInstance;
    private ConcurrentLinkedQueue<AsyncTask> asyncTaskQueue = new ConcurrentLinkedQueue<AsyncTask>();

    /**
     * get AsynchTaskManager instance
     *
     * @return
     */
    public static synchronized AsynchTaskManager getInstane() {
        if (sInstance == null) {
            sInstance = new AsynchTaskManager();
            InstantSingletonManager.getInstane().add(sInstance);
        }

        return sInstance;
    }

    /**
     * check whether AsynchTaskManager instance exist
     *
     * @return
     */
    public static boolean isInstanceExist() {
        return sInstance != null;
    }

    /**
     * clear AsynchTaskManager instance
     */
    public static void clear() {
        sInstance = null;
    }

    @Override
    public void kill() {
        if (isInstanceExist()) {
            clearAll();
            clear();
        }
    }

    /**
     * add AsyncTask instance
     *
     * @param asyncTask
     */
    public void add(AsyncTask asyncTask) {
        if (asyncTask != null && asyncTask instanceof AsyncTask) {
            asyncTaskQueue.add(asyncTask);
        }
    }

    /**
     * get AsyncTask instance item count
     *
     * @return
     */
    public int getCount() {
        return asyncTaskQueue.size();
    }

    /**
     * remove AsyncTask instance item
     *
     * @param asyncTask
     */
    public void remove(AsyncTask asyncTask) {
        if (asyncTask != null && asyncTask instanceof AsyncTask && asyncTaskQueue.contains(asyncTask)) {
            asyncTaskQueue.remove(asyncTask);
        }
    }

    /**
     * remove all AsyncTask instance item
     */
    public void clearAll() {
        for (AsyncTask asyncTask : asyncTaskQueue) {
            asyncTask.cancel(true);
        }

        if (asyncTaskQueue.size() > 0) {
            asyncTaskQueue.clear();
        }
    }
}

