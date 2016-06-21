package com.empire.tigerlibrary.animation.effect;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * flip animation
 *
 * @author lordvader
 *
 */
public class Flip3D extends Animation {
	private float mFromDegrees;
	private float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private Camera mCamera;
	private float mPivotDegrees;
	private Flip3DListener mListener = null;
	private boolean m_bIsApplyChangeView = false;

	public static interface Flip3DListener {
		public void changeView();
	}

	public Flip3D(float fromDegrees, float toDegrees, float centerX, float centerY, Flip3DListener listener) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mListener = listener;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

		// change view
		if (degrees > mPivotDegrees && !m_bIsApplyChangeView && mListener != null) {
			m_bIsApplyChangeView = true;
			mListener.changeView();
			mFromDegrees += 180;
			mToDegrees += 180;
		}

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}

	/**
	 * set timing of changing image
	 *
	 * @param pivotDegress
	 */
	public void setPivotDegrees(float pivotDegress) {
		mPivotDegrees = pivotDegress;
	}
}
