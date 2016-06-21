package com.empire.tigerlibrary.view.crosslist;

import android.widget.ListAdapter;

/**
 * handling CrossListView's item object include title and contents list
 *
 * @author lordvader
 *
 */
public class CrossListItem {
	private int mIndex;
	private String mTitle;
	private ListAdapter mAdapter;
	private CrossListDataCollector mDataCollector;

	/**
	 * set index
	 *
	 * @param index
	 */
	public void setIndex(int index) {
		mIndex = index;
	}

	/**
	 * get index
	 *
	 * @return
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * set title
	 *
	 * @param title
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * get title
	 *
	 * @return
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * set list adapter
	 *
	 * @param adapter
	 */
	public void setListAdapter(ListAdapter adapter) {
		mAdapter = adapter;
	}

	/**
	 * get list adapter
	 *
	 * @return
	 */
	public ListAdapter getListAdapter() {
		return mAdapter;
	}

	/**
	 * remove list adapter
	 */
	public void removeListAdapter() {
		mAdapter = null;
	}

	/**
	 * set DataCollector
	 *
	 * @param dataCollector
	 */
	public void setDataCollector(CrossListDataCollector dataCollector) {
		mDataCollector = dataCollector;
	}

	/**
	 * run DataCollector
	 */
	public void runDataCollector(CrossListDataCollector.Listener dataCollectorListener) {
		if (mDataCollector != null) {
			mDataCollector.collectData(this, dataCollectorListener);
		}
	}
}
