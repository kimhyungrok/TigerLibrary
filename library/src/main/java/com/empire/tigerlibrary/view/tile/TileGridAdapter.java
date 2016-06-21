package com.empire.tigerlibrary.view.tile;

import java.util.ArrayList;

import android.view.View;

/**
 * adapter for used by TileGridView
 *
 * @author lordvader
 *
 */
public class TileGridAdapter {
	protected ArrayList<View> mItemList = new ArrayList<View>();
	protected ArrayList<Integer> mTileTypeList = new ArrayList<Integer>();

	public TileGridAdapter() {

	}

	/**
	 * add child with tile type
	 *
	 * @param itemView
	 */
	public void add(View itemView, int tileType) {
		mItemList.add(itemView);
		mTileTypeList.add(tileType);
	}

	/**
	 * get such index view
	 *
	 * @param index
	 * @return
	 */
	public View getView(int index) {
		return mItemList.get(index);
	}

	/**
	 * get child count
	 *
	 * @return
	 */
	public int getCount() {
		return mItemList.size();
	}

	/**
	 * set child to such index with tileType
	 *
	 * @param index
	 * @param itemView
	 * @return
	 */

	public View setItem(int index, View itemView, int tileType) {
		mTileTypeList.set(index, tileType);
		return mItemList.set(index, itemView);
	}

	/**
	 * get index of such child view
	 *
	 * @param itemView
	 * @return
	 */
	public int getItemIndex(View itemView) {
		return mItemList.indexOf(itemView);
	}

	/**
	 * get tile type of such child view
	 *
	 * @param itemView
	 * @return
	 */
	public int getTileType(View itemView) {
		return mTileTypeList.get(mItemList.indexOf(itemView));
	}

	/**
	 * set tile type of such child view
	 *
	 * @param itemView
	 * @param tileType
	 */
	public void setTileType(View itemView, int tileType) {
		mTileTypeList.set(mItemList.indexOf(itemView), tileType);
	}

	/**
	 * remove such child view
	 *
	 * @param index
	 */
	public void removeItem(int index) {
		mItemList.remove(index);
		mTileTypeList.remove(index);
	}

	/**
	 * remove all child view
	 */
	public void removeAll() {
		mItemList.clear();
		mTileTypeList.clear();
	}

	/**
	 * sanity check tile and adjust correctly
	 */
	public void sanityCheckTileType() {
		int childCount = getCount();
		int previousTileType = -1;
		View previousChildView = null;
		View childView = null;
		int tileType = -1;

		for (int i = 0; i < childCount; i++) {
			childView = getView(i);
			tileType = getTileType(childView);

			if (previousTileType == TileGridView.POS_HALF_LEFT_WITH && tileType != TileGridView.POS_HALF_RIGHT_WITH) {
				if (previousChildView != null) {
					setTileType(previousChildView, TileGridView.POS_HALF_LEFT_ONLY);
				}
			} else if (previousTileType != TileGridView.POS_HALF_LEFT_WITH && tileType == TileGridView.POS_HALF_RIGHT_WITH) {
				setTileType(childView, TileGridView.POS_HALF_RIGHT_ONLY);
			} else if (i == (childCount - 1) && tileType == TileGridView.POS_HALF_LEFT_WITH) {
				setTileType(childView, TileGridView.POS_HALF_LEFT_ONLY);
			}

			previousTileType = tileType;
			previousChildView = childView;
		}
	}
}
