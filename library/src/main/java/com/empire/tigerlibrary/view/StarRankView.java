package com.empire.tigerlibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.util.Utils;

import java.util.ArrayList;

/**
 * showing star shape rate image by rank number
 * Created by lordvader on 2015. 7. 21..
 */
public class StarRankView extends LinearLayout {
    private final float DEFAULT_MAX_RANK_COUNT = 5;
    private final int STAR_ICON_SIZE = R.dimen.dp10;

    private Context mContext;
    private float mMaxRankCount = DEFAULT_MAX_RANK_COUNT;
    private ArrayList<ImageView> mStarIconViewList = new ArrayList<ImageView>();
    private int mStarIconSize;

    public StarRankView(Context context) {
        super(context);
        initialize(context, null);
    }

    public StarRankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public StarRankView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    /**
     * initialize this view
     *
     * @param context
     */
    private void initialize(Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StarRankView);
            mStarIconSize = typedArray.getDimensionPixelSize(R.styleable.StarRankView_icon_size, Utils.getPxFromDp(mContext, STAR_ICON_SIZE));
        }

        setDefaultIcon();
    }

    /**
     * set default star empty icon
     */
    private void setDefaultIcon() {
        for (int i = 0; i < mMaxRankCount; i++) {
            ImageView starIconView = new ImageView(mContext);
            starIconView.setImageResource(R.drawable.ic_star_empty);
            starIconView.setScaleType(ImageView.ScaleType.FIT_XY);

            LayoutParams starIconViewLayoutParam = new LayoutParams(mStarIconSize, mStarIconSize);
            starIconViewLayoutParam.weight = 1;
            starIconView.setLayoutParams(starIconViewLayoutParam);

            mStarIconViewList.add(starIconView);
            addView(starIconView, starIconViewLayoutParam);
        }
    }

    /**
     * set rank and display related star
     *
     * @param rank
     */
    public void setRank(float rank) {
        if (rank > mMaxRankCount) {
            rank = mMaxRankCount;
        }

        int starIconViewCount = mStarIconViewList.size();
        ImageView starIconView = null;
        int starResourceId;

        for (int i = 0; i < mMaxRankCount; i++) {
            starIconView = mStarIconViewList.get(i);

            if (i < (int) rank) {
                starResourceId = R.drawable.ic_star_full;
            } else {
                if (i == (int) rank && ((rank % (int) rank) > 0)) {
                    starResourceId = R.drawable.ic_star_half;
                } else {
                    starResourceId = R.drawable.ic_star_empty;
                }
            }

            starIconView.setImageResource(starResourceId);
        }
    }

    /**
     * set icon size
     *
     * @param size
     */
    public void setIconSize(int size) {
        if (size > 0) {
            for (ImageView starIconView : mStarIconViewList) {
                LayoutParams starIconViewLayoutParam = (LayoutParams) starIconView.getLayoutParams();
                starIconViewLayoutParam.width = size;
                starIconViewLayoutParam.height = size;
                starIconView.setLayoutParams(starIconViewLayoutParam);
            }
        }
    }

    /**
     * set max rank count
     *
     * @param maxRankCount
     */
    public void setMaxRank(float maxRankCount) {
        if (maxRankCount > 0) {
            mMaxRankCount = maxRankCount;
        }

        this.removeAllViews();
        mStarIconViewList.clear();
        setDefaultIcon();
    }
}

