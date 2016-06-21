package com.empire.tigerlibrary.animation;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * manage and check animation active status
 *
 * @author lordvader
 */
public class AnimationActiveChecker {
    private ConcurrentLinkedQueue<Boolean> mActiveAnimationQueue;
    private long mCheckTime;

    public AnimationActiveChecker() {
        mActiveAnimationQueue = new ConcurrentLinkedQueue<Boolean>();
    }

    /**
     * check whether motion is activating
     *
     * @return
     */
    public boolean isAnimationActivating() {
        return mActiveAnimationQueue.size() > 0;
    }

    /**
     * add current activate animation info to queue
     */
    public void add() {
        mActiveAnimationQueue.add(true);
    }

    /**
     * remove animation info from queue;
     */
    public void remove() {
        mActiveAnimationQueue.poll();
    }

    /**
     * clear all animation info form queue;
     */
    public void clear() {
        mActiveAnimationQueue.clear();
    }

    /**
     * set checktime
     */
    public void setCheckTime() {
        mCheckTime = System.currentTimeMillis();
    }

    /**
     * check whether pass check time about given interval
     *
     * @param intervalTime
     * @return
     */
    public boolean isElapsedCheckTime(long intervalTime) {
        return (mCheckTime + intervalTime) < System.currentTimeMillis();
    }
}
