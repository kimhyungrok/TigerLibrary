package com.empire.tigerlibrary.tool.motion;

import android.view.MotionEvent;
import android.view.View;

/**
 * for handling caller's callback when doing defined motion
 *
 * @author lordvader
 *
 */
public abstract class CommonMotionListener {
	public void swipeUp(View view) {
	}

	public void swipeDown(View view) {
	}

	public void swipeToLeftTop(View view) {
	}

	public void swipeToRightTop(View view) {
	}

	public void swipeToLeft(View view) {
	}

	public void swipeToRight(View view) {
	}

	public void swipeToLeft(View view, MotionEvent event) {
	}

	public void swipeToRight(View view, MotionEvent event) {
	}

}
