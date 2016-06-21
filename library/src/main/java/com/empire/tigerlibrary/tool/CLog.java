package com.empire.tigerlibrary.tool;

import android.util.Log;

/**
 * Created by lordvader on 2015. 7. 29..
 */
public class CLog {
    private static final int VERBOSE_LOG = 0x0001;
    private static final int INFO_LOG = 0x0002;
    private static final int WARN_LOG = 0x0004;
    private static final int ERROR_LOG = 0x0008;
    private static final int DEBUG_LOG = 0x0010;
    private static final int HIDE_MASK = 0;

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

}
