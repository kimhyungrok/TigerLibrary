package com.empire.tigerlibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.util.Utils;

/**
 * page indicator for showing current page in ViewPager or pagedView
 *
 * @author lordvader
 *
 */
public class PageIndicator extends LinearLayout {
	private final int DEFAULT_ICON_ID = R.drawable.selector_page_indicator;
	private final int DEFAULT_ICON_SIZE = R.dimen.dp10;
	private final int DEFALUT_PAGE_COUNT = 3;
	private final int DEFAULT_ICON_SPACE = R.dimen.page_indicator_space;

	private Context mContext;
	private int mPageCount;
	private int mIconId;
	private int mIconSize;
	private int mIconSpace;

	public PageIndicator(Context context) {
		super(context);
		initialize(context, null);
	}

	public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs);
	}

	public PageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	/**
	 * initialize this view by default setting
	 *
	 * @param context
	 * @param attrs
	 */
	private void initialize(Context context, AttributeSet attrs) {
		mContext = context;

		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator);
			mIconId = typedArray.getResourceId(R.styleable.PageIndicator_setIcon, DEFAULT_ICON_ID);
			mPageCount = typedArray.getInteger(R.styleable.PageIndicator_pageCount, DEFALUT_PAGE_COUNT);
			mIconSize = typedArray.getDimensionPixelSize(R.styleable.PageIndicator_setIconSize, DEFAULT_ICON_SIZE);
		} else {
			mIconId = DEFAULT_ICON_ID;
			mPageCount = DEFALUT_PAGE_COUNT;
			mIconSize = Utils.getPxFromDp(mContext, DEFAULT_ICON_SIZE);
		}

		mIconSpace = Utils.getPxFromDp(mContext, DEFAULT_ICON_SPACE);
		setupView();
	}

	/**
	 * setup view
	 */
	private void setupView() {
		setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams iconLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconLayoutParam.weight = 1;
		iconLayoutParam.width = mIconSize;
		iconLayoutParam.height = mIconSize;
		iconLayoutParam.leftMargin = mIconSpace;
		iconLayoutParam.rightMargin = mIconSpace;

		for (int i = 0; i < mPageCount; i++) {
			ImageView iconView = new ImageView(mContext);
			iconView.setImageResource(mIconId);
			this.addView(iconView, iconLayoutParam);
		}
	}

	/**
	 * select specific page's indicator
	 *
	 * @param page
	 */
	public void selectIndicator(int page) {
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View childView = this.getChildAt(i);

			if (childView != null) {
				childView.setSelected(i == page);
			}
		}
	}
}
