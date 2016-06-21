package com.empire.tigerlibrary.animation;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * timer for checking animation of AnimationDrawable type end
 * Created by lordvader on 2015. 12. 3..
 */
public abstract class AnimationDrawableEndListener extends TimerTask {
    private Activity mActivity;
    private Timer mTimer;
    private AnimationDrawable mAnimationDrawable;

    public AnimationDrawableEndListener(Activity activity, AnimationDrawable animationDrawable) {
        mActivity = activity;
        mAnimationDrawable = animationDrawable;
        mTimer = new Timer();
    }

    /**
     * calculate animation duration using by each frame's duration and total frame count
     *
     * @return
     */
    private int calculateDuration() {
        int duration = 0;
        int numberOfFrames = mAnimationDrawable.getNumberOfFrames();

        for (int i = 0; i < numberOfFrames; i++) {
            duration += mAnimationDrawable.getDuration(i);
        }
        return duration;
    }

    /**
     * start timer by duration
     */
    public void start() {
        mTimer.schedule(this, calculateDuration());
    }

    @Override
    public void run() {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    onAnimationEnd();
                }
            });
        }
    }

    /**
     * for callback which is run at animation end time
     */
    public abstract void onAnimationEnd();
}
