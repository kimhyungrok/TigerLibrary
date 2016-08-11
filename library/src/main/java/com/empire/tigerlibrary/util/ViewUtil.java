package com.empire.tigerlibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.empire.tigerlibrary.tool.SimpleActionListener;
import com.empire.tigerlibrary.tool.motion.OnSingleClickListener;
import com.empire.tigerlibrary.view.SystemBarTintManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * useful class composed by methods for View manipulating
 *
 * @author lordvader
 */
public class ViewUtil {
    /**
     * pass onclick event on view
     *
     * @param view
     */
    public static void skipOnClick(View view) {
        view.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
            }
        });
    }

    /**
     * prevent behind invisible view's click event
     *
     * @param view
     */
    public static void preventBehindViewClicked(View view) {
        if (view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    /**
     * get device screen height
     *
     * @param activity
     * @return
     */
    public static int getDisplayHeight(Activity activity) {
        if (activity == null) {
            return -1;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        return height;
    }

    /**
     * get device screen height
     *
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context) {
        if (context == null) {
            return -1;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();

        return metrics.heightPixels;
    }

    /**
     * get device screen width
     *
     * @param activity
     * @return
     */
    public static int getDisplayWidth(Activity activity) {
        if (activity == null) {
            return -1;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        return width;
    }

    /**
     * get device screen width
     *
     * @param context
     * @return
     */
    public static int getDisplayWidth(Context context) {
        if (context == null) {
            return -1;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();

        return metrics.widthPixels;
    }

    /**
     * get device statusbar height
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if (activity == null) {
            return -1;
        }

        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * get device statusbar height
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * get view height
     *
     * @param view
     * @return
     */
    public static int getViewHeightByMeasure(View view) {
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    /**
     * get view's boundary which is compose by left, top, right, bottom
     * according by screen location
     *
     * @param view
     * @return
     */
    public static Rect getViewBoundary(View view) {
        int[] locationInScreen = new int[2];
        view.getLocationOnScreen(locationInScreen);

        int left = locationInScreen[0];
        int top = locationInScreen[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * get real view display area which is substracted from displayheight about status bar height
     *
     * @param activity
     * @return
     */
    public static int getViewDisplayAreaHeight(Activity activity) {
        return getDisplayHeight(activity) - getStatusBarHeight(activity);
    }

    /**
     * get view height include top and bottom margin if its view's parent is
     * RelativeLayout
     *
     * @param view
     * @return
     */
    public static int getViewHeightIncludeMargin(View view) {
        if (view == null) {
            return 0;
        }

        int height = 0;
        LayoutParams viewLayoutParam = view.getLayoutParams();

        if (viewLayoutParam != null) {
            height += viewLayoutParam.height;

            if (viewLayoutParam instanceof RelativeLayout.LayoutParams) {
                height += ((RelativeLayout.LayoutParams) viewLayoutParam).topMargin;
                height += ((RelativeLayout.LayoutParams) viewLayoutParam).bottomMargin;
            }
        }

        return height;
    }

    /**
     * adjust view height
     *
     * @param view
     * @param height
     */
    public static void setViewHeight(View view, int height) {
        LayoutParams layoutParam = (LayoutParams) view.getLayoutParams();
        layoutParam.height = height;
        view.setLayoutParams(layoutParam);
    }

    /**
     * adjust view width
     *
     * @param view
     * @param width
     */
    public static void setViewWidth(View view, int width) {
        LayoutParams layoutParam = (LayoutParams) view.getLayoutParams();
        layoutParam.width = width;
        view.setLayoutParams(layoutParam);
    }

    /**
     * measure view and set layout by result measured width and height
     *
     * @param view
     */
    public static void setLayoutByMeasureExactly(Activity activity, View view, int height) {
        // measure footer view
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }

        view.measure(MeasureSpec.makeMeasureSpec(ViewUtil.getDisplayWidth(activity), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.AT_MOST));

        LayoutParams footerViewLayoutParam = view.getLayoutParams();
        footerViewLayoutParam.width = view.getMeasuredWidth();
        footerViewLayoutParam.height = view.getMeasuredHeight();
        view.setLayoutParams(footerViewLayoutParam);
    }

    /**
     * get view's top on screen
     *
     * @param view
     * @return
     */
    public static int getTopOnScreen(View view) {
        int[] locationInScreen = new int[2];
        view.getLocationOnScreen(locationInScreen);
        return locationInScreen[1];
    }

    /**
     * get view's bottom on screen
     *
     * @param view
     * @return
     */
    public static int getBottomOnScreen(View view) {
        return getTopOnScreen(view) + view.getHeight();
    }

    /**
     * clear all view's focus include child
     *
     * @param parentView
     */
    public static void clearAllFocusIncludeChild(ViewGroup parentView) {
        if (parentView != null) {
            parentView.setFocusableInTouchMode(false);
            parentView.clearFocus();

            int childCount = parentView.getChildCount();

            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = parentView.getChildAt(i);
                    child.setFocusableInTouchMode(false);
                    child.clearFocus();

                    if (child instanceof ViewGroup) {
                        clearAllFocusIncludeChild((ViewGroup) child);
                    }
                }
            }
        }
    }

    /**
     * remove focus of all views which are related with view hierarchy
     *
     * @param view
     */
    public static void removeFocus(View view) {
        if (view == null) {
            return;
        }

        if (view instanceof ViewGroup) {
            ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            int childViewCount = ((ViewGroup) view).getChildCount();

            for (int i = 0; i < childViewCount; i++) {
                View childView = ((ViewGroup) view).getChildAt(i);
                removeFocus(childView);
            }
        }

        view.setFocusable(false);
        view.setClickable(false);
    }

    /**
     * check whether this device has softkey
     *
     * @return
     */
    public static boolean hasSoftKey(Activity activity) {
        return (getSoftKeyHeight(activity) > 0) ? true : false;
    }

    /**
     * get soft key height if exist
     *
     * @param activity
     * @return
     */
    public static int getSoftKeyHeight(Activity activity) {
        int softKeyHeight = 0;

        try {
            Display display = activity.getWindowManager().getDefaultDisplay();
            int realHeight;
            if (Build.VERSION.SDK_INT >= 17) {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                realHeight = realSize.y;
            } else {
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                int nW = (Integer) mGetRawW.invoke(display);
                int nH = (Integer) mGetRawH.invoke(display);
                realHeight = nH;
            }
            softKeyHeight = realHeight - getDisplayHeight(activity);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (softKeyHeight < 0) {
            softKeyHeight = 0;
        }

        return softKeyHeight;
    }

    /**
     * set status bar color (It is activated over kitkat os
     *
     * @param activity
     * @param colorValue
     */
    public static void setStatusBarColor(Activity activity, int colorValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(colorValue);
        }
    }

    /**
     * set status bar color (It is activated over kitkat os
     *
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColorResource(Activity activity, int colorId) {
        try {
            setStatusBarColor(activity, activity.getResources().getColor(colorId));
        } catch (Resources.NotFoundException e) {
            e.getMessage();
        }
    }

    /**
     * set / remove strike line in TextView
     *
     * @param textView
     * @param isShow
     */
    public static void setStrikeLine(TextView textView, boolean isShow) {
        int paintFlags = textView.getPaintFlags();
        boolean isStrikeFlagSet = (paintFlags & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG;

        if (isShow) {
            paintFlags = paintFlags | Paint.STRIKE_THRU_TEXT_FLAG;
        } else {
            if (isStrikeFlagSet) {
                paintFlags = paintFlags ^ Paint.STRIKE_THRU_TEXT_FLAG;
            }
        }

        textView.setPaintFlags(paintFlags);
    }

    /**
     * get action bar height
     *
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarSize;
    }

    /**
     * set fullscreen mode after hiding title bar and status bar.
     * It have to call when create View Contents onCreate() in Activity
     *
     * @param activity
     */
    public static void setFullScreenMode(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * switch between full screen and normal screen.
     * It is able to call freely in any case.
     *
     * @param activity
     * @param fullscreen
     */
    public static void setFullscreen(Activity activity, boolean fullscreen) {
        setFullscreen(activity, fullscreen, null);
    }

    /**
     * switch between full screen and normal screen.
     * It is able to call freely in any case, and also handling listener
     *
     * @param activity
     * @param fullscreen
     * @param listener
     */
    public static void setFullscreen(Activity activity, boolean fullscreen, SimpleActionListener listener) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();

        if (fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }

        activity.getWindow().setAttributes(attrs);

        // run listener
        if (listener != null) {
            listener.doAction();
        }
    }

    /**
     * check whether current screen mode is full screen or not
     *
     * @param activity
     * @return
     */
    public static boolean isFullScreen(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        return ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}