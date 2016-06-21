package com.empire.tigerlibrary.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.empire.tigerlibrary.R;
import com.empire.tigerlibrary.util.Utils;
import com.empire.tigerlibrary.util.ViewUtil;

import java.util.ArrayList;

/**
 * Quick action popup window which is showed beside anchor view
 * Created by lordvader on 2015. 7. 27..
 */
public class QuickActionPopup {
    private final int ITEM_SPACE = R.dimen.dp12;
    private final float BACKGROUND_ROUND_PX = 5;
    private final int SHADOW_EDGE = 10;

    private Context mContext;
    private ArrayList<ActionItem> mActionItemList = new ArrayList<ActionItem>();
    private QuickActionItemListener mQuickActionItemListener;
    private PopupWindow mPopup;

    public QuickActionPopup(Context context) {
        mContext = context;
    }

    /**
     * get backround image
     *
     * @param popupWindowContainer
     * @return
     */
    private Bitmap getBackgroundImage(LinearLayout popupWindowContainer) {
        int srcBackgroundWidth = popupWindowContainer.getMeasuredWidth();
        int srcBackgroundHeight = popupWindowContainer.getMeasuredHeight();

        // create source background image
        Bitmap srcBitmp = Bitmap.createBitmap(srcBackgroundWidth, srcBackgroundHeight, Bitmap.Config.ARGB_8888);
        Canvas srcCanvas = new Canvas(srcBitmp);
        srcCanvas.drawColor(0xffffffff);

        // apply shadow and rounding
        Bitmap output = srcBitmp.copy(srcBitmp.getConfig(), true);
        Canvas outputCanvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, srcBackgroundWidth, srcBackgroundHeight);
        rect.left = SHADOW_EDGE;
        rect.top = SHADOW_EDGE;
        rect.right = srcBackgroundWidth - SHADOW_EDGE;
        rect.bottom = srcBackgroundHeight - SHADOW_EDGE;

        final RectF rectF = new RectF(rect);
        Paint shadowPaint = new Paint();
        shadowPaint.setColor(Color.WHITE);
        shadowPaint.setShadowLayer(SHADOW_EDGE + 2, 0, 0, 0xFF555555);
        outputCanvas.drawRoundRect(rectF, BACKGROUND_ROUND_PX, BACKGROUND_ROUND_PX, shadowPaint);

        return output;
    }

    /**
     * add action item
     *
     * @param itemText
     */
    public void addItem(String itemText) { //}, View.OnClickListener itemClickListener) {
        mActionItemList.add(new ActionItem(itemText));
    }

    /**
     * set quickActionItemListener
     *
     * @param quickActionItemListener
     */
    public void setQuickActionItemListener(QuickActionItemListener quickActionItemListener) {
        mQuickActionItemListener = quickActionItemListener;
    }

    /**
     * show popup window
     *
     * @param anchorView
     */
    public void show(View anchorView) {
        mPopup = new PopupWindow(anchorView);

        // set container
        LinearLayout popupWindowContainer = new LinearLayout(mContext);
        popupWindowContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        containerLayoutParams.weight = 1;
        popupWindowContainer.setLayoutParams(containerLayoutParams);

        // add item
        int itemSpace = Utils.getPxFromDp(mContext, ITEM_SPACE);
        int actionItemWidth = ViewUtil.getDisplayWidth((Activity) mContext) / 2;
        int itemIndex = 0;

        for (ActionItem mActionItem : mActionItemList) {
            TextView actionItem = new TextView(mContext);
            actionItem.setTag(itemIndex++);
            actionItem.setText(mActionItem.itemText);

            if (mQuickActionItemListener != null) {
                actionItem.setOnClickListener(mQuickActionItemListener);
            }

            actionItem.setPadding(itemSpace, itemSpace, itemSpace, itemSpace);
            popupWindowContainer.addView(actionItem, new LinearLayout.LayoutParams(actionItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        popupWindowContainer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View
                .MeasureSpec.UNSPECIFIED));


        // set background
        final Bitmap backgroundImage = getBackgroundImage(popupWindowContainer);
        mPopup.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), backgroundImage));
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (backgroundImage != null && !backgroundImage.isRecycled()) {
                    backgroundImage.recycle();
                }
            }
        });

        // show popup window
        int[] anchorViewlocationOnScreen = new int[2];
        anchorView.getLocationOnScreen(anchorViewlocationOnScreen);
        int x = (anchorViewlocationOnScreen[0] + anchorView.getWidth()) - (popupWindowContainer.getMeasuredWidth() + SHADOW_EDGE);
        int y;

        if (anchorViewlocationOnScreen[1] > ViewUtil.getDisplayHeight((Activity) mContext) / 2) {
            y = anchorViewlocationOnScreen[1] - popupWindowContainer.getMeasuredHeight();
            mPopup.setAnimationStyle(R.style.popup_window_animation_below);
        } else {
            y = anchorViewlocationOnScreen[1] + anchorView.getHeight();
            mPopup.setAnimationStyle(R.style.popup_window_animation_above);
        }

        // setting popup window
        mPopup.setContentView(popupWindowContainer);
        mPopup.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup.setTouchable(true);
        mPopup.setFocusable(true);
        mPopup.setOutsideTouchable(true);
        mPopup.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
    }

    /**
     * dismiss popup window
     */
    public void dismiss() {
        if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    /**
     * listener for handling click event of each item
     */
    public abstract static class QuickActionItemListener implements View.OnClickListener {
        @Override
        public final void onClick(View view) {
            int index = (Integer) view.getTag();
            onItemClick(index);
        }

        public abstract void onItemClick(int index);
    }

    /**
     * class for each action item
     */
    class ActionItem {
        private final String itemText;

        public ActionItem(String itemText) {
            this.itemText = itemText;
        }
    }
}
