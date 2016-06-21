package com.empire.tigerlibrary.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.tool.SimpleActionListener;


/**
 * handle screen cover using by CustomProgressDialog when change home or is
 * launched by Launcher
 *
 * @author lordvader
 */
public class ScreenCover {
    private final int CLOSE_COVER_DELAY = 60;
    private CustomProgressDialog mCustomProgressDlg;
    private Activity mActivity;

    public ScreenCover(Activity activity) {
        mActivity = activity;
    }

    /**
     * show screen cover
     */
    public void showCover() {
        showCover(null);
    }

    /**
     * show screen cover (handling BaseFragmentListener)
     *
     * @param actionListener
     */
    public void showCover(final SimpleActionListener actionListener) {
        new Thread() {
            public void run() {
                try {
                    Looper.prepare();
                    showProgressDialog();
                    Looper.loop();
                } catch (Exception e) {
                }
            }
        }.start();

        if (actionListener != null) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    actionListener.doAction();
                }
            }, CLOSE_COVER_DELAY);
        }
    }

    /**
     * close screen cover
     */
    public void closeCover() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dismissProgress();
            }
        }, CLOSE_COVER_DELAY);
    }

    /**
     * close screen cover immediately
     */
    public void closeCoverImmediately() {
        dismissProgress();
    }

    /**
     * show progress dialog
     */
    private void showProgressDialog() {
        mCustomProgressDlg = new CustomProgressDialog(mActivity, R.drawable.custom_progress_dialog);
        mCustomProgressDlg.show();
    }

    /**
     * dismiss progress diabeteslog when is showing
     */
    private void dismissProgress() {
        if (mCustomProgressDlg != null && mCustomProgressDlg.isShowing()) {
            mCustomProgressDlg.dismiss();
        }
    }
}
