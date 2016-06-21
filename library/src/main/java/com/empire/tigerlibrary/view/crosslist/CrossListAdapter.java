package com.empire.tigerlibrary.view.crosslist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.ListAdapter;

/**
 * adapter for adding item and retrieving useful data of CrossListView
 *
 * @author lordvader
 *
 */
public class CrossListAdapter {
	private List<CrossListItem> mItemList = Collections.synchronizedList(new ArrayList<CrossListItem>());
	private ArrayList<String> mTitleList;

	public CrossListAdapter(ArrayList<CrossListItem> itemList) {
		mItemList.addAll(itemList);
	}

	/**
	 * add item
	 *
	 * @param item
	 */
	public void add(CrossListItem item) {
		mItemList.add(item);
	}

	/**
	 * remove item
	 *
	 * @param index
	 */
	public void remove(int index) {
		if (index > -1 && index < mItemList.size()) {
			mItemList.remove(index);
		}
	}

	/**
	 * get item
	 *
	 * @param index
	 * @return
	 */
	public CrossListItem getItem(int index) {
		if (index > -1 && index < mItemList.size()) {
			return mItemList.get(index);
		}

		return null;
	}

	/**
	 * get item count
	 *
	 * @return
	 */
	public int getCount() {
		return mItemList.size();
	}

	/**
	 * get item's title
	 *
	 * @param index
	 * @return
	 */
	public String getTitle(int index) {
		if (index > -1 && index < mItemList.size()) {
			CrossListItem item = mItemList.get(index);

			if (item != null) {
				return item.getTitle();
			}
		}

		return null;
	}

	/**
	 * get item's ListAdapter
	 *
	 * @param index
	 * @return
	 */
	public ListAdapter getListAdapter(int index) {
		if (index > -1 && index < mItemList.size()) {
			CrossListItem item = mItemList.get(index);

			if (item != null) {
				return item.getListAdapter();
			}
		}

		return null;
	}

	/**
	 * get title list
	 *
	 * @return
	 */
	public List<String> getTitleList() {
		if (mTitleList != null && mTitleList.size() == mItemList.size()) {
			return mTitleList;
		}

		mTitleList = new ArrayList<String>();

		if (mItemList.size() > 0) {
			for (CrossListItem item : mItemList) {
				mTitleList.add(item.getTitle());
			}
		}

		return mTitleList;
	}
}
