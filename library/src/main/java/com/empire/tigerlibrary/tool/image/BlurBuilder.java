package com.empire.tigerlibrary.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * help for handling and making blur Bitmap image
 *
 * @author hyungrok.kim
 *
 */
public class BlurBuilder {
    public static final int STRONG_BLUR = 0;
    public static final int WEAK_BLUR = 1;

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    /**
     * get blur image after extracting Bitmap from view
     *
     * @param view
     * @param intensity
     * @return
     */
    public static Bitmap blur(View view, int intensity) {
        if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
            return blur(view.getContext(), getScreenshot(view), intensity);
        }

        return null;
    }

    /**
     * get blur image
     *
     * @param context
     * @param image
     * @param intensity
     * @return
     */
    public static Bitmap blur(Context context, Bitmap image, int intensity) {
        float bitmapScale = (intensity == STRONG_BLUR) ? BITMAP_SCALE / 2 : BITMAP_SCALE;
        float blurRadius = (intensity == STRONG_BLUR) ? 25 : BLUR_RADIUS;

        int width = Math.round(image.getWidth() * bitmapScale);
        int height = Math.round(image.getHeight() * bitmapScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    /**
     * extract Bitmap from view
     *
     * @param v
     * @return
     */
    private static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}