package com.empire.tigerlibrary.view.crosslist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * tab container which is possible to scroll horizontally
 *
 * @author lordvader
 *
 */
public class HorizontalScrollTab extends HorizontalScrollView {
	private final int DEFAULT_TEXT_COLOR_ID = R.color.color_white;
	private final int DEFAULT_TEXT_SIZE = 19; // dip unit
	private final int DEFAULT_UNSELECTED_TEXT_COLOR_ID = R.color.color_white_alpha40;

	private final int DEFAULT_BAR_HEIGHT = R.dimen.dp2;
	private final int DEFAULT_BAR_BOTTOM_MARGIN = R.dimen.dp6;
	private final int DEFAULT_BAR_COLOR_ID = R.color.color_white;
	private final int DEFAULT_ITEM_SPACING = R.dimen.dp5;

	private Context mContext;
	private LinearLayout mContainer;
	private int mItemTextColorId;
	private int mItemTextSize;
	private int mItemSpace;
	private int mItemBarColorId;
	private int mItemBarHeight;
	private int mItemBarBottomMargin;
	private int mPreviousSelectedItemIndex;
	private boolean mIsFreezeTouchEvent;
	private ArrayList<String> mItemList = new ArrayList<String>();
	private CrossListView.Callback mCallback;

	public HorizontalScrollTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initialize(context, null);
	}

	public HorizontalScrollTab(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, null);
	}

	public HorizontalScrollTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, null);
	}

	public HorizontalScrollTab(Context context, Param param) {
		super(context);
		initialize(context, param);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mIsFreezeTouchEvent) {
			return true;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mIsFreezeTouchEvent) {
			return true;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * initialize this view by default setting
	 */
	private void initialize(Context context, Param param) {
		mContext = context;
		this.setHorizontalScrollBarEnabled(false);

		// set default value;
		if (param != null) {
			mItemTextColorId = (param.itemTextColorId == 0) ? DEFAULT_TEXT_COLOR_ID : param.itemTextColorId;
			mItemTextSize = (param.itemTextSize == 0) ? DEFAULT_TEXT_SIZE : param.itemTextSize;
			mItemSpace = (param.itemSpace == 0) ? Utils.getPxFromDp(mContext, DEFAULT_ITEM_SPACING) : param.itemSpace;
			mItemBarColorId = (param.itemBarColorId == 0) ? DEFAULT_TEXT_COLOR_ID : param.itemBarColorId;
			mItemBarHeight = (param.itemBarHeight == 0) ? Utils.getPxFromDp(mContext, DEFAULT_BAR_HEIGHT) : param.itemBarHeight;
			mItemBarBottomMargin = Utils.getPxFromDp(mContext, DEFAULT_BAR_BOTTOM_MARGIN);
			mCallback = param.callback;

			if (param.itemList != null) {
				mItemList.addAll(param.itemList);
			}
		} else {
			mItemTextColorId = DEFAULT_TEXT_COLOR_ID;
			mItemTextSize = DEFAULT_TEXT_SIZE;
			mItemSpace = Utils.getPxFromDp(mContext, DEFAULT_ITEM_SPACING);
			mItemBarColorId = DEFAULT_BAR_COLOR_ID;
			mItemBarHeight = Utils.getPxFromDp(mContext, DEFAULT_BAR_HEIGHT);
			mItemBarBottomMargin = Utils.getPxFromDp(mContext, DEFAULT_BAR_BOTTOM_MARGIN);
		}

		// set container
		mContainer = new LinearLayout(mContext);
		LayoutParams containerLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mContainer.setLayoutParams(containerLayoutParam);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);
		addView(mContainer);

		composeItems(0);
	}

	/**
	 * compose item view by defined setting
	 *
	 * @param selectedItemIndex
	 */
	private void composeItems(int selectedItemIndex) {
		if (mItemList != null) {
			int itemCount = mItemList.size();

			for (int i = 0; i < itemCount; i++) {
				final String item = mItemList.get(i);

				if (item == null) {
					continue;
				}

				// add item container
				FrameLayout itemContainer = new FrameLayout(mContext);
				LinearLayout.LayoutParams itemContainerLayoutParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				itemContainer.setLayoutParams(itemContainerLayoutParam);

				// set item space
				int leftPadding = (i == 0) ? mItemSpace * 2 : mItemSpace;
				int rightPadding = (i == (itemCount - 1)) ? mItemSpace * 2 : mItemSpace;
				itemContainer.setPadding(leftPadding, 0, rightPadding, 0);

				// add item data
				TextView itemTextView = new TextView(mContext);
				LayoutParams itemTextViewLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				itemTextViewLayoutParam.gravity = Gravity.CENTER;
				itemTextView.setLayoutParams(itemTextViewLayoutParam);
				itemTextView.setText(item);
				itemTextView.setTextColor(getColorValue((i == selectedItemIndex) ? mItemTextColorId : DEFAULT_UNSELECTED_TEXT_COLOR_ID));
				itemTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mItemTextSize);
				itemContainer.addView(itemTextView);
				measureTextView(itemTextView);

				// add item bottom bar
				View itemBarView = new View(mContext);
				LayoutParams itemBarViewLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				itemBarViewLayoutParam.height = mItemBarHeight;
				itemBarViewLayoutParam.width = itemTextView.getMeasuredWidth();
				itemBarViewLayoutParam.gravity = Gravity.BOTTOM;
				itemBarViewLayoutParam.bottomMargin = mItemBarBottomMargin;
				itemBarView.setBackgroundColor(getColorValue(mItemBarColorId));
				itemBarView.setLayoutParams(itemBarViewLayoutParam);
				itemBarView.setVisibility((i == selectedItemIndex) ? View.VISIBLE : View.GONE);
				itemContainer.addView(itemBarView);
				itemContainer.setTag(i);
				mContainer.addView(itemContainer);

				itemContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Integer index = (Integer) view.getTag();

						if (index != null && mPreviousSelectedItemIndex != index) {
							changeItemStatus(index);

							if (mCallback != null) {
								mCallback.changeListContentsByIndex(index, false);
							}
						}
					}
				});
			}
		}
	}

	/**
	 * change item status according by select index
	 *
	 * @param selectedItemIndex
	 */
	public void changeItemStatus(int selectedItemIndex) {
		mPreviousSelectedItemIndex = selectedItemIndex;
		int itemCount = mContainer.getChildCount();

		for (int i = 0; i < itemCount; i++) {
			View itemContainer = mContainer.getChildAt(i);

			if (itemContainer != null && itemContainer instanceof FrameLayout) {
				int itemContainerChildCount = ((FrameLayout) itemContainer).getChildCount();

				if (itemContainerChildCount > 1) {
					TextView itemTextView = (TextView) ((FrameLayout) itemContainer).getChildAt(0);
					View itemBarView = ((FrameLayout) itemContainer).getChildAt(1);

					itemTextView.setTextColor(getColorValue((i == selectedItemIndex) ? mItemTextColorId : DEFAULT_UNSELECTED_TEXT_COLOR_ID));
					itemBarView.setVisibility((i == selectedItemIndex) ? View.VISIBLE : View.GONE);
				}
			}
		}
	}

	/**
	 * get current selected item index
	 *
	 * @return
	 */
	public int getCurrentSelectedItem() {
		return mPreviousSelectedItemIndex;
	}

	/**
	 * measure TextView for defining width and height according by text
	 *
	 * @param view
	 */
	private void measureTextView(TextView view) {
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * get color value related to colorId
	 *
	 * @param colorId
	 * @return
	 */
	private int getColorValue(int colorId) {
		return mContext.getResources().getColor(colorId);
	}

	/**
	 * set whether freeze touch event
	 *
	 * @param isFreeze
	 */
	public void setFreezeTouchEvent(boolean isFreeze) {
		mIsFreezeTouchEvent = isFreeze;
	}

	/**
	 * builder which is for setting various option and composing
	 * HorizontalScrollTab according by this setting
	 *
	 * @author lordvader
	 *
	 */
	public static class Builder {
		private Context context;
		private Param param;

		public Builder(Context context) {
			this.context = context;
			param = new Param();
		}

		/**
		 * set item text colorId
		 *
		 * @param colorId
		 */
		public Builder setItemTextColorId(int colorId) {
			param.itemTextColorId = colorId;
			return this;
		}

		/**
		 * set item text size. It's unit is dip itself, so don't try to convert
		 * defined dimension resourceId
		 *
		 * @param size
		 */
		public Builder setItemTextSize(int size) {
			param.itemTextSize = size;
			return this;
		}

		/**
		 * set item space between each item (unit : px)
		 *
		 * @param space
		 */
		public Builder setItemSpace(int space) {
			param.itemSpace = space;
			return this;
		}

		/**
		 * set item's bottom bar colorId
		 *
		 * @param colorId
		 */
		public Builder setItemBarColorId(int colorId) {
			param.itemBarColorId = colorId;
			return this;
		}

		/**
		 * set item list
		 *
		 * @param itemList
		 */
		public Builder setItemList(List<String> itemList) {
			param.itemList = itemList;
			return this;
		}

		/**
		 * set CrossListView.Callback for handle such view's event
		 *
		 * @param callback
		 * @return
		 */
		public Builder setCallback(CrossListView.Callback callback) {
			param.callback = callback;
			return this;
		}

		/**
		 * set bar height (unit : px)
		 *
		 * @param barHeight
		 * @return
		 */
		public Builder setBarHeight(int barHeight) {
			param.itemBarHeight = barHeight;
			return this;
		}

		/**
		 * get HorizontalScrollView
		 *
		 * @return
		 */
		public HorizontalScrollTab getView() {
			return new HorizontalScrollTab(context, param);
		}

		/**
		 * get HorizontalScrollView applying by LayoutParams
		 *
		 * @param layoutParams
		 * @return
		 */
		public HorizontalScrollTab getView(ViewGroup.LayoutParams layoutParams) {
			HorizontalScrollTab view = new HorizontalScrollTab(context, param);

			if (layoutParams != null) {
				view.setLayoutParams(layoutParams);
			}

			return view;
		}

	}

	/**
	 * handle various option value
	 *
	 * @author lordvader
	 *
	 */
	private static class Param {
		private int itemTextColorId;
		private int itemTextSize;
		private int itemSpace;
		private int itemBarColorId;
		private int itemBarHeight;
		private CrossListView.Callback callback;
		private List<String> itemList;
	}

}
