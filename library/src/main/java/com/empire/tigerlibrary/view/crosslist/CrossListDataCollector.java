package com.empire.tigerlibrary.view.crosslist;

import android.widget.ListAdapter;

/**
 * collect data of each item and apply such adapter to CrossListView
 *
 * @author lordvader
 *
 */
public abstract class CrossListDataCollector {
	/**
	 * collect data
	 *
	 * @param item
	 * @param dataCollectorListener
	 */
	public abstract void collectData(CrossListItem item, Listener dataCollectorListener);

	/**
	 * interface listener for updating CrossListView's adapter
	 *
	 * @author lordvader
	 *
	 */
	public static interface Listener {
		public void refreshAdapter(ListAdapter itemAdapter);
	}
}
