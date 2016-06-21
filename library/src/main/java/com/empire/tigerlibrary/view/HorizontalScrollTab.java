package com.empire.tigerlibrary.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.empire.tigerlibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * tab container which is scrolled by horizontally
 * Created by lordvader on 2015. 7. 1..
 */
public class HorizontalScrollTab extends HorizontalScrollView {
    private final int TAB_ITEM_PADDING = R.dimen.dp10;
    private final int TAB_ITEM_TEXT_COLOR_ID = R.color.color_white;
    private final int TAB_ITEM_TEXT_SIZE = R.dimen.hscroll_tab_item_text_size;
    private final int TAB_ITEM_BOTTOM_LINE_HEIGHT = R.dimen.dp2;
    private final int TAB_ITEM_BOTTOM_LINE_COLOR_ID = R.color.color_white;
    private final float TAB_ITEM_SELECT_ALPHA = 1.0f;
    private final float TAB_ITEM_UNSELECT_TEXT_ALPHA = 0.6f;

    private final int SCROLL_TO_LEFT = 0;
    private final int SCROLL_TO_RIGHT = 1;

    private RelativeLayout mContainer;
    private LinearLayout mTabContainer;
    private View mBottomLineView;
    private ArrayList<TextView> mTabItemList = new ArrayList<TextView>();
    private StyleParam mStyleParam = new StyleParam();

    private Context mContext;
    private TabItemSelectedListener mTabItemSelectedListener;
    private int mCurrentBottomLineLeft = -1;
    private int mCurrentBottomLineRight = -1;
    private boolean mIsResetBottomLineX;
    private int mPrevPage = -1;
    private int mPrevPageProgress;
    private int mDisplayWidth;
    private float mPrevScrollX = 0;
    private float mScrollRatio = 1.0f;
    private int mScrollingleftBoundary;
    private int mScrollingRightBoundary;

    private OnClickListener mTabItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            setTabItemSelected(view, true);
        }
    };

