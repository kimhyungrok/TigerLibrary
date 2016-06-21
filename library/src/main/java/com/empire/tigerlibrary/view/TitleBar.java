package com.empire.tigerlibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.util.Utils;


/**
 * common title bar which is activated like ActionBar
 * Created by lordvader on 2015. 6. 26..
 */
public class TitleBar extends RelativeLayout {
    private final int HOME_BUTTON_WIDTH = R.dimen.dp50;
    private final int HOME_BUTTON_ID = 0x9999;
    private final int ACTION_BUTTON_WIDTH = R.dimen.dp50;
    private final int TITLE_TEXT_COLOR = R.color.color_white;
    private final int TITLE_TEXT_SIZE = R.dimen.titlebar_text_size;

    private Context mContext;
    private ImageView mHomeButton;
    private TextView mTitleView;
    private ImageView mActionButton;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mHomeButton) {
                Toast.makeText(mContext, "home button clicked", Toast.LENGTH_SHORT).show();
            } else if (view == mActionButton) {
                Toast.makeText(mContext, "action button clicked", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public TitleBar(Context context) {
        super(context);
        initialize(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
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
        setupLayout();
    }

    /**
     * setup default layout
     */
    private void setupLayout() {
        // home button
        mHomeButton = new ImageView(mContext);
        mHomeButton.setId(HOME_BUTTON_ID);
        mHomeButton.setScaleType(ImageView.ScaleType.CENTER);
//        mHomeButton.setOnClickListener(mOnClickListener);

        LayoutParams homeButtonLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        homeButtonLayoutParam.width = Utils.getPxFromDp(mContext, HOME_BUTTON_WIDTH);
        homeButtonLayoutParam.alignWithParent = true;
        homeButtonLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mHomeButton.setLayoutParams(homeButtonLayoutParam);
        this.addView(mHomeButton);

        // title
        mTitleView = new TextView(mContext);
        mTitleView.setGravity(Gravity.CENTER_VERTICAL);
        mTitleView.setTextColor(mContext.getResources().getColor(TITLE_TEXT_COLOR));
        mTitleView.setTextSize(Utils.getPxFromDp(mContext, TITLE_TEXT_SIZE));

        LayoutParams titleViewLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        titleViewLayoutParam.addRule(RelativeLayout.RIGHT_OF, mHomeButton.getId());
        mTitleView.setLayoutParams(titleViewLayoutParam);
        this.addView(mTitleView);


        // action button
        mActionButton = new ImageView(mContext);
        mActionButton.setScaleType(ImageView.ScaleType.CENTER);

        LayoutParams actionButtonLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        actionButtonLayoutParam.width = Utils.getPxFromDp(mContext, ACTION_BUTTON_WIDTH);
        actionButtonLayoutParam.alignWithParent = true;
        actionButtonLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mActionButton.setLayoutParams(actionButtonLayoutParam);
        this.addView(mActionButton);
    }

    /**
     * set home button background resource id
     *
     * @param resId
     */
    public void setHomeButtonImageResource(int resId) {
        mHomeButton.setImageResource(resId);
    }

    /**
     * set home button OnClickListener
     *
     * @param onClickListener
     */
    public void setHomeButtonOnClickListener(OnClickListener onClickListener) {
        mHomeButton.setOnClickListener(onClickListener);
    }

    /**
     * set title bar title text
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    /**
     * set action button background resource id
     *
     * @param resId
     */
    public void setActionButtonImageResource(int resId) {
        mActionButton.setImageResource(resId);
    }

    /**
     * set action button OnClickListener
     *
     * @param onClickListener
     */
    public void setActionButtonOnClickListener(OnClickListener onClickListener) {
        mActionButton.setOnClickListener(onClickListener);
    }

    /**
     * set title text size
     * @param textSize
     */
    public void setTitleTextSize(float textSize) {
        mTitleView.setTextSize(textSize);
    }

    /**
     * set title text color
     * @param colorValue
     */
    public void setTitleTextColor(int colorValue) {
        mTitleView.setTextColor(colorValue);
    }
}
