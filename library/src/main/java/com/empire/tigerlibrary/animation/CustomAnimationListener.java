package com.empire.tigerlibrary.animation;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 *
 * abstract CustomAnimationListener which override AnimationListener because
 * prohibit inherit unnecessary method
 *
 * @author lordvader
 *
 */
public abstract class CustomAnimationListener implements AnimationListener {

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public abstract void onAnimationEnd(Animation animation);

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	public void doAction() {
	}
}