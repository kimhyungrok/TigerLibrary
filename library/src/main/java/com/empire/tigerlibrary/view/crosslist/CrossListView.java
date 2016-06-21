package com.empire.tigerlibrary.view.crosslist;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.animation.AnimationActiveChecker;
import com.empire.tigerlibrary.animation.CustomAnimationListener;
import com.empire.tigerlibrary.tool.SimpleActionListener;
import com.empire.tigerlibrary.tool.motion.CommonGestureDetector;
import com.empire.tigerlibrary.tool.motion.CommonMotionListener;
import com.empire.tigerlibrary.util.Utils;
import com.empire.tigerlibrary.util.ViewUtil;
import com.empire.tigerlibrary.view.SystemBarTintManager;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * custom ListView which is possible to keep another list group horizontally
 *
 * @author lordvader
 */
public class CrossListView extends FrameLayout implements OnScrollListener {
    public static final int ADAPTER_CHANGE_MOTION_TYPE1 = 0;
    public static final int ADAPTER_CHANGE_MOTION_TYPE2 = 1;
    private final int ADAPTER_CHANGE_MOTION_TO_RIGHT = 0;
    private final int ADAPTER_CHANGE_MOTION_FROM_RIGHT = 1;
    private final int ADAPTER_CHANGE_MOTION_FROM_LEFT = 2;
    private final int ADAPTER_CHANGE_MOTION_TO_LEFT = 3;
    private final int ADAPTER_CHANGE_MOTION_TYPE1_DURATION = 1000;

    private final int CHANGE_ADAPTER_TO_LEFT = 0;
    private final int CHANGE_ADAPTER_TO_RIGHT = 1;

    private final int PRE_SWIPE_DOWN_SENSITIVITY = 60;

    /**
     * circular progress bar
     */
    private final int PROGRESS_BAR_SIZE = R.dimen.dp50;
    private final int PROGRESS_BAR_COLOR = R.color.color_blue;
    private final int PROGRESS_BAR_BOTTOM_MARGIN = R.dimen.dp50;
    private final float FILTER_ALPHA = 0.8f;

    /**
     * horizontal tab
     */
    private final int TAB_SPACE = R.dimen.dp15;
    private final int TAB_ITEM_COLOR = R.color.color_white;
    private final int TAB_BAR_HEIGHT = R.dimen.crosslist_category_bar_height;
    private final int TAB_FILTER_COLOR = R.color.color_black;

    /**
     * view holder and adapter
     */
    private Context mContext;
    private ListView mListView;
    private View mFixedView;
    private View mDoppelgangerView;
    private HorizontalScrollTab mScrollTabForFixedView;
    private HorizontalScrollTab mScrollTabForDoppelgangerView;
    private ProgressBar mProgressBar;
    private CrossListAdapter mAdapter;

    /**
     * for handling scrollY
     */
    private float mPreviousRawY = -1;
    private boolean mIsApplyPreviousScroll;
    private int mPreviousTopY;
    private int mFirstVisibleItem;
    private BlockingDeque<Integer> mPreviousHeightStack = new LinkedBlockingDeque<Integer>();

    private int mListViewDividerHeight;
    private int mFixedAreaHeight;
    private int mHeaderHeight;
    private int mStatusBarHeight;
    private int mCurrentIndexOfAdapter;
    private AnimationActiveChecker mMotionChecker = new AnimationActiveChecker();
    private int mAdapterChangeMotionType = ADAPTER_CHANGE_MOTION_TYPE2;
    private SimpleActionListener mSwipeDownActionListener;
    private SystemBarTintManager mTintManager;
    private boolean mIsContainerFitsSystemWindows;

