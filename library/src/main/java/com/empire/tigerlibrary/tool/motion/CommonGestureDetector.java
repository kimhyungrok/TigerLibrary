package com.empire.tigerlibrary.tool.motion;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * gesture detector for handling touch event on trigger
 *
 * @author lordvader
 *
 */
public class CommonGestureDetector implements OnGestureListener {
	private final int MIN_DIAGONAL_RANGE_X = 100;
	private GestureDetector gestureDetector;
	private CommonMotionListener motionListener;
	private View view;
	private boolean mIsReinforceDetectCorner;

	public CommonGestureDetector(Context context) {
		gestureDetector = new GestureDetector(context, this);
	}

	/**
	 * set MotionListener for handling callback
	 *
	 * @param motionListener
	 */
	public void setMotionListener(CommonMotionListener motionListener) {
		this.motionListener = motionListener;
	}

	/**
	 * set whether reinforce detecting corner gesture
	 *
	 * @param isReinforceDetectCorner
	 */
	public void setReinforceDetectCorner(boolean isReinforceDetectCorner) {
		mIsReinforceDetectCorner = isReinforceDetectCorner;
	}

	/**
	 * handle touch event for detecting defined motion and doing callback
	 *
	 * @param event
	 * @return
	 */
	public boolean onHandleTouchEvent(View view, MotionEvent event) {
		this.view = view;
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		final int minDiagonalRangeX = mIsReinforceDetectCorner ? MIN_DIAGONAL_RANGE_X / 3 : MIN_DIAGONAL_RANGE_X;

		if (!mIsReinforceDetectCorner) {
			// detect left and right
			if (Math.abs(velocityX) > Math.abs(velocityY)) {
				if (velocityX > 0) {
					if (motionListener != null) {
						motionListener.swipeToLeft(view, e2);
					}

					return true;
				} else {
					if (motionListener != null) {
						motionListener.swipeToRight(view, e2);
					}

					return true;
				}
			}
		}

		float distanceX = Math.abs(e1.getX() - e2.getX());

		if (distanceX > minDiagonalRangeX && (e1.getX() > e2.getX()) && (e1.getY() > e2.getY())) {
			if (motionListener != null) {
				motionListener.swipeToLeftTop(view);
			}
		} else if (distanceX > minDiagonalRangeX && (e1.getX() < e2.getX()) && (e1.getY() > e2.getY())) {
			if (motionListener != null) {
				motionListener.swipeToRightTop(view);
			}
		} else if (Math.abs(velocityX) < Math.abs(velocityY)) {
			if (velocityY > 0) {
				if (motionListener != null) {
					motionListener.swipeDown(view);
				}
			} else {
				if (motionListener != null) {
					motionListener.swipeUp(view);
				}
			}
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

}
