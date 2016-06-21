package com.empire.tigerlibrary.tool.motion;

import android.content.Context;
import android.view.MotionEvent;

/**
 * detect and handle onClick event during handling onTouchEvent callback
 *
 * @author lordvader
 *
 */
public class OnClickDetector {
	private static final int MAX_CLICK_DURATION = 70;
	private static final int MAX_CLICK_DISTANCE = 15;

	private Context mContext;
	private long mPressStartTime;
	private float mPressedX;
	private float mPressedY;
	private boolean mStayedWithinClickDistance;

	public OnClickDetector(Context context) {
		mContext = context;
	}

	/**
	 * handle touch action down event (it is just used for calculation)
	 *
	 * @param event
	 */
	public void handleTouchActionDown(MotionEvent event) {
		mPressStartTime = System.currentTimeMillis();
		mPressedX = event.getX();
		mPressedY = event.getY();
		mStayedWithinClickDistance = true;
	}

	/**
	 * handle touch action move event (it is just used for calculation)
	 *
	 * @param event
	 */
	public void handleTouchActionMove(MotionEvent event) {
		if (mStayedWithinClickDistance && distance(mPressedX, mPressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
			mStayedWithinClickDistance = false;
		}
	}

	/**
	 * check whether click timing is over from defined max duration
	 *
	 * @return
	 */
	public boolean isCheckingOver() {
		long pressDuration = System.currentTimeMillis() - mPressStartTime;
		return pressDuration > MAX_CLICK_DURATION;
	}

	/**
	 * check whether onClick event is detected
	 *
	 * @return
	 */
	public boolean isOnClickDetected() {
		long pressDuration = System.currentTimeMillis() - mPressStartTime;
		return pressDuration < MAX_CLICK_DURATION && mStayedWithinClickDistance;
	}

	/**
	 * get distance between two point
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private float distance(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
		return pxToDp(distanceInPx);
	}

	/**
	 * convert pixel to dimension value
	 *
	 * @param px
	 * @return
	 */
	private float pxToDp(float px) {
		return px / mContext.getResources().getDisplayMetrics().density;
	}
}
