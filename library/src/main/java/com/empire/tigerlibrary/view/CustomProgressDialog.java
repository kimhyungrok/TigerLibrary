package com.empire.tigerlibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.util.Utils;


/**
 * custom ProgressDialog which is manipulated default style
 *
 * @author lordvader
 *
 */
public class CustomProgressDialog extends Dialog {
	private final int DEFAULT_ICON_SIZE_RES_ID = R.dimen.dp50;
	private final int ICON_ROTATE_DURATION = 1000;
	private ImageView mIconView;

	public CustomProgressDialog(Context context, int theme) {
		super(context, R.style.CustomProgressDialog);

		WindowManager.LayoutParams wmLayoutParams = getWindow().getAttributes();
		wmLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		getWindow().setAttributes(wmLayoutParams);

		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);

		LinearLayout container = new LinearLayout(context);
		container.setOrientation(LinearLayout.VERTICAL);
		int size = Utils.getPxFromDp(context, DEFAULT_ICON_SIZE_RES_ID);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);

		mIconView = new ImageView(context);
		mIconView.setImageResource(theme);
		container.addView(mIconView, layoutParams);
		addContentView(container, layoutParams);
	}

	@Override
	public void show() {
		super.show();

		RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setDuration(ICON_ROTATE_DURATION);

		mIconView.setAnimation(rotateAnimation);
		mIconView.startAnimation(rotateAnimation);
	}
}