    private CommonGestureDetector mTriggerGestureDetector;
    private CommonMotionListener mTriggerMotionListener = new CommonMotionListener() {
        private boolean isInBoundOfFixedView(MotionEvent event) {
            if (mFixedView != null) {
                Rect fixedViewBoundary = ViewUtil.getViewBoundary(mFixedView);
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();

                if (rawX > fixedViewBoundary.left && rawX < fixedViewBoundary.right && rawY < fixedViewBoundary.bottom && rawY > fixedViewBoundary
                        .top) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void swipeToLeft(View view, MotionEvent event) {
            if (isInBoundOfFixedView(event)) {
                return;
            }

            changeListContents(CHANGE_ADAPTER_TO_LEFT);
        }

        @Override
        public void swipeToRight(View view, MotionEvent event) {
            if (isInBoundOfFixedView(event)) {
                return;
            }

            changeListContents(CHANGE_ADAPTER_TO_RIGHT);
        }

        @Override
        public void swipeDown(View view) {
            // handle swipe down when listView is anchored to bottom in display
            if (isListViewAttachedToBottom()) {
                if (mSwipeDownActionListener != null) {
                    mSwipeDownActionListener.doAction();
                }
            }
        }
    };

    private Callback mCallback = new Callback() {
        @Override
        public void changeListContentsByIndex(int index, boolean isRefreshTab) {
            if (isRefreshTab) {
                if (mScrollTabForFixedView != null) {
                    mScrollTabForFixedView.changeItemStatus(index);
                }

                if (mScrollTabForDoppelgangerView != null) {
                    mScrollTabForDoppelgangerView.changeItemStatus(index);
                }
            }

            CrossListView.this.changeListContentsByIndex(index);
        }
    };

    public CrossListView(Context context) {
        super(context);
        initialize(context, null);
    }

    public CrossListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    public CrossListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public CrossListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    /**
     * initialize this view by default setting
     */
    private void initialize(Context context, AttributeSet attrs) {
        mContext = context;
        mStatusBarHeight = ViewUtil.getStatusBarHeight(mContext);
        setStatusBarTint();

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CrossListView);
            mHeaderHeight = typedArray.getDimensionPixelSize(R.styleable.CrossListView_header_height, 0);
            mFixedAreaHeight = typedArray.getDimensionPixelSize(R.styleable.CrossListView_fixedArea_height, 0);
            mListViewDividerHeight = typedArray.getDimensionPixelSize(R.styleable.CrossListView_dividerHeight, 0);
        }

        mTriggerGestureDetector = new CommonGestureDetector(context);
        mTriggerGestureDetector.setMotionListener(mTriggerMotionListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mMotionChecker.isAnimationActivating()) {
            return true;
        }

        boolean isIntercept = mTriggerGestureDetector.onHandleTouchEvent(this, ev);

        if (!isIntercept) {
            isIntercept = isDetectePreSwipeDown(ev);
        }

        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mMotionChecker.isAnimationActivating()) {
            return true;
        }

        mTriggerGestureDetector.onHandleTouchEvent(this, ev);

