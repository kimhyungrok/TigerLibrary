package com.empire.tigerlibrary.view.tile;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.empire.tigerlibrary.animation.CustomAnimationListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * tile grid view which is possible to move tile in blank area or replace
 * another tile
 *
 * @author lordvader
 */
public class TileGridView extends ViewGroup {
    /**
     * tile type
     */
    public static final int POS_FULL = 0;
    public static final int POS_HALF_LEFT_WITH = 1;
    public static final int POS_HALF_RIGHT_WITH = 2;
    public static final int POS_HALF_LEFT_ONLY = 3;
    public static final int POS_HALF_RIGHT_ONLY = 4;
    /**
     * switch flag
     */
    protected final boolean SHOW_CUSTOM_LOG = false;
    /**
     * tile swap type
     */
    private final int SWAP_TYPE_IMPOSSIBLE = -1;
    private final int SWAP_TYPE_HALF_ONLY = 0;
    private final int SWAP_TYPE_WITH_FULL = 1;
    private final int SWAP_TYPE_PUSH_HALF = 2;
    private final int SWAP_TYPE_PUSH_FULL = 3;
    /**
     * fill blank type when half type tile is moved in blank area
     */
    private final int FILL_BLANK_TO_LAST_ROW = 0;
    private final int FILL_BLANK_FROM_LAST_ROW = 1;
    private final int FILL_BLANK_TO_FIRST_ROW = 2;
    private final int FILL_BLANK_FROM_FIRST_ROW = 3;
    private final int FILL_BLANK_INSIDE = 4;
    private final int FILL_BLANK_DRAG_UPPER = 5;
    /**
     * refresh (index, tag) type after filling blank
     */
    private final int FILL_BLANK_REFRESH_TYPE1 = 0;
    private final int FILL_BLANK_REFRESH_TYPE2 = 1;
    private final int ALLOW_UNCONDITIONAL_MEASURING_COUNT = 7;
    private int mPreviousChildBottom;
    private int mChildLeft;
    private int mChildTop;
    private int mChildRight;
    private int mChildBottom;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mTopBoundary = -1;
    private int mPreviousFooterTop;
    /**
     * about swap child
     */
    private int mDraggedViewIndex;
    private int mTargetViewIndex;
    private int mTargetSiblingViewIndex;
    private int mDraggedViewTag;
    private int mTargetViewTag;
    private int mTargetSiblingViewTag;
    /**
     * footer view
     */
    private View mFooterView;
    private TileGridAdapter mAdapter;
    private TileViewListener mTileViewListener;
    private boolean mIsReDrawChildView = true;
    private HashMap<String, View> mViewTagMapper = new HashMap<String, View>();
    private boolean mIsSkipDrawFooterView;
    private boolean mIsDrawFooterViewFirstTime = true;
    private boolean mIsChildMeasured;
    private int mTotalChildViewHeight;
    private boolean mIsMeasureUnconditionally;
    private int mUnconditionalMeasuringCount;
    private ConcurrentLinkedQueue<Boolean> mActiveAnimationQueue = new ConcurrentLinkedQueue<Boolean>();

    public TileGridView(Context context) {
        super(context);
        initialize();
    }

    public TileGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public TileGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * initialize default setting
     */
    private void initialize() {
        // set screen width
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mScreenWidth = display.getWidth();
        mScreenHeight = display.getHeight();
    }

    /**
     * set tile view listener for manipulating container
     *
     * @param tileViewListener
     */
    public void setTileViewListener(TileViewListener tileViewListener) {
        mTileViewListener = tileViewListener;
    }

    /**
     * get TileGridAdapter adapter
     *
     * @return
     */
    public TileGridAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * set adapter which is composed by tile view list
     *
     * @param adapter
     */
    public void setAdapter(TileGridAdapter adapter) {
        mAdapter = adapter;
        addChildView();
    }

    /*******************************************************************************
     * draw view by measure, layout
     *******************************************************************************/
    @Override
    public void addView(View child) {
        Object tag = child.getTag();

        if (tag != null && !"".equals(tag)) {
            mViewTagMapper.put(tag.toString(), child);
        }

        super.addView(child);
    }

    @Override
    public void removeAllViews() {
        // TODO Auto-generated method stub
        super.removeAllViews();
        mViewTagMapper.clear();
    }

    @Override
    public void removeView(View view) {
        if (mViewTagMapper.containsValue(view)) {
            Object tag = view.getTag();

            if (tag != null && !"".equals(tag)) {
                mViewTagMapper.remove(tag);
            }
        }
        super.removeView(view);
    }

    /**
     * get item by pre-set tag
     *
     * @param tag
     * @return
     */
    public View getItemByTag(String tag) {
        return mViewTagMapper.get(tag);
    }

    /**
     * set whether measure and layout child unconditionally
     *
     * @param isMeasureUnconditionally
     */
    public void setMeasureUnconditionally(boolean isMeasureUnconditionally) {
        mIsMeasureUnconditionally = isMeasureUnconditionally;
        mUnconditionalMeasuringCount = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LOG(String.format("TileGridView - onMeasure() mTestIsChildMeasured / mTestTotalChildViewHeight / mIsReDrawChildView = %s/%s/%s", mIsChildMeasured, mTotalChildViewHeight, mIsReDrawChildView));

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int totalChildViewHeight = 0;
        int childCount = (mAdapter != null) ? mAdapter.getCount() : 0;

        if (!mIsChildMeasured) {
            if (mIsReDrawChildView && mIsMeasureUnconditionally) {
                mUnconditionalMeasuringCount++;

                if (mUnconditionalMeasuringCount > ALLOW_UNCONDITIONAL_MEASURING_COUNT) {
                    mIsMeasureUnconditionally = false;
                }
            }

            for (int i = 0; i < childCount; i++) {
                View childView = mAdapter.getView(i);

                int childViewTag = mAdapter.getTileType(childView);
                int childViewWidth = (childViewTag == POS_FULL) ? width : width / 2;
                int tmpChildViewHeight = childView.getHeight();
                int childViewHeight = (childView.getHeight() == 0) ? childView.getLayoutParams().height : tmpChildViewHeight;

                // reduce measuring child view count. because it occur
                // performance issue
                if (!mIsReDrawChildView || (mIsReDrawChildView && childView.isDirty()) || mIsMeasureUnconditionally) {
                    LOG(String.format("TileGridView - childView.measure() cardId = %s, redraw = %s, dirty = %s", childView.getTag(),
							mIsReDrawChildView, childView.isDirty()));

                    childViewWidth = childViewWidth - mTileViewListener.getTileSpacing() * 2;
                    childView.measure(MeasureSpec.makeMeasureSpec(childViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec
							(childViewHeight, MeasureSpec.EXACTLY));
                }

                if (childViewTag != POS_HALF_RIGHT_WITH) {
                    totalChildViewHeight += (childView.getMeasuredHeight() + mTileViewListener.getTileSpacing() * 2);
                }
            }
        }

        // consider footer view height when exist
        if (mFooterView != null) {
            mFooterView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mFooterView.getLayoutParams()
					.height, MeasureSpec.AT_MOST));

            totalChildViewHeight += mFooterView.getMeasuredHeight();
        }

        if (!mIsChildMeasured) {
            mTotalChildViewHeight = totalChildViewHeight;
            mIsChildMeasured = true;
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mTotalChildViewHeight);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LOG("TileGridView - onLayout() = " + changed);

        if (!mIsReDrawChildView) {
            mPreviousChildBottom = 0;
            mChildBottom = 0;

            int childCount = (mAdapter != null) ? mAdapter.getCount() : 0;

            for (int i = 0; i < childCount; i++) {
                View childView = mAdapter.getView(i);
                layoutChildView(childView);
            }
        } else {
            reDrawChildView();
        }

        // layout footer view
        drawFooterView();

        // set parent top boundary
        if (mTopBoundary == -1) {
            int[] parentLocationInScreen = new int[2];
            getLocationOnScreen(parentLocationInScreen);
            mTopBoundary = parentLocationInScreen[1];
        }

