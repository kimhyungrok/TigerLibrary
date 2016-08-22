package com.empire.tigerlibrary.tool;

import android.util.Log;

/**
 * Created by lordvader on 2015. 7. 29..
 */
public class CLog {
    private static final boolean IS_SHOW = true;
    private static final String TAG = "VV";

    private static final int VERBOSE_LOG = 0x0001;
    private static final int INFO_LOG = 0x0002;
    private static final int WARN_LOG = 0x0004;
    private static final int ERROR_LOG = 0x0008;
    private static final int DEBUG_LOG = 0x0010;
    private static final int HIDE_MASK = IS_SHOW ? 0 : 0x0017;

    /**
     * show VERBOSE log
     *
     * @param tag
     * @param message
     */
    public static void v(String tag, String message) {
        if ((HIDE_MASK & VERBOSE_LOG) == VERBOSE_LOG) {
            return;
        }

        Log.v(tag, message);
    }

    /**
     * show VERBOSE log (apply default TAG)
     *
     * @param message
     */
    public static void v(String message) {
        v(TAG, message);
    }

    /**
     * show INFO log
     *
     * @param tag
     * @param message
     */
    public static void i(String tag, String message) {
        if ((HIDE_MASK & INFO_LOG) == INFO_LOG) {
            return;
        }

        Log.i(tag, message);
    }

    /**
     * show INFO log (apply default TAG)
     *
     * @param message
     */
    public static void i(String message) {
        i(TAG, message);
    }

    /**
     * show WARN log
     *
     * @param tag
     * @param message
     */
    public static void w(String tag, String message) {
        if ((HIDE_MASK & WARN_LOG) == WARN_LOG) {
            return;
        }

        Log.w(tag, message);
    }

    /**
     * show WARN log (apply default TAG)
     *
     * @param message
     */
    public static void w(String message) {
        w(TAG, message);
    }

    /**
     * show ERROR log
     *
     * @param tag
     * @param message
     */
    public static void e(String tag, String message) {
        if ((HIDE_MASK & ERROR_LOG) == ERROR_LOG) {
            return;
        }

        Log.e(tag, message);
    }

    /**
     * show ERROR log (apply default TAG)
     *
     * @param message
     */
    public static void e(String message) {
        e(TAG, message);
    }

    /**
     * show DEBUG log
     *
     * @param tag
     * @param message
     */
    public static void d(String tag, String message) {
        if ((HIDE_MASK & DEBUG_LOG) == DEBUG_LOG) {
            return;
        }

        Log.d(tag, message);
    }

    /**
     * show DEBUG log (apply default TAG)
     *
     * @param message
     */
    public static void d(String message) {
        d(TAG, message);
    }
}
