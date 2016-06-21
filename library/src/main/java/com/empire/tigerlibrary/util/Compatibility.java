package com.empire.tigerlibrary.util;

import android.os.Build;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.lang.reflect.Method;

/**
 * composite of useful methods for supporting lower OS version
 * Created by lordvader on 2016. 3. 16..
 */
public class Compatibility {
    private static Method sListViewTrackMotionScroll;
    private static Method sIsAttachedToWindow;

    static {
        Method trackMotionScroll = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                trackMotionScroll = AbsListView.class.getDeclaredMethod("trackMotionScroll", int.class, int.class);
                trackMotionScroll.setAccessible(true);
            } catch (NoSuchMethodException e) {

            }
        }
        sListViewTrackMotionScroll = trackMotionScroll;
    }

    /**
     * support scrollListBy()
     *
     * @param listView
     * @param y
     */
    public static void scrollListBy(ListView listView, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listView.scrollListBy(y);
        } else {
            try {
                if (sListViewTrackMotionScroll != null) {
                    sListViewTrackMotionScroll.invoke(listView, -y, -y);
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * support isAttachedToWindow()
     * @param view
     * @return
     */
    public static boolean isAttachedToWindow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return view.isAttachedToWindow();
        } else {
            boolean result = false;
            try {
                if (sIsAttachedToWindow != null) {
                    result = (boolean)sIsAttachedToWindow.invoke(view);

                }
            } catch (Exception e) {

            }
            return result;
        }
    }
}
