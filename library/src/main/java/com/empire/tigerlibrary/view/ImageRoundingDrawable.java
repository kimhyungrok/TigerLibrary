package com.empire.tigerlibrary.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * change normal square image to corner rounding image
 * Created by lordvader on 2015. 11. 25..
 */
public class ImageRoundingDrawable extends Drawable {
    private final int DEFAULT_ROUND_RADIUS = 300;
    private int mRoundRadius = DEFAULT_ROUND_RADIUS;
    private Paint mPaint;

    public ImageRoundingDrawable(Bitmap sourceImage) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(new BitmapShader(sourceImage, Shader.TileMode.CLAMP, Shader.TileMode.MIRROR));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(new RectF(0.0f, 0.0f, getBounds().width(), getBounds().height()), mRoundRadius, mRoundRadius, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return 255;
    }

    /**
     * set corner round radius
     * @param roundRadius
     */
    public void setRoundRadius(int roundRadius) {
        mRoundRadius = roundRadius;
    }
}