        mIsReDrawChildView = true;
        mIsChildMeasured = false;
    }

    /**
     * draw footer view
     */
    private void drawFooterView() {
        if (mFooterView != null && !mIsSkipDrawFooterView) {
            int rawTop = 0;

            if (mAdapter != null && mAdapter.getCount() > 0) {
                View lastChildView = mAdapter.getView(mAdapter.getCount() - 1);
                rawTop = lastChildView.getBottom() + mTileViewListener.getTileSpacing();
            }

            int footerViewLeft = 0;
            int footerViewTop = rawTop;
            int footerViewRight = this.getMeasuredWidth();
            int footerViewBottom = footerViewTop + mFooterView.getMeasuredHeight();
            mFooterView.layout(footerViewLeft, footerViewTop, footerViewRight, footerViewBottom);

            // for move down motion when child view is added dynamically
            if (!mIsReDrawChildView && !mIsDrawFooterViewFirstTime) {
                int differHeight = mFooterView.getTop() - mPreviousFooterTop;
                TranslateAnimation transAnimation = new TranslateAnimation(0, 0, -differHeight, 0);
                transAnimation.setDuration(200);
                mFooterView.startAnimation(transAnimation);
            }
            mIsDrawFooterViewFirstTime = false;
            mPreviousFooterTop = mFooterView.getTop();
        }
    }

    /**
     * redraw child view maintain current measure and layout
     */
    private void reDrawChildView() {
        if (mAdapter != null) {
            int childCount = mAdapter.getCount();

            for (int i = 0; i < childCount; i++) {
                View childView = mAdapter.getView(i);

                if ((childView.isDirty()) || mIsMeasureUnconditionally) {
                    LOG(String.format("TileGridView - reDrawChildView() cardId = %s, isDirty = %s", childView.getTag(), childView.isDirty()));
                    childView.layout(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
                }
            }
        }
    }

    /**
     * calculate child view layout and draw child view according to tile type
     *
     * @param childView
     */
    private void layoutChildView(View childView) {
        int childViewTag = mAdapter.getTileType(childView);
        int tileSpacing = mTileViewListener.getTileSpacing();

        // set previous bottom for calculate next view bottom
        if (childViewTag != POS_HALF_RIGHT_WITH) {
            mPreviousChildBottom = mChildBottom;
            mPreviousChildBottom += tileSpacing;
        }

        mChildLeft = 0;
        mChildTop = mPreviousChildBottom;
        mChildRight = this.getMeasuredWidth();
        mChildBottom = mPreviousChildBottom + childView.getMeasuredHeight();

        if (childViewTag == POS_HALF_RIGHT_WITH || childViewTag == POS_HALF_RIGHT_ONLY) {
            mChildLeft = this.getMeasuredWidth() / 2;
        }

        if (childViewTag == POS_HALF_LEFT_WITH || childViewTag == POS_HALF_LEFT_ONLY) {
            mChildRight = this.getMeasuredWidth() / 2;
        }

        rawLayoutChild(childView, mChildLeft, mChildTop, mChildRight, mChildBottom, false);
        mChildBottom += tileSpacing;
    }

    /**
     * draw child view after applying container scroll and padding
     *
     * @param childView
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void rawLayoutChild(View childView, int left, int top, int right, int bottom, boolean isApplyScroll) {
        LOG(String.format("TileGridView - rawLayoutChild() l,t,r,b = %s,%s,%s,%s", left, top, right, bottom));

        int scrollY = isApplyScroll ? mTileViewListener.getScrollY() : 0;
        int tileSpacing = mTileViewListener.getTileSpacing();

        int drawLeft = left + tileSpacing;
        int drawTop = top + scrollY;
        int drawRight = right - tileSpacing;
        int drawBottom = bottom + scrollY;

        childView.layout(drawLeft, drawTop, drawRight, drawBottom);
    }

    /**
     * add child view when adapter is set or reload tile view
     */
    public void addChildView() {
        addChildView(false);
    }

    /**
     * add child view when adapter is set or reload tile view
     */
    public void addChildView(boolean isReDrawChildView) {
        mIsReDrawChildView = isReDrawChildView;

        if (mAdapter != null) {
            int childViewCount = mAdapter.getCount();

            for (int i = 0; i < childViewCount; i++) {
                View childView = mAdapter.getView(i);

                // set long click event
                if (mTileViewListener.isEnableTileLongClick()) {
                    childView.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View v) {
                            Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(25);

                            // disable container scrolling
                            mTileViewListener.setEnableScrolling(false);
                            v.setOnTouchListener(new ChildViewOnTouchListener());

                            return false;
                        }
                    });
                }

                removeView(childView);

                LOG("TileGridView - addChildView() : addView = " + i);
                addView(childView);

                // show fade in effect when new child is added
                if (!mIsReDrawChildView && i == (childViewCount - 1)) {
                    showFadeIn(childView);
                }
            }
        }

        // add footer
        if (mFooterView != null) {
            removeView(mFooterView);
            addView(mFooterView);
        }
    }

    /**
     * add footer view
     *
     * @param view
     */
    public void addFooterView(View view) {
        mFooterView = view;
    }

    /**
     * check whether footer view exist
     *
     * @return
     */
    public boolean isExistFooterView() {
        return mFooterView != null ? true : false;
    }

    /**
     * remove footer view
     */
    public void removeFooterView() {
        if (mFooterView != null) {
            removeView(mFooterView);
            mFooterView = null;
        }
    }

    /**
     * swap tile between same type basically. But tile type is half_left_only or
     * half_right_only, it is possible to swap with full type
     *
     * @param draggedViewIndex
     * @param targetViewIndex
     * @param draggedViewOriLeft
     * @param draggedViewOriTop
     * @return
     */
    private boolean swapView(int draggedViewIndex, int targetViewIndex, int draggedViewOriLeft, int draggedViewOriTop) {
        View draggedView = mAdapter.getView(draggedViewIndex);
        View targetView = mAdapter.getView(targetViewIndex);

        mDraggedViewIndex = draggedViewIndex;
        mTargetViewIndex = targetViewIndex;

        mDraggedViewTag = mAdapter.getTileType(draggedView);
        mTargetViewTag = mAdapter.getTileType(targetView);
        int swapType;

        if ((swapType = getSwapType(mDraggedViewTag, mTargetViewTag)) != SWAP_TYPE_IMPOSSIBLE) {
            switch (swapType) {
                case SWAP_TYPE_HALF_ONLY:
                case SWAP_TYPE_WITH_FULL: {
                    return swapTile(swapType, draggedView, targetView, draggedViewOriLeft, draggedViewOriTop);
                }
                case SWAP_TYPE_PUSH_FULL:
                case SWAP_TYPE_PUSH_HALF: {
                    return pushTile(swapType, draggedView, targetView, draggedViewOriLeft, draggedViewOriTop);
                }
                default: {
                    return false;
                }
            }
        }

        return false;
    }

    /*******************************************************************************
     * move tile (swap)
     *******************************************************************************/

    /**
     * swap each tile location when tiles are same type or full and half_only
     * type
     *
     * @param swapType
     * @param draggedView
     * @param targetView
     * @param draggedViewOriLeft
     * @param draggedViewOriTop
     * @return
     */
    private boolean swapTile(int swapType, View draggedView, View targetView, int draggedViewOriLeft, int draggedViewOriTop) {
        switch (swapType) {
            case SWAP_TYPE_HALF_ONLY: {
                moveTile(draggedView, targetView.getLeft(), targetView.getTop());
                moveTile(targetView, draggedViewOriLeft, draggedViewOriTop);

                mAdapter.setTileType(targetView, mDraggedViewTag);
                mAdapter.setTileType(draggedView, mTargetViewTag);

                break;
            }
            case SWAP_TYPE_WITH_FULL: {
                // replace between different height tile
                int draggedViewTop = targetView.getBottom() - draggedView.getHeight();
                int targetViewTop = draggedViewOriTop;
                int lowerBoundaryIndex = mDraggedViewIndex;
                int upperBoundaryIndex = mTargetViewIndex;
                int offsetLength = draggedView.getHeight() - targetView.getHeight();

                if (mDraggedViewIndex > mTargetViewIndex) {
                    draggedViewTop = targetView.getTop();
                    targetViewTop = draggedViewOriTop + draggedView.getHeight() - targetView.getHeight();
                    lowerBoundaryIndex = mTargetViewIndex;
                    upperBoundaryIndex = mDraggedViewIndex;
                    offsetLength = targetView.getHeight() - draggedView.getHeight();
                }

                moveTile(draggedView, draggedViewOriLeft, draggedViewTop);
                moveTile(targetView, targetView.getLeft(), targetViewTop);

                // move tiles which are inside by dragged and target tiles
                int childCount = mAdapter.getCount();
                for (int i = 0; i < childCount; i++) {
                    if (upperBoundaryIndex > i && i > lowerBoundaryIndex) {
                        View insideView = mAdapter.getView(i);
                        int toBeTop = insideView.getTop() - offsetLength;
                        moveTile(insideView, insideView.getLeft(), toBeTop);
                    }
                }

                break;
            }
        }

        // reorder tile
        int tmpTargetViewTag = mAdapter.getTileType(targetView);
        int tmpDraggedViewTag = mAdapter.getTileType(draggedView);

        mAdapter.setItem(mDraggedViewIndex, targetView, tmpTargetViewTag);
        mAdapter.setItem(mTargetViewIndex, draggedView, tmpDraggedViewTag);

        return true;
    }

    /**
     * push tile to downward when tiles are full and half_with type
     *
     * @param swapType
     * @param draggedView
     * @param targetView
     * @param draggedViewOriLeft
     * @param draggedViewOriTop
     * @return
     */
    private boolean pushTile(int swapType, View draggedView, View targetView, int draggedViewOriLeft, int draggedViewOriTop) {
        int targetViewIndex = mTargetViewIndex;

        if (swapType == SWAP_TYPE_PUSH_FULL) {
            // tag
            if (mDraggedViewTag == POS_HALF_LEFT_WITH) {
                mAdapter.setTileType(draggedView, POS_HALF_LEFT_ONLY);
                View draggedSiblingView = mAdapter.getView(mDraggedViewIndex + 1);
                mAdapter.setTileType(draggedSiblingView, POS_HALF_RIGHT_ONLY);
            } else if (mDraggedViewTag == POS_HALF_RIGHT_WITH) {
                mAdapter.setTileType(draggedView, POS_HALF_RIGHT_ONLY);
                View draggedSiblingView = mAdapter.getView(mDraggedViewIndex - 1);
                mAdapter.setTileType(draggedSiblingView, POS_HALF_LEFT_ONLY);
            }
        } else {
            if (mTargetViewTag == POS_HALF_RIGHT_WITH) {
                targetView = mAdapter.getView(targetViewIndex - 1);
                targetViewIndex = targetViewIndex - 1;
            }

            // when next view and next's next view is half left with and
            // half right width skip swapping
            if ((mTargetViewTag == POS_HALF_LEFT_WITH || mTargetViewTag == POS_HALF_RIGHT_WITH) && (targetViewIndex - mDraggedViewIndex == 1)) {
                return false;
            }
        }

        // move child view
        moveTile(draggedView, draggedViewOriLeft, targetView.getTop());
        int upperToBeIndex = splashTileInSwap(swapType, mDraggedViewIndex, targetViewIndex);

        reOrderIndex(mDraggedViewIndex, targetViewIndex, upperToBeIndex);

        if (swapType == SWAP_TYPE_PUSH_FULL) {
            reDrawParent(true);
        }

        return true;
    }

    /**
     * splash tile when running pushTile method
     *
     * @param swapType
     * @param draggedViewIndex
     * @param targetViewIndex
     * @return
     */
    private int splashTileInSwap(int swapType, int draggedViewIndex, int targetViewIndex) {
        View draggedView = mAdapter.getView(draggedViewIndex);
        int childViewCount = mAdapter.getCount();
        int upperToBeIndex = -1;
        int verticalSpacingOffset = mTileViewListener.getTileSpacing() * 2;

        for (int i = 0; i < childViewCount; i++) {
            // set upperToBeIndex;
            if (upperToBeIndex == -1 && i >= targetViewIndex && i != draggedViewIndex) {
                upperToBeIndex = i;
            }

            View pushedView = mAdapter.getView(i);

            switch (swapType) {
                case SWAP_TYPE_PUSH_FULL: {
                    if (i >= targetViewIndex && i != draggedViewIndex) {
                        moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() + verticalSpacingOffset) + draggedView.getMeasuredHeight());
                    }

                    break;
                }
                case SWAP_TYPE_PUSH_HALF: {
                    if (draggedViewIndex < targetViewIndex) {
                        if (i < targetViewIndex && i >= draggedViewIndex) {
                            moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() - verticalSpacingOffset) - draggedView
									.getMeasuredHeight());
                        } else {
                            moveTile(pushedView, pushedView.getLeft(), pushedView.getTop());
                        }
                    } else {
                        if (i >= targetViewIndex && i != draggedViewIndex) {
                            if (mAdapter.getItemIndex(pushedView) < draggedViewIndex) {
                                moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() + verticalSpacingOffset) + draggedView
										.getMeasuredHeight());
                            }
                        }
                    }
                    break;
                }
            }
        }

        return upperToBeIndex - 1;
    }

    /**
     * get swap type according by dragged and target tile tag
     *
     * @param draggedViewTag
     * @param targetViewTag
     * @return
     */
    private int getSwapType(int draggedViewTag, int targetViewTag) {
        List<Integer> crossSwapPossibleList = Arrays.asList(new Integer[]{POS_FULL,
                                                                          POS_HALF_LEFT_ONLY,
                                                                          POS_HALF_RIGHT_ONLY});
        List<Integer> notFitList = Arrays.asList(new Integer[]{POS_HALF_LEFT_WITH,
                                                               POS_HALF_RIGHT_WITH});

        if (draggedViewTag != POS_FULL && targetViewTag != POS_FULL) {
            return SWAP_TYPE_HALF_ONLY;
        } else if (draggedViewTag == POS_FULL && crossSwapPossibleList.contains(targetViewTag)) {
            return SWAP_TYPE_WITH_FULL;
        } else if (targetViewTag == POS_FULL && crossSwapPossibleList.contains(draggedViewTag)) {
            return SWAP_TYPE_WITH_FULL;
        } else if (draggedViewTag == POS_FULL && notFitList.contains(targetViewTag)) {
            return SWAP_TYPE_PUSH_HALF;
        } else if (targetViewTag == POS_FULL && notFitList.contains(draggedViewTag)) {
            return SWAP_TYPE_PUSH_FULL;
        } else {
            return SWAP_TYPE_IMPOSSIBLE;
        }
    }

    /**
     * move tile to Blank area when tile is half type
     *
     * @param draggedViewIndex
     * @param siblingViewIndex
     * @param draggedViewOriLeft
     * @param draggedViewOriTop
     * @return
     */
    private boolean fillBlankArea(int draggedViewIndex, int siblingViewIndex, int draggedViewOriLeft, int draggedViewOriTop) {
        final View draggedView = mAdapter.getView(draggedViewIndex);
        View targetSiblingView = mAdapter.getView(siblingViewIndex);

        mDraggedViewIndex = draggedViewIndex;
        mTargetSiblingViewIndex = siblingViewIndex;
        mDraggedViewTag = mAdapter.getTileType(draggedView);
        mTargetSiblingViewTag = mAdapter.getTileType(targetSiblingView);

        int left = targetSiblingView.getLeft();
        int top = targetSiblingView.getTop();

        // just fill dragged view's side blank area
        if (draggedViewIndex == siblingViewIndex) {
            left = draggedViewOriLeft;
            top = draggedViewOriTop;

            left = refreshIndexNTag(FILL_BLANK_REFRESH_TYPE1, draggedView, targetSiblingView, left);
            moveTile(draggedView, left, top);

            return true;
        } else { // fill target view's side blank area
            if (mDraggedViewTag == POS_HALF_RIGHT_WITH) {
                View draggedSiblingView = mAdapter.getView(draggedViewIndex - 1);
                mAdapter.setTileType(draggedSiblingView, POS_HALF_LEFT_ONLY);
            } else if (mDraggedViewTag == POS_HALF_LEFT_WITH) {
                View draggedSiblingView = mAdapter.getView(draggedViewIndex + 1);
                mAdapter.setTileType(draggedSiblingView, POS_HALF_RIGHT_ONLY);
            }

            left = refreshIndexNTag(FILL_BLANK_REFRESH_TYPE2, draggedView, targetSiblingView, left);

            // erase blank behind row
            if (mDraggedViewTag == POS_HALF_LEFT_ONLY || mDraggedViewTag == POS_HALF_RIGHT_ONLY) {
                return eraseRemainRow(draggedView, targetSiblingView, draggedViewIndex, left, top, draggedViewOriTop);
            } else {
                moveTile(draggedView, left, top);
                return true;
            }
        }
    }

    /*******************************************************************************
     * move tile (fill blank)
     *******************************************************************************/

    /**
     * reorder index and tag
     *
     * @param type
     * @param draggedView
     * @param targetSiblingView
     * @param left
     * @return
     */
    private int refreshIndexNTag(int type, View draggedView, View targetSiblingView, int left) {
        int tileSpacing = mTileViewListener.getTileSpacing();
        if (mTargetSiblingViewTag == POS_HALF_LEFT_ONLY) {
            left = left + targetSiblingView.getWidth() + tileSpacing * 2;

            if (type == FILL_BLANK_REFRESH_TYPE1) {
                mAdapter.setTileType(draggedView, POS_HALF_RIGHT_ONLY);
            } else {
                mAdapter.setTileType(targetSiblingView, POS_HALF_LEFT_WITH);
                mAdapter.setTileType(draggedView, POS_HALF_RIGHT_WITH);

                if ((mTargetSiblingViewIndex + 1) != mDraggedViewIndex) {
                    reOrderIndex(mDraggedViewIndex, mTargetSiblingViewIndex + 1, mTargetSiblingViewIndex);
                }
            }
        } else {
            left = left - targetSiblingView.getWidth() - (tileSpacing * 2);
            if (type == FILL_BLANK_REFRESH_TYPE1) {
                mAdapter.setTileType(draggedView, POS_HALF_LEFT_ONLY);
            } else {
                mAdapter.setTileType(targetSiblingView, POS_HALF_RIGHT_WITH);
                mAdapter.setTileType(draggedView, POS_HALF_LEFT_WITH);

                if ((mTargetSiblingViewIndex - 1) != mDraggedViewIndex) {
                    reOrderIndex(mDraggedViewIndex, mTargetSiblingViewIndex, mTargetSiblingViewIndex - 1);
                }
            }
        }

        return left;
    }

    /**
     * remove remain blank row when half tile's move is complete
     *
     * @param draggedView
     * @param targetSiblingView
     * @param beforeDraggedViewIndex
     * @param left
     * @param top
     * @param draggedViewOriTop
     * @return
     */
    private boolean eraseRemainRow(final View draggedView, View targetSiblingView, final int beforeDraggedViewIndex, int left, int top, final int
			draggedViewOriTop) {
        int moveType = -1;

        if (isAlignParentBottom(draggedView, top)) {
            moveType = FILL_BLANK_TO_LAST_ROW;
        } else if (isAlignParentBottom(draggedView, draggedViewOriTop)) {
            moveType = FILL_BLANK_FROM_LAST_ROW;
        } else if (getTop() == top) {
            moveType = FILL_BLANK_TO_FIRST_ROW;
        } else if (getTop() == draggedViewOriTop) {
            moveType = FILL_BLANK_FROM_FIRST_ROW;
        } else {
            moveType = FILL_BLANK_INSIDE;
        }

        switch (moveType) {
            case FILL_BLANK_TO_LAST_ROW: {
                moveTile(draggedView, left, top);
                rawSplashTileInFill(moveType, draggedView, beforeDraggedViewIndex, 0, reDrawParent(false));
                return true;
            }
            case FILL_BLANK_FROM_LAST_ROW: {
                moveTile(draggedView, left, top, reDrawParent(false));
                return true;
            }
            case FILL_BLANK_TO_FIRST_ROW: {
                splashTileInFill(moveType, draggedView, beforeDraggedViewIndex, 0);
                reDrawParent(true);
                moveTile(draggedView, left, top);

                return true;
            }
            case FILL_BLANK_FROM_FIRST_ROW: {
                moveTile(draggedView, left, top);
                splashTileInFill(moveType, draggedView, beforeDraggedViewIndex, 0);
                reDrawParent(true);
                return true;
            }
            case FILL_BLANK_INSIDE: {
                moveTile(draggedView, left, top);
                rawSplashTileInFill(moveType, draggedView, beforeDraggedViewIndex, (top - draggedViewOriTop), reDrawParent(false));
                return true;
            }
            default: {
                return false;
            }
        }
    }

    /**
     * splash tile when half tile is moved to blank area
     *
     * @param moveType
     * @param draggedView
     * @param beforeDraggedViewIndex
     * @param extraValue
     */
    private void rawSplashTileInFill(int moveType, View draggedView, int beforeDraggedViewIndex, int extraValue, TileGridViewListener
			animationListener) {
        int childViewCount = mAdapter.getCount();
        int verticalSpacingOffset = mTileViewListener.getTileSpacing() * 2;

        for (int i = 0; i < childViewCount; i++) {
            View pushedView = mAdapter.getView(i);

            switch (moveType) {
                case FILL_BLANK_TO_LAST_ROW: {
                    if (beforeDraggedViewIndex <= i) {
                        moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() - verticalSpacingOffset) - draggedView.getMeasuredHeight(),
								(i == (childViewCount - 1) ? animationListener : null), true);
                    }
                    break;
                }
                case FILL_BLANK_TO_FIRST_ROW: {
                    if (beforeDraggedViewIndex < i) {
                        moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() - verticalSpacingOffset) - draggedView.getMeasuredHeight());
                    }
                    break;
                }
                case FILL_BLANK_FROM_FIRST_ROW: {
                    moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() - verticalSpacingOffset) - draggedView.getMeasuredHeight());
                    break;
                }
                case FILL_BLANK_INSIDE: {
                    if (beforeDraggedViewIndex == i && (extraValue > 0) || beforeDraggedViewIndex < i) {
                        moveTile(pushedView, pushedView.getLeft(), (pushedView.getTop() - verticalSpacingOffset) - draggedView.getMeasuredHeight(),
								(i == (childViewCount - 1) ? animationListener : null));
                    }
                    break;
                }
                case FILL_BLANK_DRAG_UPPER: {
                    if (i < beforeDraggedViewIndex) {
                        float currentX = pushedView.getX();
                        float currentY = pushedView.getY();

                        showMoveTileEffect(pushedView, (currentX - 0), (currentY - extraValue), null);
                    }
                    break;
                }
            }
        }
    }

    /**
     * splash tile when half tile is moved to blank area (not handle
     * TileGridViewListener)
     *
     * @param moveType
     * @param draggedView
     * @param beforeDraggedViewIndex
     * @param extraValue
     */
    private void splashTileInFill(int moveType, View draggedView, int beforeDraggedViewIndex, int extraValue) {
        rawSplashTileInFill(moveType, draggedView, beforeDraggedViewIndex, extraValue, null);
    }

    /**
     * restore scroll when footer view exist
     */
    private void restoreScrollWithFooter() {
        if (mFooterView != null) {
            int[] locationInScreen = new int[2];
            mFooterView.getLocationOnScreen(locationInScreen);
            int footerViewBottom = locationInScreen[1] + mFooterView.getHeight() + mTileViewListener.getPaddingBottom();

            if (footerViewBottom <= mScreenHeight) {
                int containerViewHeight = ((View) getParentView()).getHeight();
                int previousScrollY = mTileViewListener.getScrollY();

                if (getHeight() <= containerViewHeight) {
                    mTileViewListener.setScrollY(-previousScrollY);
                    showScrollDown(-previousScrollY);
                } else {
                    int differFromScreenHeight = mScreenHeight - footerViewBottom;
                    mTileViewListener.setScrollY(mTileViewListener.getScrollY() - differFromScreenHeight);
                    showScrollDown(-differFromScreenHeight);
                }
            }
        }
    }

    /*******************************************************************************
     * about footer view
     *******************************************************************************/

    /**
     * move footer view considering tile position and layout (include
     * TileGridViewListener)
     */
    private void moveFooterView(TileGridViewListener listener) {
        if (mFooterView != null) {
            int top = 0;

            if (mAdapter != null && mAdapter.getCount() > 0) {
                View lastChildView = mAdapter.getView(mAdapter.getCount() - 1);
                top = lastChildView.getBottom() + mTileViewListener.getTileSpacing();
            }

            moveTile(mFooterView, mFooterView.getLeft(), top, listener);
        }
    }

    /**
     * move footer view considering tile position and layout
     */
    private void moveFooterView() {
        moveFooterView(null);
    }

    /**
     * search target tile when touch move event on screen
     *
     * @param draggedViewIndex
     * @param rawX
     * @param rawY
     * @param oriTopOnScreen
     * @return
     */
    private int[] searchTargetChildView(int draggedViewIndex, int rawX, int rawY, int oriTopOnScreen) {
        int[] returnIndex = {-1,
                             -1};

        if (mAdapter != null) {
            int childViewCount = mAdapter.getCount();

            for (int i = 0; i < childViewCount; i++) {
                View childView = mAdapter.getView(i);
                int[] locationInScreen = new int[2];
                childView.getLocationOnScreen(locationInScreen);

                int left = locationInScreen[0];
                int top = locationInScreen[1];
                int right = left + childView.getMeasuredWidth() + mTileViewListener.getTileSpacing() * 2;
                int bottom = top + childView.getMeasuredHeight();

                if ((i != draggedViewIndex) && (left < rawX && rawX < right && top < rawY && rawY < bottom)) {
                    returnIndex[0] = i;
                    break;
                }

                int childViewTag = mAdapter.getTileType(childView);

                // scan single target view sibling blank area
                if (isOnlyHalfType(childViewTag, draggedViewIndex)) {
                    if ((i != draggedViewIndex) && isInChangableBlankArea(childViewTag, rawX, rawY, top, bottom)) {
                        returnIndex[1] = i;
                        break;
                    }

                    if ((i == draggedViewIndex) && isInChangableBlankArea(childViewTag, rawX, rawY, oriTopOnScreen, oriTopOnScreen + childView
							.getMeasuredHeight())) {
                        returnIndex[1] = draggedViewIndex;
                        break;
                    }
                }
            }
        }

        return returnIndex;
    }

    /*******************************************************************************
     * scanning screen for moving tile
     *******************************************************************************/

    /**
     * check whether tile is half type
     *
     * @param childViewTag
     * @param draggedViewIndex
     * @return
     */
    private boolean isOnlyHalfType(int childViewTag, int draggedViewIndex) {
        int draggedViewTag = mAdapter.getTileType(mAdapter.getView(draggedViewIndex));

        if (draggedViewTag == POS_FULL) {
            return false;
        }

        if (childViewTag == POS_HALF_LEFT_ONLY || childViewTag == POS_HALF_RIGHT_ONLY) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check whether the area in touch is possible to move tile of half type
     *
     * @param viewTag
     * @param rawX
     * @param rawY
     * @param top
     * @param bottom
     * @return
     */
    private boolean isInChangableBlankArea(int viewTag, int rawX, int rawY, int top, int bottom) {
        int blankLeft = 0;
        int blankTop = top;
        int blankRight = mScreenWidth / 2;
        int blankBottom = bottom;

        if (viewTag == POS_HALF_LEFT_ONLY) {
            blankLeft = mScreenWidth / 2;
            blankTop = top;
            blankRight = mScreenWidth;
            blankBottom = bottom;
        }

        // scan right blank area
        if ((blankLeft < rawX && rawX < blankRight && blankTop < rawY && rawY < blankBottom)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check whether such child view is align to parent bottom
     *
     * @param childView
     * @param top
     * @return
     */
    private boolean isAlignParentBottom(View childView, int top) {
        int childViewBottom = top + childView.getHeight() + mTileViewListener.getTileSpacing() + this.getPaddingBottom() + mTileViewListener
				.getPaddingBottom();
        int parentBottom = getBottom() - (mFooterView != null ? mFooterView.getHeight() : 0);

        if (childViewBottom == parentBottom) {
            return true;
        } else {
            return false;
        }
    }

    /*******************************************************************************
     * common
     *******************************************************************************/

    /**
     * check whether last is location on screen
     *
     * @return
     */
    private boolean isLastChildOnScreen() {
        if (mAdapter.getCount() < 1) {
            return false;
        }

        View lastView = mAdapter.getView(mAdapter.getCount() - 1);
        int[] locationInScreen = new int[2];
        lastView.getLocationOnScreen(locationInScreen);
        int lastViewBottom = locationInScreen[1] + lastView.getHeight() + mTileViewListener.getTileSpacing() + mTileViewListener.getPaddingBottom();

        if (mFooterView != null) {
            int[] footerViewlocationInScreen = new int[2];
            mFooterView.getLocationOnScreen(footerViewlocationInScreen);
            lastViewBottom = footerViewlocationInScreen[1];
        }

        return (lastViewBottom < mScreenHeight) ? true : false;
    }

    /**
     * apply scale up/down effect when dragged tile is located on target tile
     *
     * @param previousIndex
     * @param currentIndex
     * @return
     */
    private int applyTargetViewEffect(int previousIndex, int currentIndex) {
        if (previousIndex != currentIndex) {
            // remove effect on previous target view
            if (previousIndex != -1) {
                View previousTargetView = mAdapter.getView(previousIndex);
                showScaleEffect(previousTargetView, true);
            }

            // apply effect on new target view
            if (currentIndex != -1) {
                View targetView = mAdapter.getView(currentIndex);
                showScaleEffect(targetView, false);
            }
            previousIndex = currentIndex;
        }

        return previousIndex;
    }

    /**
     * move single tile to target area
     *
     * @param view
     * @param targetLeft
     * @param targetTop
     * @param listener
     * @param isEffectOn
     */
    private void rawMoveTile(View view, int targetLeft, int targetTop, TileGridViewListener listener, boolean isEffectOn) {
        float currentX = view.getX();
        float currentY = view.getY();

        view.layout(targetLeft, targetTop, targetLeft + view.getWidth(), targetTop + view.getHeight());
        if (isEffectOn) {
            showMoveTileEffect(view, (currentX - targetLeft), (currentY - targetTop), listener);
        }
    }

    /**
     * move single tile to target area (apply move effect basically)
     *
     * @param view
     * @param targetLeft
     * @param targetTop
     */
    private void moveTile(View view, int targetLeft, int targetTop) {
        rawMoveTile(view, targetLeft, targetTop, null, true);
    }

    /**
     * move single tile to target area (include TileGridViewListener)
     *
     * @param view
     * @param targetLeft
     * @param targetTop
     * @param listener
     */
    private void moveTile(View view, int targetLeft, int targetTop, TileGridViewListener listener) {
        rawMoveTile(view, targetLeft, targetTop, listener, true);
    }

    /**
     * move single tile to target area(decide to apply move effect and include
     * TileGridViewListener)
     *
     * @param view
     * @param targetLeft
     * @param targetTop
     * @param isEffectOn
     */
    private void moveTile(View view, int targetLeft, int targetTop, TileGridViewListener listener, boolean isEffectOn) {
        rawMoveTile(view, targetLeft, targetTop, listener, isEffectOn);
    }

    /**
     * get total child view height
     *
     * @return
     */
    private int getTotalChildViewHeight() {
        int childCount = mAdapter.getCount();
        int totalChildViewHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = mAdapter.getView(i);
            int childViewTag = mAdapter.getTileType(mAdapter.getView(i));

            if (childViewTag != POS_HALF_RIGHT_WITH) {
                totalChildViewHeight += (childView.getMeasuredHeight() + mTileViewListener.getTileSpacing() * 2);
            }
        }

        return totalChildViewHeight;
    }

    /**
     * reorder child view index
     *
     * @param draggedViewIndex
     * @param targetViewIndex
     * @param upperToBeIndex
     */
    private void reOrderIndex(int draggedViewIndex, int targetViewIndex, int upperToBeIndex) {
        View draggedView = mAdapter.getView(draggedViewIndex);
        int draggedViewTag = mAdapter.getTileType(draggedView);
        int toBeIndex;

        if (draggedViewIndex < targetViewIndex) { // to upper
            toBeIndex = upperToBeIndex;
            int newIndex = 0;

            // reorder front view
            for (int i = 0; i <= toBeIndex; i++) {
                if (draggedViewIndex != i) {
                    View previousView = mAdapter.getView(i);
                    int previousViewTag = mAdapter.getTileType(previousView);
                    mAdapter.setItem(newIndex++, previousView, previousViewTag);
                }
            }
        } else { // to bottom
            toBeIndex = targetViewIndex;
            int childCount = mAdapter.getCount();
            View previousView = mAdapter.getView(targetViewIndex);
            int previousViewTag = mAdapter.getTileType(previousView);

            // reorder rear view
            for (int i = targetViewIndex + 1; i < childCount; i++) {
                int tmpPreviousViewTag = mAdapter.getTileType(mAdapter.getView(i));
                View fleeView = mAdapter.setItem(i, previousView, previousViewTag);
                previousView = fleeView;
                previousViewTag = tmpPreviousViewTag;

                if (i == draggedViewIndex) {
                    break;
                }
            }

        }
        mAdapter.setItem(toBeIndex, draggedView, draggedViewTag);
    }

    /**
     * restore scroll when footer is not exist
     */
    private void restoreScroll() {
        int containerViewHeight = ((View) getParentView()).getHeight();
        int previousScrollY = mTileViewListener.getScrollY();

        if (getHeight() <= containerViewHeight) {
            mTileViewListener.setScrollY(-previousScrollY);
            showScrollDown(-previousScrollY);
        } else {
            View lastChildView = mAdapter.getView(mAdapter.getCount() - 1);
            int[] locationInScreen = new int[2];
            lastChildView.getLocationOnScreen(locationInScreen);
            int lastChildeViewBottom = locationInScreen[1] + lastChildView.getHeight() + mTileViewListener.getTileSpacing() + mTileViewListener
					.getPaddingBottom();
            int differFromScreenHeight = mScreenHeight - lastChildeViewBottom;

            mTileViewListener.setScrollY(previousScrollY - differFromScreenHeight);
            showScrollDown(-differFromScreenHeight);
        }
    }

    /**
     * redraw parent and adjust scroll after drawing completed. If param is
     * true, it is run redraw immediately, or just return listener for redrawing
     *
     * @param isRunImmediately
     * @return
     */
    private TileGridViewListener reDrawParent(boolean isRunImmediately) {
        return reDrawParent(isRunImmediately, null);
    }

    /**
     * redraw parent and adjust scroll after drawing completed. If param is
     * true, it is run redraw immediately, or just return listener for redrawing
     * (include TileGridViewListener listener)
     *
     * @param isRunImmediately
     * @param endListener
     * @return
     */
    private TileGridViewListener reDrawParent(boolean isRunImmediately, final TileGridViewListener endListener) {
        final TileGridViewListener drawParentlistener = new TileGridViewListener() {
            @Override
            public void doAction() {
                measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getTotalChildViewHeight(),
						MeasureSpec.AT_MOST));

                layout(getLeft(), getTop(), getLeft() + getMeasuredWidth(), getTop() + getMeasuredHeight());

                // restore scroll when footer view exist
                if (mFooterView != null) {
                    restoreScrollWithFooter();
                } else if (isLastChildOnScreen()) {
                    restoreScroll();
                }

                // run end listener, if exist
                if (endListener != null) {
                    endListener.doAction();
                }
            }
        };

        TileGridViewListener listener = new TileGridViewListener() {
            @Override
            public void doAction() {
                if (mFooterView != null) {
                    int containerViewHeight = ((View) getParentView()).getHeight();

                    // if parent height is smaller than screen height, run
                    // moving
                    // motion and redraw at the same time.
                    // but parent height is bigger than screen height, run
                    // moving motion firstly, case smooth animation effect
                    int currentParentHeight = getHeight();
                    int afterParentHeight = getTotalChildViewHeight() + mFooterView.getHeight();

                    if (currentParentHeight <= containerViewHeight) {
                        if (currentParentHeight > afterParentHeight) {
                            moveFooterView(drawParentlistener);
                        } else {
                            moveFooterView();
                            drawParentlistener.doAction();
                        }
                    } else {
                        moveFooterView(drawParentlistener);
                    }
                } else {
                    drawParentlistener.doAction();
                }
            }
        };

        if (isRunImmediately) {
            listener.doAction();
        }

        return listener;
    }

    /**
     * get index of such view
     *
     * @param view
     * @return
     */
    public int getIndex(View view) {
        return mAdapter.getItemIndex(view);
    }

    /*******************************************************************************
     * external public method
     *******************************************************************************/

    /**
     * add tile to first row
     *
     * @param view
     * @param finalListener
     */
    public void addTileToFirst(View view, final CustomAnimationListener finalListener) {
        int moveHeight = view.getLayoutParams().height;

        // add new item and reOrdering
        mAdapter.add(view, POS_FULL);

        int childCount = mAdapter.getCount();

        for (int i = childCount - 1; i >= 0; i--) {
            if (i == (childCount - 1)) {
                continue;
            }
            View childView = mAdapter.getView(i);
            mAdapter.setItem(i + 1, childView, mAdapter.getTileType(childView));

        }
        mAdapter.setItem(0, view, POS_FULL);
        addChildView();

        // splash move
        for (int i = 1; i < childCount; i++) {
            View childView = mAdapter.getView(i);
            moveTile(childView, childView.getLeft(), childView.getTop() + moveHeight);
        }

        showSlideIn(view);
    }

    /**
     * add tile to first row
     *
     * @param view
     * @param finalListener
     */
    public void addTileToLast(View view, final CustomAnimationListener finalListener) {
        // add new item and reOrdering
        mAdapter.add(view, POS_FULL);
        addChildView();
    }

    /**
     * add tile to first and last
     *
     * @param firstView
     * @param lastView
     * @param replaceViewMap
     */
    public void addTileFirstAndLast(View firstView, View lastView, SparseArray<View> replaceViewMap) {
        // replace tile firstly
        TileGridViewListener replaceEffectListener = replaceSameTiles(replaceViewMap);

        // show effect
        if (replaceEffectListener != null && firstView == null && lastView == null) {
            addChildView();
            replaceEffectListener.doAction();
            return;
        }

        // if first view not exist, add only last view
        if (firstView == null) {
            if (lastView != null) {
                mAdapter.add(lastView, POS_FULL);
                addChildView();
            }
            return;
        }

        // add new item and reOrdering
        if (lastView != null) {
            mAdapter.add(lastView, POS_FULL);
        }

        mAdapter.add(firstView, POS_FULL);
        int childCount = mAdapter.getCount();

        for (int i = childCount - 1; i >= 0; i--) {
            if (i == (childCount - 1)) {
                continue;
            }
            View childView = mAdapter.getView(i);
            mAdapter.setItem(i + 1, childView, mAdapter.getTileType(childView));

        }
        mAdapter.setItem(0, firstView, POS_FULL);
        addChildView();

        // show effect
        // splash move
        int moveHeight = firstView.getLayoutParams().height;
        for (int i = 1; i < childCount; i++) {
            View childView = mAdapter.getView(i);
            moveTile(childView, childView.getLeft(), childView.getTop() + moveHeight);
        }

        // show new tile slide in effect
        if (replaceEffectListener != null) {
            replaceEffectListener.doAction();
        }
        showSlideIn(firstView);
    }

    /**
     * replace previous tile to new tile which is same size and same type
     *
     * @param beforeView
     * @param afterView
     */
    public void replaceSameTile(View beforeView, View afterView) {
        int beforeTileIndex = mAdapter.getItemIndex(beforeView);

        if (beforeTileIndex > -1) {
            int beforeTileType = mAdapter.getTileType(beforeView);

            mAdapter.setItem(beforeTileIndex, afterView, beforeTileType);
            removeView(beforeView);
            addChildView();

            showSlideIn(afterView);
        }
    }

    private TileGridViewListener replaceSameTiles(final SparseArray<View> replaceViewMap) {
        TileGridViewListener replaceEffectListener = null;

        // if replaceViewMap not null, replace tile firstly
        if (replaceViewMap != null && replaceViewMap.size() > 0) {
            final int replaceViewMapSize = replaceViewMap.size();

            for (int i = 0; i < replaceViewMapSize; i++) {
                int beforeViewIndex = replaceViewMap.keyAt(i);
                View newView = replaceViewMap.get(beforeViewIndex);
                View beforeView = mAdapter.getView(beforeViewIndex);
                int beforeTileType = mAdapter.getTileType(beforeView);

                mAdapter.setItem(beforeViewIndex, newView, beforeTileType);
                removeView(beforeView);
            }

            // set effect
            replaceEffectListener = new TileGridViewListener() {
                @Override
                public void doAction() {
                    for (int i = 0; i < replaceViewMapSize; i++) {
                        View newView = replaceViewMap.get(replaceViewMap.keyAt(i));
                        showSlideIn(newView);
                    }
                }
            };
        }
        return replaceEffectListener;
    }

    /**
     * remove such tile
     *
     * @param view
     */
    public void removeTile(final View view, final CustomAnimationListener finalListener) {
        mTileViewListener.setTouchEventLock(true);

        final int beforeIndex = mAdapter.getItemIndex(view);
        final int lastIndex = mAdapter.getCount() - 1;

        View lastView = mAdapter.getView(lastIndex);

        final int oriTop = view.getTop();
        final int top = lastView.getBottom() + mTileViewListener.getTileSpacing() * 2;
        int tileType = mAdapter.getTileType(view);

        CustomAnimationListener animationEndListener = new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(final Animation animation) {
                mAdapter.removeItem(mAdapter.getItemIndex(view));
                TileGridViewListener endlistener = new TileGridViewListener() {
                    @Override
                    public void doAction() {
                        removeView(view);

                        // run caller's final listener
                        if (finalListener != null) {
                            finalListener.onAnimationEnd(animation);
                        }

                        mTileViewListener.setTouchEventLock(false);
                    }
                };
                reDrawParent(true, endlistener);
            }
        };

        if (tileType == TileGridView.POS_HALF_LEFT_WITH) {
            mAdapter.setTileType(view, TileGridView.POS_HALF_LEFT_ONLY);
            mAdapter.setTileType(mAdapter.getView(beforeIndex + 1), TileGridView.POS_HALF_RIGHT_ONLY);
        } else if (tileType == TileGridView.POS_HALF_RIGHT_WITH) {
            mAdapter.setTileType(view, TileGridView.POS_HALF_RIGHT_ONLY);
            mAdapter.setTileType(mAdapter.getView(beforeIndex - 1), TileGridView.POS_HALF_LEFT_ONLY);
        } else {
            animationEndListener = new CustomAnimationListener() {
                @Override
                public void onAnimationEnd(final Animation animation) {
                    mAdapter.removeItem(mAdapter.getItemIndex(view));
                    TileGridViewListener endlistener = new TileGridViewListener() {
                        @Override
                        public void doAction() {
                            removeView(view);

                            // run caller's final listener
                            if (finalListener != null) {
                                finalListener.onAnimationEnd(animation);
                            }

                            mTileViewListener.setTouchEventLock(false);
                        }
                    };

                    if (beforeIndex == lastIndex) {
                        reDrawParent(true, endlistener);
                    } else {
                        rawSplashTileInFill(FILL_BLANK_INSIDE, view, beforeIndex, (top - oriTop), reDrawParent(false, endlistener));
                    }
                }
            };
        }

        showDiminishView(view, animationEndListener);
        // showDisappearView(view, animationEndListener);
    }

    /**
     * move or remove such tile
     *
     * @param view
     */
    public void moveToFirst(final View view) {
        final int beforeIndex = mAdapter.getItemIndex(view);

        // if dragged view is first item, skip action
        if (beforeIndex == 0) {
            return;
        }

        int top = 0 + mTileViewListener.getTileSpacing();
        int tileType = mAdapter.getTileType(view);
        int swapType = SWAP_TYPE_PUSH_FULL;

        moveTile(view, view.getLeft(), top);

        if (tileType == TileGridView.POS_HALF_LEFT_WITH) {
            mAdapter.setTileType(view, TileGridView.POS_HALF_LEFT_ONLY);
            mAdapter.setTileType(mAdapter.getView(beforeIndex + 1), TileGridView.POS_HALF_RIGHT_ONLY);
        } else if (tileType == TileGridView.POS_HALF_RIGHT_WITH) {
            mAdapter.setTileType(view, TileGridView.POS_HALF_RIGHT_ONLY);
            mAdapter.setTileType(mAdapter.getView(beforeIndex - 1), TileGridView.POS_HALF_LEFT_ONLY);
        } else {
            swapType = SWAP_TYPE_PUSH_HALF;
        }

        splashTileInSwap(swapType, beforeIndex, 0);
        reOrderIndex(beforeIndex, 0, 0);
        reDrawParent(true);
    }

    /**
     * change tile type and size
     *
     * @param view
     * @param toTileType
     * @param toHeight
     * @return
     */
    public int changeTileType(View view, int toTileType, int toHeight, CustomAnimationListener animationListener) {
        mTileViewListener.setTouchEventLock(true);
        int resizeViewIndex = mAdapter.getItemIndex(view);
        int tileType = mAdapter.getTileType(view);
        int beforeWidth = view.getWidth();
        int beforeHeight = view.getHeight();
        int pivotX = 0;
        int resizeEffectStartOffset = 0;

        if (toTileType == POS_FULL) {
            int[] pivotXNOffset = changeTileTypeToFull(view, toHeight, tileType, pivotX, resizeViewIndex);
            pivotX = pivotXNOffset[0];
            resizeEffectStartOffset = pivotXNOffset[1];
        } else {
            changeTileTypeToHalf(view, toHeight, resizeViewIndex);
        }

        showResizeView(view, beforeWidth, beforeHeight, pivotX, resizeEffectStartOffset, animationListener, reDrawParent(false));

        return mAdapter.getTileType(view);
    }

    /**
     * change tile type to full
     *
     * @param view
     * @param tileType
     * @param pivotX
     * @param resizeViewIndex
     * @return
     */
    private int[] changeTileTypeToFull(View view, int toHeight, int tileType, int pivotX, int resizeViewIndex) {
        int resizeEffectStartOffset = 0;
        int left = view.getLeft();
        int top = view.getTop();
        int width = this.getWidth() - mTileViewListener.getTileSpacing() * 2;
        int heightDiffer = view.getHeight();
        int childCount = mAdapter.getCount();

        if (tileType == POS_HALF_RIGHT_ONLY || tileType == POS_HALF_RIGHT_WITH) {
            left = mTileViewListener.getTileSpacing();
            pivotX = view.getRight() - mTileViewListener.getTileSpacing();
        }

        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(toHeight, MeasureSpec.AT_MOST));
        view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
        heightDiffer = heightDiffer - view.getMeasuredHeight();
        mAdapter.setTileType(view, POS_FULL);

        // splash under tile as differ height
        if (tileType == POS_HALF_LEFT_WITH || tileType == POS_HALF_RIGHT_WITH) {
            int upperBoundaryIndex = resizeViewIndex;
            View siblingView;

            if (tileType == POS_HALF_LEFT_WITH) {
                siblingView = mAdapter.getView(resizeViewIndex + 1);
                mAdapter.setTileType(siblingView, POS_HALF_RIGHT_ONLY);
            } else {
                upperBoundaryIndex = resizeViewIndex - 1;
                siblingView = mAdapter.getView(resizeViewIndex - 1);
                int siblingViewIndex = mAdapter.getItemIndex(siblingView);

                mAdapter.setItem(resizeViewIndex, siblingView, POS_HALF_LEFT_ONLY);
                mAdapter.setItem(siblingViewIndex, view, POS_FULL);
            }

            // splash related tiles
            for (int i = upperBoundaryIndex + 1; i < childCount; i++) {
                View pushedView = mAdapter.getView(i);
                int pusehedViewTop = pushedView.getTop() + view.getMeasuredHeight() + mTileViewListener.getTileSpacing() * 2;
                moveTile(pushedView, pushedView.getLeft(), pusehedViewTop);
            }
            resizeEffectStartOffset = 200;
        } else {
            // splash related tiles
            for (int i = resizeViewIndex + 1; i < childCount; i++) {
                View pushedView = mAdapter.getView(i);
                int pusehedViewTop = pushedView.getTop() - heightDiffer;
                moveTile(pushedView, pushedView.getLeft(), pusehedViewTop);
            }
        }

        if (mFooterView != null) {
            reDrawParent(true);
        }

        return new int[]{pivotX,
                         resizeEffectStartOffset};
    }

    /**
     * change tile type to half
     *
     * @param view
     * @param toHeight
     * @param resizeViewIndex
     */
    private void changeTileTypeToHalf(View view, int toHeight, int resizeViewIndex) {
        // redraw child
        int width = this.getWidth() / 2 - mTileViewListener.getTileSpacing() * 2;
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(toHeight, MeasureSpec.AT_MOST));
        int differHeight = view.getMeasuredHeight() - view.getHeight();
        view.layout(view.getLeft(), view.getTop(), view.getLeft() + view.getMeasuredWidth(), view.getTop() + view.getMeasuredHeight());
        mAdapter.setTileType(view, POS_HALF_LEFT_ONLY);

        // splash under tile as differ height
        int childCount = mAdapter.getCount();

        for (int i = resizeViewIndex + 1; i < childCount; i++) {
            View pushedView = mAdapter.getView(i);
            moveTile(pushedView, pushedView.getLeft(), pushedView.getTop() + differHeight);
        }

        // when after size is large, redraw parent first
        if (differHeight > 0 || mFooterView != null) {
            reDrawParent(true);
        }
    }

    /**
     * get parent view
     *
     * @return
     */
    public View getParentView() {
        if (mTileViewListener.isShowExtraTopMargin()) {
            return (View) getParent().getParent();
        } else {
            return (View) super.getParent();
        }
    }

    /*******************************************************************************
     * set touch event on each tile
     *******************************************************************************/

    /**
     * show custom log
     *
     * @param logMessage
     */
    private void LOG(String logMessage) {
        if (SHOW_CUSTOM_LOG && logMessage != null && !"".equals(logMessage)) {
            Log.v("VV", logMessage);
        }
    }

    /**
     * show scale up/down effect
     *
     * @param view
     * @param isExpand
     */
    private void showScaleEffect(View view, boolean isExpand) {
        final float SCALE_FACTOR = 0.9f;
        final int DURATION = 150;
        float from;
        float to;

        if (isExpand) {
            from = SCALE_FACTOR;
            to = 1f;
        } else {
            from = 1f;
            to = SCALE_FACTOR;
        }

        ScaleAnimation scaleAnimation = new ScaleAnimation(from, to, from, to, view.getWidth() * 0.5f, view.getHeight() * 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(DURATION);

        view.clearAnimation();
        view.startAnimation(scaleAnimation);
    }

    /**
     * show move tile to such position effect
     *
     * @param view
     * @param currentX
     * @param currentY
     * @param listener
     */
    private void showMoveTileEffect(final View view, float currentX, float currentY, final TileGridViewListener listener) {
        final int DURATION = 300;

        TranslateAnimation transAnimation = new TranslateAnimation(currentX, 0, currentY, 0);
        transAnimation.setDuration(DURATION);
        transAnimation.setAnimationListener(new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(1f);
                mTileViewListener.setEnableScrolling(true);

                if (listener != null) {
                    listener.doAction();
                }
            }
        });

        view.clearAnimation();
        view.startAnimation(transAnimation);
    }

    /*******************************************************************************
     * effect
     *******************************************************************************/

    /**
     * show scroll down effect
     *
     * @param fromY
     */
    private void showScrollDown(int fromY) {
        final int DURATION = 300;

        TranslateAnimation scrollAnimation = new TranslateAnimation(0, 0, fromY, 0);
        scrollAnimation.setStartOffset(0);
        scrollAnimation.setDuration(DURATION);
        clearAnimation();
        startAnimation(scrollAnimation);
    }

    /**
     * show view disappeared effect
     *
     * @param view
     * @param animationEndListener
     */
    private void showDisappearView(View view, CustomAnimationListener animationEndListener) {
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1, 0);
        fadeOutAnimation.setDuration(400);
        fadeOutAnimation.setFillAfter(true);
        fadeOutAnimation.setAnimationListener(animationEndListener);
        view.startAnimation(fadeOutAnimation);
    }

    /**
     * show view diminished effect
     *
     * @param view
     * @param animationEndListener
     */
    private void showDiminishView(View view, CustomAnimationListener animationEndListener) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, view.getWidth() / 2, view.getHeight() / 2);
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(animationEndListener);
        view.startAnimation(scaleAnimation);
    }

    /**
     * show resizing tile effect
     *
     * @param view
     * @param beforeWidth
     * @param beforeHeight
     * @param pivotX
     */
    private void showResizeView(View view, float beforeWidth, float beforeHeight, float pivotX, int startOffSet, final CustomAnimationListener animationListener, final TileGridViewListener extraListener) {
        final int DURATION = 300;

        int currentWidth = view.getWidth();
        int currentHeight = view.getHeight();
        float scaleFactorX = (float) beforeWidth / (float) currentWidth;
        float scaleFactorY = (float) beforeHeight / (float) currentHeight;

        ScaleAnimation scaleAnimation = new ScaleAnimation(scaleFactorX, 1f, scaleFactorY, 1f, pivotX, 0);
        scaleAnimation.setStartOffset(startOffSet);
        scaleAnimation.setAnimationListener(new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (animationListener != null) {
                    animationListener.onAnimationEnd(animation);
                }
                if (extraListener != null) {
                    extraListener.doAction();
                }
                mTileViewListener.setTouchEventLock(false);
            }
        });

        scaleAnimation.setDuration(DURATION);
        view.startAnimation(scaleAnimation);
    }

    /**
     * show slide in from right to left effect
     *
     * @param view
     */
    private void showSlideIn(View view) {
        TranslateAnimation slideInAnimation = new TranslateAnimation(mScreenWidth, 0, 0, 0);
        slideInAnimation.setStartTime(100);
        slideInAnimation.setDuration(300);
        view.startAnimation(slideInAnimation);
    }

    /**
     * show fade in effect
     *
     * @param view
     */
    private void showFadeIn(View view) {
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(300);
        fadeInAnimation.setAnimationListener(new CustomAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mActiveAnimationQueue.add(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActiveAnimationQueue.poll();
            }
        });

        view.startAnimation(fadeInAnimation);
    }

    /**
     * check whether motion effect is activating
     *
     * @return
     */
    public boolean isMotionActive() {
        return mActiveAnimationQueue.size() > 0;
    }

    public interface TileGridViewListener {
        public void doAction();
    }

    /**
     * touch listener for each tile movement
     *
     * @author lordvader
     */
    class ChildViewOnTouchListener implements OnTouchListener {
        private final int SCROLL_ACTIVE_BOTTOM_BOUNDARY = mScreenHeight;
        private final int SCROLL_ACTIVE_TOP_BOUNDARY = mTopBoundary;

        private int mDraggedViewIndex;
        private int mPreviousTargetViewIndex = -1;
        private int[] mCurrentTargetViewIndex;

        private boolean mIsFirstDown;
        private boolean mIsSwap;

        private int mAdjustX;
        private int mAdjustY;

        private int mOriLeft;
        private int mOriTop;
        private int mOriTopOnScreen;

        @Override
        public boolean onTouch(final View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mIsFirstDown) {
                        mDraggedViewIndex = mAdapter.getItemIndex(view);
                        touchDown(view);
                        mIsFirstDown = true;
                        mIsSkipDrawFooterView = true;
                    }

                    touchMove(view, (int) event.getRawX(), (int) event.getRawY());

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    touchUp(view);
                    mIsFirstDown = false;
                    mIsSkipDrawFooterView = false;
                    break;
                }

            }
            return false;
        }

        /**
         * define action for first touch down event
         *
         * @param view
         */
        private void touchDown(View view) {
            view.bringToFront();
            view.setAlpha(0.5f);
            layout(getLeft(), getTop(), getLeft() + getMeasuredWidth(), getTop() + getMeasuredHeight());

            int[] locationInScreen = new int[2];
            view.getLocationOnScreen(locationInScreen);

            mAdjustX = Math.abs(locationInScreen[0] - (int) view.getX());
            mAdjustY = Math.abs(locationInScreen[1] - ((int) view.getY() - mTileViewListener.getScrollY()));

            mOriLeft = view.getLeft();
            mOriTop = view.getTop();
            mOriTopOnScreen = locationInScreen[1];
        }

        /**
         * define action for touch move event
         *
         * @param view
         * @param rawX
         * @param rawY
         */
        private void touchMove(final View view, int rawX, int rawY) {
            final int width = view.getMeasuredWidth() + mTileViewListener.getTileSpacing() * 2;
            final int height = view.getMeasuredHeight();
            final int left = rawX - (1 * width / 2) - mAdjustX;
            final int top = rawY - (1 * height / 2) - mAdjustY;

            TileGridViewListener gridViewListener = new TileGridViewListener() {
                @Override
                public void doAction() {
                    rawLayoutChild(view, left, top, left + width, top + height, true);
                }
            };

            gridViewListener.doAction();

            // active auto scrolling when dragged view is located on screen top
            // or bottom area
            int[] locationInScreen = new int[2];
            view.getLocationOnScreen(locationInScreen);
            int topOnScreen = locationInScreen[1];
            int bottomOnScreen = topOnScreen + height;
            int autoScrollFlag = TileView.AUTO_SCROLL_FLAG_STOP;

            if (bottomOnScreen > SCROLL_ACTIVE_BOTTOM_BOUNDARY) {
                autoScrollFlag = TileView.AUTO_SCROLL_FLAG_TO_BOTTOM;
            } else if (topOnScreen < SCROLL_ACTIVE_TOP_BOUNDARY) {
                autoScrollFlag = TileView.AUTO_SCROLL_FLAG_TO_TOP;
            }

            mTileViewListener.setGridViewListener(gridViewListener);
            mTileViewListener.setAutoScrollY(autoScrollFlag);

            // search target view
            mCurrentTargetViewIndex = searchTargetChildView(mDraggedViewIndex, rawX, rawY, mOriTopOnScreen);
            mPreviousTargetViewIndex = applyTargetViewEffect(mPreviousTargetViewIndex, mCurrentTargetViewIndex[0]);
        }

        /**
         * define action for last touch up event
         *
         * @param view
         */
        private void touchUp(final View view) {
            // It occur mCurrentTargetViewIndex is null according to touch
            // motion
            if (mCurrentTargetViewIndex == null) {
                return;
            }

            // swap dragged view and target view
            if (mCurrentTargetViewIndex[0] != -1) {
                mIsSwap = swapView(mDraggedViewIndex, mCurrentTargetViewIndex[0], mOriLeft, mOriTop);
            } else if (mCurrentTargetViewIndex[1] != -1) {
                // fill blank area when dragging half type view
                mIsSwap = fillBlankArea(mDraggedViewIndex, mCurrentTargetViewIndex[1], mOriLeft, mOriTop);
            }

            // restore dragged view to original location
            if (!mIsSwap) {
                moveTile(view, mOriLeft, mOriTop);
                applyTargetViewEffect(mPreviousTargetViewIndex, -1);
            }

            view.setOnTouchListener(null);
            view.clearFocus();
            mTileViewListener.setAutoScrollY(TileView.AUTO_SCROLL_FLAG_STOP);
        }
    }
}
