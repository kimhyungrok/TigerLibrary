package com.empire.tigerlibrary.view.tile;

import android.util.SparseArray;
import android.view.View;

import com.empire.tigerlibrary.animation.CustomAnimationListener;


/**
 * interface for communicating between TileView (container) and TileGridView
 *
 * @author lordvader
 *
 */
public interface TileViewListener {
	/**
	 * check whether enable scrolling
	 *
	 * @return
	 */
	public boolean isEnableScrolling();

	/**
	 * set enable scrolling
	 *
	 * @param enableScrolling
	 */
	public void setEnableScrolling(boolean enableScrolling);

	/**
	 * get scroll Y value
	 *
	 * @return
	 */
	public int getScrollY();

	/**
	 * set scroll to Y
	 *
	 * @param value
	 */
	public void setScrollY(int value);

	/**
	 * set auto scroll status
	 *
	 * @param flag
	 */
	public void setAutoScrollY(int flag);

	/**
	 * set TileGridViewListener for controlling TileGridView
	 *
	 * @param gridViewListener
	 */
	public void setGridViewListener(TileGridView.TileGridViewListener gridViewListener);

	/**
	 * check whether auto scroll is active
	 *
	 * @return
	 */
	public boolean isAutoScrolling();

	/**
	 * check whether each tile get long click event
	 *
	 * @return
	 */
	public boolean isEnableTileLongClick();

	/**
	 * reload whole view
	 */
	public void reloadView();

	/**
	 * get padding left value
	 *
	 * @return
	 */
	public int getPaddingLeft();

	/**
	 * get padding bottom value
	 *
	 * @return
	 */
	public int getPaddingBottom();

	/**
	 * get padding right value
	 *
	 * @return
	 */
	public int getPaddingRight();

	/**
	 * get padding top value
	 *
	 * @return
	 */
	public int getPaddingTop();

	/**
	 * get each tile spacing
	 *
	 * @return
	 */
	public int getTileSpacing();

	/**
	 * get index of such view
	 *
	 * @param view
	 * @return
	 */
	public int getIndex(View view);

	/**
	 * add such tile to first row
	 *
	 * @param view
	 * @param animationListener
	 */
	public void addTileToFirst(View view, CustomAnimationListener animationListener);

	/**
	 * add such tile to last row
	 *
	 * @param view
	 * @param animationListener
	 */
	public void addTileToLast(View view, CustomAnimationListener animationListener);

	/**
	 * add two tiles to first and last row each
	 *
	 * @param firstView
	 * @param lastView
	 * @param finalListener
	 */
	public void addTileFirstAndLast(View firstView, View lastView, SparseArray<View> replaceViewMap);

	/**
	 * replace tile
	 *
	 * @param beforeView
	 * @param afterView
	 */
	public void replaceSameTile(View beforeView, View afterView);

	/**
	 * remove such tile
	 *
	 * @param view
	 */
	public void removeTile(View view, CustomAnimationListener animationListener);

	/**
	 * remove all tile
	 */
	public void removeAllTile();

	/**
	 * move tile to first
	 *
	 * @param view
	 */
	public void moveToFirst(View view);

	/**
	 * change tile type and size
	 *
	 * @param view
	 * @param tileType
	 * @param toHeight
	 * @return
	 */
	public int changeTileType(View view, int tileType, int toHeight, CustomAnimationListener animationListener);

	/**
	 * get current tile type of view
	 *
	 * @param view
	 * @return
	 */
	public int getTileType(View view);

	/**
	 * check whether showing extra top margin
	 *
	 * @return
	 */
	public boolean isShowExtraTopMargin();

	/**
	 * set whether lock or release touch event
	 *
	 * @param isTouchEventLock
	 */
	public void setTouchEventLock(boolean isTouchEventLock);

	/**
	 * check whether motion effect is activating
	 *
	 * @return
	 */
	public boolean isMotionActive();
}
