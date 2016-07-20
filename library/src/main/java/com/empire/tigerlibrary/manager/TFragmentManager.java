package com.empire.tigerlibrary.manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.animation.CustomAnimationListener;
import com.empire.tigerlibrary.tool.SimpleActionListener;

/**
 * sub fragment manager
 * Created by lordvader on 2015. 8. 28..
 */
public class TFragmentManager implements InstantSingletonManager.SingleTon {
    private static TFragmentManager sInstance;
    private final int ANIMATION_DURATION = 150;
    private FragmentManager mFragmentManager;
    private View mUiTargetView;
    private UiAnimationListener mUiAnimationListener;
    private boolean mIsApplyCustomAnimation;

    private TFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    /**
     * get SubFragmentManager instance
     *
     * @return
     */
    public static synchronized TFragmentManager getInstane(FragmentManager fragmentManager) {
        if (sInstance == null) {
            sInstance = new TFragmentManager(fragmentManager);
            InstantSingletonManager.getInstane().add(sInstance);
        }

        return sInstance;
    }

    /**
     * check whether TaskFragmentManager instance exist
     *
     * @return
     */
    public static boolean isInstanceExist() {
        return sInstance != null;
    }

    /**
     * clear TaskFragmentManager instance
     */
    public static void clear() {
        sInstance = null;
    }

    @Override
    public void kill() {
        if (isInstanceExist()) {
            clear();
        }
    }

    /**
     * check whether apply defined custom animation when change fragemnt
     *
     * @param isApplyCustomAnimation
     */
    public void applyCustomAnimation(boolean isApplyCustomAnimation) {
        mIsApplyCustomAnimation = isApplyCustomAnimation;
    }

    /**
     * set ui animation info about target view and listener
     *
     * @param targetView
     * @param uiAnimationListener
     */
    public void setUiAnimationInfo(View targetView, UiAnimationListener uiAnimationListener) {
        mUiTargetView = targetView;
        mUiAnimationListener = uiAnimationListener;
    }

    /**
     * run sub fragment
     *
     * @param containerId
     * @param fragment
     */
    public void runFragment(final int containerId, final Fragment fragment) {
        SimpleActionListener actionListener = new SimpleActionListener() {
            @Override
            public void doAction() {
                rawRunFragment(containerId, fragment);
            }
        };

        runAddExitUiAnimation(actionListener);
    }

    /**
     * run sub fragment actually
     *
     * @param containerId
     * @param fragment
     */
    private void rawRunFragment(int containerId, Fragment fragment) {
        if (fragment != null) {
            try {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                String tag = fragment.getClass().getSimpleName();

                if (mIsApplyCustomAnimation) {
                    fragmentTransaction.setCustomAnimations(R.anim.attach_fragment_launch_in, 0, 0, R.anim.attach_fragment_finish_out);
                }

                fragmentTransaction.add(containerId, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
                fragmentTransaction.commitAllowingStateLoss();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    /**
     * get current focused fragment which is located on top of backstacks
     *
     * @return
     */
    public Fragment getCurrentFocusedFragment() {
        Fragment resultFragment = null;
        int backStackEntryCount = mFragmentManager.getBackStackEntryCount();

        if (backStackEntryCount > 0) {
            FragmentManager.BackStackEntry backStackEntry = mFragmentManager.getBackStackEntryAt(backStackEntryCount - 1);

            if (backStackEntry != null) {
                resultFragment = mFragmentManager.findFragmentByTag(backStackEntry.getName());
            }
        }

        return resultFragment;
    }

    /**
     * pop backStack in FragmentManager
     */
    public boolean popFragment() {
        boolean popResult = false;

        Log.v("VV", "TFragmentManager / popFragment() / mFragmentManager.getBackStackEntryCount() = " + mFragmentManager.getBackStackEntryCount());

        if (mFragmentManager.getBackStackEntryCount() > 0) {
            popResult = mFragmentManager.popBackStackImmediate();
            runPopEnterUiAnimation();
        }

        return popResult;
    }

    /**
     * run exit ui animation when add fragment
     *
     * @param actionListener
     */
    private void runAddExitUiAnimation(final SimpleActionListener actionListener) {
        if (mUiTargetView != null) {
            TranslateAnimation translateAnimation = new TranslateAnimation(0, -mUiTargetView.getWidth(), 0, 0);
            translateAnimation.setDuration(ANIMATION_DURATION);
            translateAnimation.setAnimationListener(new CustomAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mUiAnimationListener != null) {
                        mUiAnimationListener.runAddExitUiListener();
                    }

                    actionListener.doAction();
                }

            });
            mUiTargetView.startAnimation(translateAnimation);
        } else {
            actionListener.doAction();
        }
    }

    /**
     * run pop enter ui animation when pop fragment from backstack
     */
    private void runPopEnterUiAnimation() {
        if (mUiTargetView != null) {
            if (mUiAnimationListener != null) {
                mUiAnimationListener.runPopEnterUiListener();
            }

            TranslateAnimation translateAnimation = new TranslateAnimation(-mUiTargetView.getWidth(), 0, 0, 0);
            translateAnimation.setDuration(ANIMATION_DURATION);
            mUiTargetView.startAnimation(translateAnimation);
        }
    }

    /**
     * ui animation listener which is run by add / pop stack tim
     */
    public interface UiAnimationListener {
        public void runAddExitUiListener();

        public void runPopEnterUiListener();
    }
}

