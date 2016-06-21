package com.empire.tigerlibrary.tool.motion;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

/**
 * long click detector which is able to distinguish normal click
 *
 * @author lordvader
 *
 */
public class LongClickDetector {
	private View mView;
	private LongPressGestureDetector mLongPressGestureDetector;
	private OnLongClickListener mLongClickListener;

	public LongClickDetector(Context context, View view) {
		mView = view;
		mLongPressGestureDetector = new LongPressGestureDetector(context, new CustomGestureDetector());
	}

	/**
	 * set long click listener
	 *
	 * @param longClickListener
	 */
	public void setLongClickListener(OnLongClickListener longClickListener) {
		mLongClickListener = longClickListener;
	}

	/**
	 * intercept touch event and manipulate
	 *
	 * @param ev
	 * @return
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLongPressGestureDetector != null) {
			return mLongPressGestureDetector.onTouchEvent(ev);
		}
		return false;
	}

	/**
	 * intercept and manipulate long press from touch event
	 *
	 * @author lordvader
	 *
	 */
	class LongPressGestureDetector extends GestureDetector {
		private CustomGestureDetector mGestureDetector;

		public LongPressGestureDetector(Context context, CustomGestureDetector gestureDetector) {
			super(context, gestureDetector);
			mGestureDetector = gestureDetector;
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (mGestureDetector.isLongPressed()) {
				mGestureDetector.releseLongPressFlag();
				return true;
			} else {
				return super.onTouchEvent(ev);
			}
		}
	}

	/**
	 * gesture detector which has distinguish from touch event
	 *
	 * @author lordvader
	 *
	 */
	class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
		public boolean mIsLongPressed;

		@Override
		public void onLongPress(MotionEvent e) {
			if (mLongClickListener != null) {
				mLongClickListener.onLongClick(mView);
			}

			mIsLongPressed = true;
		}

		/**
		 * check whether long press detected
		 *
		 * @return
		 */
		public boolean isLongPressed() {
			return mIsLongPressed;
		}

		/**
		 * release long press mode
		 */
		public void releseLongPressFlag() {
			mIsLongPressed = false;
		}
	}
}
