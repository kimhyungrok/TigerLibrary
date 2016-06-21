package com.empire.tigerlibrary.tool.motion;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/**
 * class for intercepting back press event
 *
 * @author lordvader
 *
 */
public class BackPressStealer {
	public static void steal(final View view, final BackPressStealerListener listener) {
		if (view == null) {
			return;
		}

		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean isConsume = false;

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (listener != null) {
						listener.doAction();
						isConsume = true;
					}
				}

				// release key event
				view.setFocusableInTouchMode(false);
				view.clearFocus();

				return isConsume;
			}
		});
	}

	public static void stealContinue(final View view, final BackPressStealerListener listener) {
		if (view == null) {
			return;
		}

		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean isConsume = false;

				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (listener != null) {
						listener.doAction();
						isConsume = true;
					}
				}

				return isConsume;
			}
		});
	}

	/**
	 * interface listener for BackPressStealer
	 *
	 * @author lordvader
	 *
	 */
	public interface BackPressStealerListener {
		public void doAction();
	};
}
