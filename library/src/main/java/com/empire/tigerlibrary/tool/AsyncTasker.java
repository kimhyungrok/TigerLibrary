package com.empire.tigerlibrary.tool;

import android.os.AsyncTask;

import com.empire.tigerlibrary.manager.AsynchTaskManager;

/**
 * for handling AsyncTask instance with AsynchTaskManager
 * Created by lordvader on 16. 9. 13..
 */
public abstract class AsyncTasker<B, V, B1> extends AsyncTask<B, V, B1> {
    public AsyncTasker() {
        AsynchTaskManager.getInstane().add(this);
    }

    @Override
    protected void onPostExecute(B1 b1) {
        AsynchTaskManager.getInstane().remove(this);
    }
}
