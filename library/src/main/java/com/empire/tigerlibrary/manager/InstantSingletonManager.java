package com.empire.tigerlibrary.manager;

import java.util.ArrayList;

/**
 * for handling instant singleton which is need to add and remove frequently
 * Created by lordvader on 2016. 4. 7..
 */
public class InstantSingletonManager {
    private static InstantSingletonManager mInstance;
    private ArrayList<SingleTon> mInstantSingleTonList;

    private InstantSingletonManager() {
        mInstantSingleTonList = new ArrayList<SingleTon>();
    }

    /**
     * get InstantSingletonManager instance
     *
     * @return
     */
    public static synchronized InstantSingletonManager getInstane() {
        if (mInstance == null) {
            mInstance = new InstantSingletonManager();
        }

        return mInstance;
    }

    /**
     * check whether InstantSingletonManager instance exist
     *
     * @return
     */
    public static boolean isInstanceExist() {
        return mInstance != null;
    }

    /**
     * clear InstantSingletonManager instance
     */
    public static void clear() {
        mInstance = null;
    }

    /**
     * add singleton instance
     *
     * @param instantSingleTon
     */
    public void add(SingleTon instantSingleTon) {
        mInstantSingleTonList.add(instantSingleTon);
    }

    /**
     * get added instance count
     *
     * @return
     */
    public int getCount() {
        return mInstantSingleTonList.size();
    }

    /**
     * clear all instance
     */
    public void clearAll() {
        for (SingleTon instantSingleTonList : mInstantSingleTonList) {
            if (instantSingleTonList != null) {
                instantSingleTonList.kill();
            }
        }
    }

    /**
     * for tagging short lifecycle singleton for add/release easily
     * Created by lordvader on 2016. 4. 7..
     */
    public static interface SingleTon {
        /**
         * remove instance
         */
        public void kill();
    }
}
