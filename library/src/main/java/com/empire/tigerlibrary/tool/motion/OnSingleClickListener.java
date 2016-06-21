package com.empire.tigerlibrary.tool.motion;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * custom onClickListener for preventing handling over twice click event when
 * click view very fast
 *
 * @author lordvader
 *
 */
public abstract class OnSingleClickListener implements OnClickListener {
	private static final long MIN_CLICK_INTERVAL = 1000;
	private long mLastClickTime;

	/**
	 * onClick event callback which is filtered fast click
	 *
	 * @param view
	 */
	public abstract void onSingleClick(View view);

	@Override
	public final void onClick(View view) {
		long currentClickTime = SystemClock.uptimeMillis();
		long elapsedTime = currentClickTime - mLastClickTime;
		mLastClickTime = currentClickTime;

		if (elapsedTime <= MIN_CLICK_INTERVAL) {
			return;
		}

		onSingleClick(view);
	}
}