//    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
//        private int currentPosition;
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            onScrolled(position, positionOffsetPixels);
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            currentPosition = position;
//            setTabItemSelected(mTabItemList.get(currentPosition), false);
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//            if (state == ViewPager.SCROLL_STATE_IDLE) {
//                adjustBottomLine(mTabItemList.get(currentPosition));
//            }
//        }
//    };

    private TabPageChangeListener mViewPagerPageChangeListener = new TabPageChangeListener();

    public HorizontalScrollTab(Context context) {
        super(context);
        initialize(context);
    }

    public HorizontalScrollTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public HorizontalScrollTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    /**
     * initialize this view
     *
     * @param context
     */
    private void initialize(Context context) {
        mContext = context;
        mDisplayWidth = getDisplayWidth();

        setupLayout();
        initTabItemAppearance();
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * setup default layout
     */
    private void setupLayout() {
        // root container
        mContainer = new RelativeLayout(mContext);
        LayoutParams containerLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(containerLayoutParam);

        // tab container
        mTabContainer = new LinearLayout(mContext);
        mTabContainer.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams tabContainerLayoutParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mTabContainer.setLayoutParams(tabContainerLayoutParam);
        mContainer.addView(mTabContainer);
    }

    /**
     * initialize tabitem appearance setting
     */
    private void initTabItemAppearance() {
        mStyleParam.textColor = mContext.getResources().getColor(TAB_ITEM_TEXT_COLOR_ID);
        mStyleParam.textSize = getPxFromDp(TAB_ITEM_TEXT_SIZE);
        mStyleParam.textSpacing = getPxFromDp(TAB_ITEM_PADDING);
        mStyleParam.isTextBold = false;
        mStyleParam.bottomLineColor = mContext.getResources().getColor(TAB_ITEM_BOTTOM_LINE_COLOR_ID);
        mStyleParam.bottomLineHeight = getPxFromDp(TAB_ITEM_BOTTOM_LINE_HEIGHT);
    }

    /**
     * set style param
     *
     * @param styleParam
     */
    private void setStyleParam(StyleParam styleParam) {
        if (styleParam == null) {
            return;
        }
        if (styleParam.textColor != null) {
            mStyleParam.textColor = styleParam.textColor;
        }
        if (styleParam.textSize != null) {
            mStyleParam.textSize = styleParam.textSize;
        }
        if (styleParam.textSpacing != null) {
            mStyleParam.textSpacing = styleParam.textSpacing;
        }
        if (styleParam.isTextBold != null) {
            mStyleParam.isTextBold = styleParam.isTextBold;
        }
        if (styleParam.bottomLineColor != null) {
            mStyleParam.bottomLineColor = styleParam.bottomLineColor;
        }
        if (styleParam.bottomLineHeight != null) {
            mStyleParam.bottomLineHeight = styleParam.bottomLineHeight;
        }
    }

    /**
     * set tabitem text appearance
     *
     * @param tabItemView
     */
    private void setTabItemTextAppearance(TextView tabItemView) {
        if (tabItemView != null) {
            // default style
            tabItemView.setEllipsize(TextUtils.TruncateAt.END);
            tabItemView.setGravity(Gravity.CENTER);
            tabItemView.setSingleLine(true);

            // custom style
            tabItemView.setTextColor(mStyleParam.textColor);
            tabItemView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mStyleParam.textSize);
            tabItemView.setPadding(mStyleParam.textSpacing, 0, mStyleParam.textSpacing, 0);

            if (mStyleParam.isTextBold) {
                tabItemView.setTypeface(tabItemView.getTypeface(), Typeface.BOLD);
            }

            tabItemView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }
    }

    /**
     * set selected tab item's text and bottom line color change
     *
     * @param view
     */
    private void setTabItemSelected(View view, boolean isAdjustBottomLine) {
        for (TextView tabItemView : mTabItemList) {
            if (view == tabItemView) {
                tabItemView.setAlpha(TAB_ITEM_SELECT_ALPHA);
                if (isAdjustBottomLine) {
                    adjustBottomLine(tabItemView);
                }
            } else {
                tabItemView.setAlpha(TAB_ITEM_UNSELECT_TEXT_ALPHA);
            }
        }

        if (mTabItemSelectedListener != null) {
            TextView itemView = (TextView) view;
            mTabItemSelectedListener.onSelected(mTabItemList.indexOf(itemView));
        }
    }

    /**
     * adjust bottom line aligned by tabItemView
     *
     * @param referenceView
     */
    private void adjustBottomLine(View referenceView) {
        if (mBottomLineView == null) {
            mBottomLineView = new View(mContext);
            mBottomLineView.setBackgroundColor(mStyleParam.bottomLineColor);

            RelativeLayout.LayoutParams bottomLineViewLayoutParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                    .WRAP_CONTENT);
            bottomLineViewLayoutParam.width = referenceView.getMeasuredWidth();
            bottomLineViewLayoutParam.height = mStyleParam.bottomLineHeight;
            bottomLineViewLayoutParam.alignWithParent = true;
            bottomLineViewLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mBottomLineView.setLayoutParams(bottomLineViewLayoutParam);
            mContainer.addView(mBottomLineView);
            this.addView(mContainer);
        } else {
            int referenceViewLeft = referenceView.getLeft();
            int referenceViewRight = referenceView.getRight();

            if (mBottomLineView.getLeft() != referenceViewLeft || mBottomLineView.getRight() != referenceViewRight) {
                mBottomLineView.layout(referenceViewLeft, mBottomLineView.getTop(), referenceViewRight, mBottomLineView.getBottom());
            }
        }
    }

    /**
     * set tab item list
     *
     * @param tabItemList
     */
    public void setTabItemList(List<String> tabItemList) {
        setTabItemList(tabItemList, null);
    }

    /**
     * set tab item list (include style param)
     *
     * @param tabItemList
     * @param styleParam
     */
    public void setTabItemList(List<String> tabItemList, StyleParam styleParam) {
        if (mTabItemList.size() > 0) {
            mTabItemList.clear();
            mTabContainer.removeAllViews();
        }

        if (styleParam != null) {
            setStyleParam(styleParam);
        }

        // set tab item
        for (String tabItem : tabItemList) {
            // set tabitem text
            TextView tabItemView = new TextView(mContext);
            tabItemView.setText(tabItem);
            tabItemView.setGravity(Gravity.CENTER);
            setTabItemTextAppearance(tabItemView);

            // set tabItemView & bottomLineView layout
            LinearLayout.LayoutParams tabItemLayoutParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            tabItemView.setLayoutParams(tabItemLayoutParam);
            tabItemView.setOnClickListener(mTabItemOnClickListener);

            mTabContainer.addView(tabItemView);
            mTabItemList.add(tabItemView);
        }

        // set first tab item selected
        if (mTabItemList.size() > 0) {
            setTabItemSelected(mTabItemList.get(0), true);
        }

        calculateScrollRatio();
    }

    /**
     * calculate scroll ratio which is used for scrolling tab item
     */
    private void calculateScrollRatio() {
        mTabContainer.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int tabContainerWidth = mTabContainer.getMeasuredWidth();
        int tabItemCount = mTabItemList.size();
        int totalPageWidth = tabItemCount * mDisplayWidth;

        mScrollRatio = (float) tabContainerWidth / (float) totalPageWidth;
        mScrollingleftBoundary = (int) (mDisplayWidth / 2 / mScrollRatio);
        mScrollingRightBoundary = totalPageWidth - mScrollingleftBoundary;
    }

    /**
     * add tabItemSelectedListener which is handling custom action when click tab item
     *
     * @param tabItemSelectedListener
     */
    public void addTabItemSelectedListener(TabItemSelectedListener tabItemSelectedListener) {
        mTabItemSelectedListener = tabItemSelectedListener;
    }

    /**
     * get tab item count
     *
     * @return
     */
    public int getTabItemCount() {
        return mTabItemList.size();
    }

    /**
     * handle scroll callback
     *
     * @param page
     * @param offset
     */
    public void onScrolled(int page, int offset) {
        if (page == 0 && offset == 0) {
            return;
        }

        int totalPageOffset = (page * mDisplayWidth) + offset;
        float convertedOffset = totalPageOffset * mScrollRatio;
        float differ = convertedOffset - mPrevScrollX;
        int direction = (differ >= 0) ? SCROLL_TO_RIGHT : SCROLL_TO_LEFT;

        if (direction == SCROLL_TO_RIGHT) {
            if (totalPageOffset >= mScrollingleftBoundary) {
                this.scrollBy((int) differ, 0);
            }
        } else {
            if (totalPageOffset <= mScrollingRightBoundary) {
                this.scrollBy((int) differ, 0);
            }
        }

        // adjust bottom line
        if (page < mTabItemList.size() - 1) {
            if (mIsResetBottomLineX) {
                TextView pivotTabItemView = mTabItemList.get(page);
                mCurrentBottomLineLeft = pivotTabItemView.getLeft();
                mCurrentBottomLineRight = pivotTabItemView.getRight();
                mPrevPageProgress = 0;
                mIsResetBottomLineX = false;
            }

            TextView rightTabItemView = mTabItemList.get(page + 1);
            TextView leftTabItemView = mTabItemList.get(page);
            int pageProgress = offset * 100 / mDisplayWidth;

            if (direction == SCROLL_TO_RIGHT && mPrevPageProgress >= 99) {
                pageProgress = 100;
            }

            mPrevPageProgress = pageProgress;
            int rightEdgeOffset = pageProgress * rightTabItemView.getWidth() / 100;
            int leftEdgeOffset = pageProgress * leftTabItemView.getWidth() / 100;

            if (mPrevPage == page) {
                mBottomLineView.layout(mCurrentBottomLineLeft + leftEdgeOffset, mBottomLineView.getTop(), mCurrentBottomLineRight +
                        rightEdgeOffset, mBottomLineView.getBottom());
            } else {
                mIsResetBottomLineX = true;
            }
        }

        mPrevScrollX = convertedOffset;
        mPrevPage = page;
    }

    public ViewPager.OnPageChangeListener getPageChangeListener() {
        return mViewPagerPageChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(this.getLayoutParams().height, MeasureSpec.EXACTLY));
    }

    /**
     * get device screen width
     *
     * @return
     */
    public int getDisplayWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;
    }

    /**
     * change dp to px
     *
     * @param dimensionId
     * @return
     */
    public int getPxFromDp(int dimensionId) {
        return mContext.getResources().getDimensionPixelSize(dimensionId);
    }

    /*******************************************************************************
     * utility method
     *******************************************************************************/

    /**
     * listener for doing custom action when click tab item
     */
    public interface TabItemSelectedListener {
        /**
         * handle onSelected
         */
        void onSelected(int index);
    }

    /*******************************************************************************
     * inner class and interface
     *******************************************************************************/

    /**
     * builder for setting textview and bottom line style
     */
    public static class StyleBuilder {
        private StyleParam param;

        public StyleBuilder() {
            param = new StyleParam();
        }

        /**
         * set text color
         *
         * @param color
         */
        public void setTextColor(int color) {
            param.textColor = color;
        }

        /**
         * set text size (its unit is pixel)
         *
         * @param textSize
         */
        public void setTextSize(int textSize) {
            param.textSize = textSize;
        }

        /**
         * set text left and right padding (its unit is pixel)
         *
         * @param textSpacing
         */
        public void setTextSpacing(int textSpacing) {
            param.textSpacing = textSpacing;
        }

        /**
         * set text style whether bold
         *
         * @param isTextBold
         */
        public void setTextBold(boolean isTextBold) {
            param.isTextBold = isTextBold;
        }

        /**
         * set bottom line color
         *
         * @param color
         */
        public void setBottomLineColor(int color) {
            param.bottomLineColor = color;
        }

        /**
         * set bottom line height (its unit is pixel)
         *
         * @param bottomLineHeight
         */
        public void setBottomLineHeight(int bottomLineHeight) {
            param.bottomLineHeight = bottomLineHeight;
        }

        /**
         * get style param
         *
         * @return
         */
        public StyleParam getStyleParam() {
            return param;
        }
    }

    /**
     * define style item for manipulating
     */
    private static class StyleParam {
        Integer textColor;
        Integer textSize;
        Integer textSpacing;
        Boolean isTextBold;
        Integer bottomLineColor;
        Integer bottomLineHeight;
    }

    /**
     * tab page change listener
     */
    public class TabPageChangeListener implements ViewPager.OnPageChangeListener {
        private int currentPosition;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            onScrolled(position, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            setTabItemSelected(mTabItemList.get(currentPosition), false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                adjustBottomLine(mTabItemList.get(currentPosition));
            }
        }
    }
}
