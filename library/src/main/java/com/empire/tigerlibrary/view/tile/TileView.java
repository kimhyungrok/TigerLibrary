package com.empire.tigerlibrary.view.tile;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.empire.tigerlibrary.animation.CustomAnimationListener;


/**
 * custom view extends ListView for metro tile effect.
 *
 *
 * @author lordvader
 *
 */
public class TileView extends ScrollView implements TileViewListener {
	/** auto scroll control */
	public static final int AUTO_SCROLL_FLAG_STOP = 0;
	public static final int AUTO_SCROLL_FLAG_TO_TOP = 1;
	public static final int AUTO_SCROLL_FLAG_TO_BOTTOM = 2;
	private final int AUTO_SCROLL_VELOCITY = 40;
	private int mGridSpacing = 0;

	private TileGridView mTileGridView;
	private TileGridView.TileGridViewListener mGridViewListener;

	private int mAutoScrollFlag = AUTO_SCROLL_FLAG_STOP;
	private boolean mEnableScrolling = true;
	private boolean mEnableEachTileLongClick = false;
	private boolean mIsTouchEventLock;

	/** for extra top margin */
	private boolean mIsShowExtraTopMargin;
	private LinearLayout mChildContainer;
	private View mExtraMarginView;
	private int mExtraTopMargin;

	public TileView(Context context) {
		super(context);
		initialize();
	}