        return true;
    }

    /**
     * check whether touch event is possible to occur swipe down event
     *
     * @param ev
     * @return
     */
    private boolean isDetectePreSwipeDown(MotionEvent ev) {
        if (isListViewAttachedToBottom()) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mPreviousRawY = ev.getRawY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (ev.getRawY() - mPreviousRawY > PRE_SWIPE_DOWN_SENSITIVITY) {
                        return true;
                    }

                    break;
                }
            }
        }

        return false;
    }

    /**
     * check whether ListView is attached to bottom
     *
     * @return
     */
    private boolean isListViewAttachedToBottom() {
        return mFirstVisibleItem == 0 && mListView.getChildCount() > 0 && mListView.getChildAt(0).getTop() == 0;
    }

    /**
     * set SwipeDownActionListener which is called when detected swipe down
     * motion
     *
     * @param actionListener
     */
    public void setSwipeDownActionListener(SimpleActionListener actionListener) {
        mSwipeDownActionListener = actionListener;
    }

    /**
     * copy fixed view which is showed when fixed area is scrolled to over
     * screen
     */
    private void makeFixedViewDoppelganger() {
        View[] rawFixedViews = getFixedView(Gravity.TOP, mIsContainerFitsSystemWindows ? 0 : mStatusBarHeight);
        mDoppelgangerView = rawFixedViews[0];
        mScrollTabForDoppelgangerView = (HorizontalScrollTab) rawFixedViews[1];
        setColorFilterInFixedView(getColorValue(TAB_FILTER_COLOR), false);
        this.addView(mDoppelgangerView);
    }

    /**
     * get fixed view
     *
     * @param gravity
     * @return
     */
    private View[] getFixedView(int gravity, int extraTopMargin) {
        FrameLayout headerContainer = new FrameLayout(mContext);
        LayoutParams headerViewLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerViewLayoutParam.gravity = gravity;
        headerViewLayoutParam.height = mFixedAreaHeight;
        headerViewLayoutParam.topMargin = extraTopMargin;
        headerContainer.setLayoutParams(headerViewLayoutParam);

        // set filter background
        View filterView = new View(mContext);
        LayoutParams filterViewLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        filterView.setLayoutParams(filterViewLayoutParam);
        headerContainer.addView(filterView);

        // set category tab
        HorizontalScrollTab.Builder horizontalScrollTabBuilder = new HorizontalScrollTab.Builder(mContext);
        horizontalScrollTabBuilder.setItemBarColorId(TAB_ITEM_COLOR).setItemSpace(Utils.getPxFromDp(mContext, TAB_SPACE)).setItemTextColorId
                (TAB_ITEM_COLOR).setBarHeight(Utils.getPxFromDp(mContext, TAB_BAR_HEIGHT)).setItemList(mAdapter.getTitleList()).setItemTextSize(19)
                .setCallback(mCallback);
        HorizontalScrollTab scrollTab = horizontalScrollTabBuilder.getView(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        headerContainer.addView(scrollTab);

        return new View[]{headerContainer,
                          scrollTab};
    }

    /**
     * set color filter in FixedView (also DoppelgangerView)
     *
     * @param color
     */
    public void setColorFilterInFixedView(int color, boolean isApplyAll) {
        View[] targetViews = null;

        if (isApplyAll) {
            targetViews = new View[]{mFixedView,
                                     mDoppelgangerView};
        } else {
            targetViews = new View[]{mDoppelgangerView};
        }

        for (View targetView : targetViews) {
            if (targetView != null && ((ViewGroup) targetView).getChildCount() > 0) {
                View filterView = ((ViewGroup) targetView).getChildAt(0);

                if (filterView != null) {
                    filterView.setBackgroundColor(color);
                    filterView.setAlpha(FILTER_ALPHA);
                }
            }
        }
    }

    /**
     * remove color filter in FixedView (also DoppelgangerView)
     */
    public void removeColorFilterInFixedView() {
        setColorFilterInFixedView(getColorValue(R.color.color_transparent), true);
    }

    /**
     * set circular progress bar default setting
     */
    private void setCircularProgressBar() {
        mProgressBar = new ProgressBar(mContext);
        int size = Utils.getPxFromDp(mContext, PROGRESS_BAR_SIZE);
        LayoutParams progressBarLayoutParam = new LayoutParams(size, size);
        progressBarLayoutParam.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        progressBarLayoutParam.bottomMargin = Utils.getPxFromDp(mContext, PROGRESS_BAR_BOTTOM_MARGIN);
        mProgressBar.setLayoutParams(progressBarLayoutParam);

        ColorStateList stateList = ColorStateList.valueOf(getColorValue(PROGRESS_BAR_COLOR));
        // mProgressBar.setIndeterminateTintList(stateList);
        // mProgressBar.setIndeterminateTintMode(Mode.SRC_ATOP);
        mProgressBar.setVisibility(View.GONE);
        this.addView(mProgressBar);
    }

    /**
     * show circular progress bar
     */
    private void showCircularProgressBar() {
        if (mProgressBar.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mMotionChecker.add();
        }
    }

    /**
     * hide circular progress bar
     */
    private void hideCircularProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
            mMotionChecker.remove();
        }
    }

    /**
     * set main list view according by layout info and default setting value
     */
    private void setListView() {
        // set main list view
        mListView = new ListView(mContext);
        LayoutParams listViewLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mListView.setLayoutParams(listViewLayoutParam);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setDividerHeight(mListViewDividerHeight);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Utils.showToast(mContext, "hahaha");
            }
        });

        // set header view
        if (mHeaderHeight > 0) {
            FrameLayout headerContainer = new FrameLayout(mContext);
            AbsListView.LayoutParams headerViewLayoutParam = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            headerViewLayoutParam.height = mHeaderHeight;
            headerContainer.setLayoutParams(headerViewLayoutParam);

            if (mFixedAreaHeight > 0) {
                View[] rawFixedViews = getFixedView(Gravity.BOTTOM, 0);
                mFixedView = rawFixedViews[0];
                mScrollTabForFixedView = (HorizontalScrollTab) rawFixedViews[1];
                headerContainer.addView(mFixedView);
            }

            mListView.addHeaderView(headerContainer);
        }

        // adjust layout after adapter's changed
        mListView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mIsApplyPreviousScroll) {
                    int absPreviousTopY = Math.abs(mPreviousTopY);
                    mListView.scrollListBy(absPreviousTopY);
                    int absCurrentTopY = mListView.getChildCount() > 0 ? Math.abs(mListView.getChildAt(0).getTop()) : 0;

                    if (absPreviousTopY != absCurrentTopY) {
                        if (!mIsContainerFitsSystemWindows) {
                            absPreviousTopY -= mStatusBarHeight;
                        }

                        showAdjustHeightMotion(absPreviousTopY, absCurrentTopY);
                    }

                    if (mAdapterChangeMotionType == ADAPTER_CHANGE_MOTION_TYPE2) {
                        Integer changeMotionFromDirection = (Integer) mListView.getTag();

                        if (changeMotionFromDirection != null) {
                            showAdapterChangeMotion(mListView, changeMotionFromDirection);
                        }
                    }

                    mIsApplyPreviousScroll = false;
                }
            }
        });

        this.addView(mListView);
    }

    /**
     * set ListView's adapter
     *
     * @param adapter
     */
    public void setAdapter(CrossListAdapter adapter) {
        mAdapter = adapter;

        if (mAdapter != null && mAdapter.getCount() > 0) {
            setListView();
            setCircularProgressBar();

            if (mHeaderHeight > 0 && mFixedAreaHeight > 0) {
                makeFixedViewDoppelganger();
            }

            rawSetAdapter();
        }
    }

    /**
     * set ListView's adapter really
     */
    private void rawSetAdapter() {
        final ListAdapter itemAdapter = mAdapter.getListAdapter(mCurrentIndexOfAdapter);
        final CrossListDataCollector.Listener dataCollectorListener = new CrossListDataCollector.Listener() {
            @Override
            public void refreshAdapter(ListAdapter itemAdapter) {
                if (itemAdapter != null) {
                    mListView.setAdapter(itemAdapter);
                    hideCircularProgressBar();
                }
            }
        };

        if (itemAdapter == null) {
            showCircularProgressBar();
            mAdapter.getItem(mCurrentIndexOfAdapter).runDataCollector(dataCollectorListener);
        } else {
            dataCollectorListener.refreshAdapter(itemAdapter);
        }
    }

    /**
     * refresh adapter
     */
    public void refreshAdapter() {
        if (mListView != null) {
            mListView.setAdapter(null);

            if (mAdapter != null) {
                int childCount = mAdapter.getCount();

                for (int i = 0; i < childCount; i++) {
                    CrossListItem item = mAdapter.getItem(0);
                    item.removeListAdapter();
                }

                rawSetAdapter();
            }
        }
    }

    /**
     * change list contents which is related to param's index
     *
     * @param index
     */
    private void changeListContentsByIndex(int index) {
        if (mAdapter != null && mAdapter.getCount() > 0 && index > -1 && index < mAdapter.getCount() && mCurrentIndexOfAdapter != index) {
            final int changeMotionFromDirection = (mCurrentIndexOfAdapter < index) ? ADAPTER_CHANGE_MOTION_FROM_RIGHT :
                    ADAPTER_CHANGE_MOTION_FROM_LEFT;
            mCurrentIndexOfAdapter = index;
            final ListAdapter adapter = mAdapter.getListAdapter(mCurrentIndexOfAdapter);
            CrossListDataCollector.Listener dataCollectorListener = new CrossListDataCollector.Listener() {
                @Override
                public void refreshAdapter(final ListAdapter itemAdapter) {
                    View topView = mListView.getChildAt(0);

                    if (topView != null) {
                        final SimpleActionListener changeAdapterListener = new SimpleActionListener() {
                            @Override
                            public void doAction() {
                                mPreviousTopY = getScrollListByValue();
                                mIsApplyPreviousScroll = true;
                                mListView.setAdapter(itemAdapter);
                                mListView.setTag(changeMotionFromDirection);
                            }
                        };

                        changeAdapterListener.doAction();
                        hideCircularProgressBar();
                    }
                }
            };

            if (adapter == null) {
                showCircularProgressBar();
                mAdapter.getItem(mCurrentIndexOfAdapter).runDataCollector(dataCollectorListener);
            } else {
                dataCollectorListener.refreshAdapter(adapter);
            }
        }
    }

    /**
     * change list contents
     *
     * @param direction
     */
    private void changeListContents(int direction) {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            if (direction == CHANGE_ADAPTER_TO_LEFT && (mCurrentIndexOfAdapter - 1) < 0) {
                return;
            } else if (direction == CHANGE_ADAPTER_TO_RIGHT && (mCurrentIndexOfAdapter + 1) >= mAdapter.getCount()) {
                return;
            }

            final int changeMotionToDirection;
            final int changeMotionFromDirection;

            if (direction == CHANGE_ADAPTER_TO_LEFT) {
                mCurrentIndexOfAdapter--;
                changeMotionToDirection = ADAPTER_CHANGE_MOTION_TO_LEFT;
                changeMotionFromDirection = ADAPTER_CHANGE_MOTION_FROM_LEFT;
            } else {
                mCurrentIndexOfAdapter++;
                changeMotionToDirection = ADAPTER_CHANGE_MOTION_TO_RIGHT;
                changeMotionFromDirection = ADAPTER_CHANGE_MOTION_FROM_RIGHT;
            }

            final ListAdapter adapter = mAdapter.getListAdapter(mCurrentIndexOfAdapter);
            CrossListDataCollector.Listener dataCollectorListener = new CrossListDataCollector.Listener() {
                @Override
                public void refreshAdapter(final ListAdapter itemAdapter) {
                    if (itemAdapter != null) {
                        View topView = mListView.getChildAt(0);

                        if (topView != null) {
                            int postDelayTime;
                            final SimpleActionListener motionEndCallback;

                            switch (mAdapterChangeMotionType) {
                                case ADAPTER_CHANGE_MOTION_TYPE1: {
                                    postDelayTime = ADAPTER_CHANGE_MOTION_TYPE1_DURATION;
                                    showCircularProgressBar();
                                    motionEndCallback = new SimpleActionListener() {
                                        @Override
                                        public void doAction() {
                                            hideCircularProgressBar();
                                        }
                                    };
                                    break;
                                }
                                case ADAPTER_CHANGE_MOTION_TYPE2: {
                                    postDelayTime = showAdapterChangeMotion(mListView, changeMotionToDirection);
                                    motionEndCallback = new SimpleActionListener() {
                                        @Override
                                        public void doAction() {
                                            mListView.setTag(changeMotionFromDirection);
                                        }
                                    };

                                    break;
                                }
                                default: {
                                    postDelayTime = 0;
                                    motionEndCallback = null;
                                    break;
                                }
                            }

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    mPreviousTopY = getScrollListByValue();
                                    mIsApplyPreviousScroll = true;
                                    mListView.setAdapter(itemAdapter);

                                    if (mScrollTabForFixedView != null) {
                                        mScrollTabForFixedView.changeItemStatus(mCurrentIndexOfAdapter);
                                    }

                                    if (mScrollTabForDoppelgangerView != null) {
                                        mScrollTabForDoppelgangerView.changeItemStatus(mCurrentIndexOfAdapter);
                                    }

                                    if (motionEndCallback != null) {
                                        motionEndCallback.doAction();
                                    }
                                    hideCircularProgressBar();
                                }
                            }, postDelayTime);
                        }
                    }
                }
            };

            if (adapter == null) {
                showCircularProgressBar();
                mAdapter.getItem(mCurrentIndexOfAdapter).runDataCollector(dataCollectorListener);
            } else {
                dataCollectorListener.refreshAdapter(adapter);
            }
        }
    }

    /**
     * get adapter change motion type
     *
     * @return
     */
    public int getAdapterChangeMotionType() {
        return mAdapterChangeMotionType;
    }

    /**
     * set adapter change motion type
     *
     * @param motionType
     */
    public void setAdapterChangeMotionType(int motionType) {
        mAdapterChangeMotionType = motionType;
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
     * set status bar tint
     */
    private void setStatusBarTint() {
        if (isSupportStatusBarTint() && mContext instanceof Activity) {
            mTintManager = new SystemBarTintManager((Activity) mContext);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setStatusBarTintColor(getColorValue(TAB_FILTER_COLOR));
        }
    }

    /**
     * set status bar alpha
     *
     * @param alpha
     */
    private void setStatusBarAlpha(float alpha) {
        if (isSupportStatusBarTint() && mTintManager != null) {
            mTintManager.setTintAlpha(alpha);
        }
    }

    /**
     * check whether current device support status bar tint
     *
     * @return
     */
    private boolean isSupportStatusBarTint() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;

        if (view != null) {
            int childCount = view.getChildCount();

            if (childCount > 0) {
                View topView = view.getChildAt(0);

                if (topView != null) {
                    if (mPreviousHeightStack.size() - 1 < firstVisibleItem) {
                        mPreviousHeightStack.addLast(topView.getHeight() + mListView.getDividerHeight());
                    } else if (mPreviousHeightStack.size() - 1 > firstVisibleItem) {
                        mPreviousHeightStack.pollLast();
                    }
                }
            }
        }

        if (mFixedView != null) {
            int top = ViewUtil.getTopOnScreen(mFixedView);

            if (visibleItemCount > 0 && top < mStatusBarHeight) {
                if (mDoppelgangerView.getVisibility() == View.INVISIBLE) {
                    mScrollTabForDoppelgangerView.setScrollX(mScrollTabForFixedView.getScrollX());
                    mScrollTabForDoppelgangerView.changeItemStatus(mScrollTabForFixedView.getCurrentSelectedItem());
                    mDoppelgangerView.setVisibility(View.VISIBLE);
                    mFixedView.setVisibility(View.INVISIBLE);
                    setStatusBarAlpha(FILTER_ALPHA);
                }

                View applyAlphaTargetView = ((ViewGroup) mDoppelgangerView).getChildAt(0);

                if (firstVisibleItem == 0) {
                    adjustDoppelgangerViewAlpha(applyAlphaTargetView, calculateAlpha(mStatusBarHeight, FILTER_ALPHA, top));
                } else {
                    if (applyAlphaTargetView.getAlpha() < FILTER_ALPHA) {
                        adjustDoppelgangerViewAlpha(applyAlphaTargetView, FILTER_ALPHA);
                    }
                }
            } else {
                if (mDoppelgangerView.getVisibility() == View.VISIBLE) {
                    mScrollTabForFixedView.setScrollX(mScrollTabForDoppelgangerView.getScrollX());
                    mScrollTabForFixedView.changeItemStatus(mScrollTabForDoppelgangerView.getCurrentSelectedItem());
                    mDoppelgangerView.setVisibility(View.INVISIBLE);
                    mFixedView.setVisibility(View.VISIBLE);
                    setStatusBarAlpha(0f);
                }
            }
        }
    }

    /**
     * get extra scrollY
     *
     * @return
     */
    private int getExtraScrollY() {
        int extraScrollY = 0;
        int index = 0;
        int height = 0;
        int stackSize = mPreviousHeightStack.size();
        Iterator<Integer> stackIterator = mPreviousHeightStack.iterator();

        while (stackIterator.hasNext()) {
            height = stackIterator.next();

            if (index++ < stackSize - 1) {
                extraScrollY += height;
            }
        }

        return extraScrollY;
    }

    /**
     * get scroll list value after changing adapter
     *
     * @return
     */
    private int getScrollListByValue() {
        if (mFixedView.getVisibility() == View.VISIBLE) {
            return mListView.getChildAt(0).getTop();
        } else {
            return mFixedAreaHeight - mHeaderHeight;
        }
    }

    /**
     * calculate fixed view background alpha
     *
     * @param compareMax
     * @param maxAlpha
     * @param compareCurrent
     * @return
     */
    private float calculateAlpha(int compareMax, float maxAlpha, int compareCurrent) {
        return compareCurrent <= -compareMax ? maxAlpha : (maxAlpha - (maxAlpha / (float) (compareMax * 2) * (compareMax + compareCurrent)));
    }

    /**
     * adjust alpha of DoppelgangerView and status bar
     *
     * @param view
     * @param alpha
     */
    private void adjustDoppelgangerViewAlpha(View view, float alpha) {
        view.setAlpha(alpha);
        setStatusBarAlpha(alpha);
    }

    /**
     * get Callback which is able to use opened CrossListView's function
     *
     * @return
     */
    public Callback getCallback() {
        return mCallback;
    }

    /**
     * set whether FitsSystemWindows of this view's container is set true or
     * false
     *
     * @param isFitsSystemWindows
     */
    public void setContainerFitsSystemWindows(boolean isFitsSystemWindows) {
        mIsContainerFitsSystemWindows = isFitsSystemWindows;
    }

    /*******************************************************************************
     * effect
     *******************************************************************************/
    /**
     * show adapter content changing motion
     *
     * @param listView
     * @param motionType
     * @return
     */
    private int showAdapterChangeMotion(ListView listView, int motionType) {
        final int DURATION = 200;
        int childCount = listView.getChildCount();
        int totalAnimationTime = 0;
        int startIndex = mFirstVisibleItem == 0 ? 1 : 0;

        for (int i = startIndex; i < childCount; i++) {
            View childView = listView.getChildAt(i);

            if (childView != null) {
                AnimationSet animationSet = new AnimationSet(false);

                float fromX = 0;
                float toX = 0;
                float fromAlpha = 0;
                float toAlpha = 0;

                switch (motionType) {
                    case ADAPTER_CHANGE_MOTION_TO_LEFT: {
                        fromX = 0;
                        toX = childView.getWidth();
                        fromAlpha = 1f;
                        toAlpha = 0f;
                        break;
                    }
                    case ADAPTER_CHANGE_MOTION_FROM_RIGHT: {
                        fromX = childView.getWidth();
                        toX = 0;
                        fromAlpha = 0f;
                        toAlpha = 1f;
                        break;
                    }
                    case ADAPTER_CHANGE_MOTION_FROM_LEFT: {
                        fromX = -childView.getWidth();
                        toX = 0;
                        fromAlpha = 0f;
                        toAlpha = 1f;
                        break;
                    }
                    case ADAPTER_CHANGE_MOTION_TO_RIGHT: {
                        fromX = 0;
                        toX = -childView.getWidth();
                        fromAlpha = 1f;
                        toAlpha = 0f;
                        break;
                    }

                }

                TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, 0, 0);
                AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);

                animationSet.addAnimation(translateAnimation);
                animationSet.addAnimation(alphaAnimation);
                animationSet.setStartOffset(DURATION * (i - 1));
                animationSet.setDuration(DURATION);
                animationSet.setAnimationListener(new CustomAnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mMotionChecker.add();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mMotionChecker.remove();
                    }
                });

                if (motionType == ADAPTER_CHANGE_MOTION_TO_LEFT || motionType == ADAPTER_CHANGE_MOTION_TO_RIGHT) {
                    animationSet.setFillAfter(true);
                }

                totalAnimationTime += DURATION;
                childView.startAnimation(animationSet);
            }
        }

        return totalAnimationTime;
    }

    /**
     * show adjust height motion between category
     *
     * @param absPreviousTopY
     * @param absCurrentTopY
     */
    private void showAdjustHeightMotion(int absPreviousTopY, int absCurrentTopY) {
        final int DURATION = 300;

        TranslateAnimation transAnimation = new TranslateAnimation(0, 0, absCurrentTopY - absPreviousTopY, 0);
        transAnimation.setDuration(DURATION);
        transAnimation.setAnimationListener(new CustomAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMotionChecker.add();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMotionChecker.remove();
            }
        });

        mListView.startAnimation(transAnimation);
    }

    /*******************************************************************************
     * inner class and interface
     *******************************************************************************/
    /**
     * interface for suppling methods which are possible to handle
     * CrossListView's function from other class
     *
     * @author lordvader
     */
    public static interface Callback {
        public void changeListContentsByIndex(int index, boolean isRefreshTab);
    }
}