	public TileView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mAutoScrollFlag > 0) {
			if (mGridViewListener != null) {
				mGridViewListener.doAction();
			}

			setScrollY(getScrollY() + ((mAutoScrollFlag == AUTO_SCROLL_FLAG_TO_BOTTOM) ? AUTO_SCROLL_VELOCITY : -AUTO_SCROLL_VELOCITY));
		}

		super.onDraw(canvas);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// test
		if (mIsTouchEventLock) {
			return true;
		}

		if (mEnableScrolling) {
			return super.onInterceptTouchEvent(ev);

		} else {
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// test
		if (mIsTouchEventLock) {
			return true;
		}

		if (mEnableScrolling) {
			return super.onTouchEvent(ev);
		} else {
			return false;
		}
	}

	/**
	 * initialize default setting
	 */
	protected void initialize() {
		Context context = getContext();
		mTileGridView = new TileGridView(context);
		mTileGridView.setTileViewListener(this);

		mChildContainer = new LinearLayout(context);
		mExtraMarginView = new View(context);
		reloadView();
		setVerticalScrollBarEnabled(false);
	}

	/**
	 * set TileGridAdapter adapter
	 *
	 * @param adapter
	 */
	public void setAdapter(TileGridAdapter adapter) {
		if (adapter != null) {
			adapter.sanityCheckTileType();
		}

		mTileGridView.setAdapter(adapter);
	}

	/**
	 * get TileGridAdapter adapter
	 *
	 * @return
	 */
	public TileGridAdapter getAdapter() {
		return mTileGridView.getAdapter();
	}

	/**
	 * reload view
	 */
	public void reloadView() {
		setScrollY(0);
		removeAllViews();
		mTileGridView.removeAllViews();

		if (mIsShowExtraTopMargin) {
			mChildContainer.removeAllViews();
			mChildContainer.setOrientation(LinearLayout.VERTICAL);
			mChildContainer.addView(mExtraMarginView, new LayoutParams(LayoutParams.MATCH_PARENT, mExtraTopMargin));
			mChildContainer.addView(mTileGridView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			super.addView(mChildContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		} else {
			super.addView(mTileGridView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		mTileGridView.addChildView();
	}

	/**
	 * set enable/disable each tile long click
	 *
	 * @param isEnableTileLongClick
	 */
	public void setEnableTileLongClick(boolean isEnableTileLongClick) {
		mEnableEachTileLongClick = isEnableTileLongClick;
	}

	/**
	 * set each tile surround space
	 * @param gridSpacing
	 */
	public void setTileSpacing(int gridSpacing) {
		mGridSpacing = gridSpacing;
	}

	/**
	 * add footer view in last
	 *
	 * @param view
	 */
	public void addFooterView(View view) {
		mTileGridView.addFooterView(view);
		mTileGridView.addChildView(true);
	}

	/**
	 * remove footer view
	 */
	public void removeFooterView() {
		mTileGridView.removeFooterView();
	}

	/**
	 * check whether footer view exist
	 *
	 * @return
	 */
	public boolean isExistFooterView() {
		return mTileGridView.isExistFooterView();
	}

	/**
	 * set whether showing extra top margin
	 *
	 * @param isShowExtraTopMargin
	 * @param topMargin
	 */
	public void setShowExtraTopMargin(boolean isShowExtraTopMargin, int topMargin) {
		mIsShowExtraTopMargin = isShowExtraTopMargin;
		mExtraTopMargin = topMargin;
		reloadView();
	}

	/**
	 * get item of specific index
	 *
	 * @param index
	 * @return
	 */
	public View getItemAt(int index) {
		View item = null;

		if (mTileGridView.getChildCount() > 0) {
			item = mTileGridView.getChildAt(index);
		}

		return item;
	}

	/**
	 * get item by tag
	 *
	 * @param tag
	 * @return
	 */
	public View getItemByTag(String tag) {
		View item = null;

		if (tag != null && !"".equals(tag)) {
			item = mTileGridView.getItemByTag(tag);
		}

		return item;
	}

	/**
	 * set whether measure and layout child unconditionally
	 *
	 * @param isMeasureUnconditionally
	 */
	public void setMeasureUnconditionally(boolean isMeasureUnconditionally) {
		mTileGridView.setMeasureUnconditionally(isMeasureUnconditionally);
	}

	/*******************************************************************************
	 * implements TileViewListener
	 *******************************************************************************/
	@Override
	public boolean isEnableTileLongClick() {
		return mEnableEachTileLongClick;
	}

	@Override
	public void addView(View child) {
		mTileGridView.addView(child);
	}

	@Override
	public boolean isEnableScrolling() {
		return mEnableScrolling;
	}

	@Override
	public void setEnableScrolling(boolean enableScrolling) {
		mEnableScrolling = enableScrolling;
	}

	@Override
	public void setAutoScrollY(int flag) {
		if (flag != AUTO_SCROLL_FLAG_STOP && isAutoScrolling()) {
			return;
		}

		mAutoScrollFlag = flag;

		if (mAutoScrollFlag == AUTO_SCROLL_FLAG_STOP) {
			mGridViewListener = null;
		}

		invalidate();
	}

	@Override
	public boolean isAutoScrolling() {
		return (mAutoScrollFlag != AUTO_SCROLL_FLAG_STOP) ? true : false;
	}

	@Override
	public void setGridViewListener(TileGridView.TileGridViewListener gridViewListener) {
		mGridViewListener = gridViewListener;
	}

	@Override
	public int getTileSpacing() {
		return mGridSpacing;
	}

	@Override
	public int getIndex(View view) {
		return mTileGridView.getIndex(view);
	}

	@Override
	public void addTileToFirst(View view, CustomAnimationListener animationListener) {
		mTileGridView.addTileToFirst(view, animationListener);
	}

	@Override
	public void addTileToLast(View view, CustomAnimationListener animationListener) {
		mTileGridView.addTileToLast(view, animationListener);
	}

	@Override
	public void replaceSameTile(View beforeView, View afterView) {
		mTileGridView.replaceSameTile(beforeView, afterView);
	}

	@Override
	public void addTileFirstAndLast(View firstView, View lastView, SparseArray<View> replaceViewMap) {
		mTileGridView.addTileFirstAndLast(firstView, lastView, replaceViewMap);
	}

	@Override
	public void removeTile(View view, CustomAnimationListener animationListener) {
		mTileGridView.removeTile(view, animationListener);
	}

	@Override
	public void removeAllTile() {
		mTileGridView.removeAllViews();
	}

	@Override
	public void moveToFirst(View view) {
		mTileGridView.moveToFirst(view);
	}

	@Override
	public int changeTileType(View view, int tileType, int toHeight, CustomAnimationListener animationListener) {
		return mTileGridView.changeTileType(view, tileType, toHeight, animationListener);
	}

	@Override
	public boolean isShowExtraTopMargin() {
		return mIsShowExtraTopMargin;
	}

	@Override
	public int getTileType(View view) {
		return mTileGridView.getAdapter().getTileType(view);
	}

	@Override
	public void setTouchEventLock(boolean isTouchEventLock) {
		mIsTouchEventLock = isTouchEventLock;
	}

	@Override
	public boolean isMotionActive() {
		return mTileGridView.isMotionActive();
	}
}
