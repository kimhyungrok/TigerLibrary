package com.empire.tigerlibrary.util;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.empire.tigerlibrary.tool.AsyncTasker;
import com.empire.tigerlibrary.tool.image.BlurBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * useful class composed by methods for handling about image
 * Created by lordvader on 2016. 4. 21..
 */
public class ImageUtil {
    public static final int BITMAP_QUALITY_HIGH = 0;
    public static final int BITMAP_QUALITY_LOW = 1;
    private static final float BLUR_DARKEN_ALPHA = 0.3f;
    private static final int FAST_BLUR_RADIUS = 40;

    /**
     * @param view
     * @param filePath
     * @return
     */
    public static Uri getScreenshot(View view, String filePath) {
        FileOutputStream out = null;

        try {
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            if (bitmap != null) {
                File fileScreenshot = new File(filePath);
                out = new FileOutputStream(fileScreenshot);
                boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();

                if (result) {
                    view.destroyDrawingCache();
                    return Uri.fromFile(fileScreenshot);
                } else {
                    fileScreenshot.delete();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        view.destroyDrawingCache();
        return null;
    }

    /**
     * save bitmap to file and return created file uri
     *
     * @param context
     * @param bitmap
     * @param fileName
     * @return
     */
    public static Uri saveBitmapToFile(Context context, Bitmap bitmap, String fileName, int bitmapQuality) {
        if (Utils.isEmptyString(fileName) || bitmap == null) {
            return null;
        }

        Uri uri = null;
        StringBuilder cacheFileName = new StringBuilder();
        cacheFileName.append(context.getFilesDir());
        cacheFileName.append("/");
        cacheFileName.append(fileName);
        File outputFile = new File(cacheFileName.toString());
        BufferedOutputStream bos = null;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(outputFile));

            if (bitmapQuality == BITMAP_QUALITY_LOW) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            }
            uri = Uri.fromFile(outputFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            FileUtil.closeSilently(bos);
        }

        return uri;
    }

    /**
     * load bitmap from file
     *
     * @return
     */
    public static Bitmap loadImage(String imageUri) {
        if (!Utils.isEmptyString(imageUri)) {
            return BitmapFactory.decodeFile(Uri.parse(imageUri).getPath());
        }

        return null;
    }

    /**
     * get drawable resource uri
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Uri getDrawableUri(Context context, int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(drawableId) + '/' +
                context.getResources().getResourceTypeName(drawableId) + '/' + context.getResources().getResourceEntryName(drawableId));
    }

    /**
     * adjust bitmap's alpha
     *
     * @param bitmap
     * @param opacity
     * @return
     */
    public static Bitmap adjustOpacity(Bitmap bitmap, int opacity) {
        Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    /**
     * get wallpaper which is applied by blur effect
     *
     * @param context
     * @return
     */
    public static void setBlurWallpaper(ViewGroup rootView, Context context) {
        setBlurWallpaper(rootView, context, BLUR_DARKEN_ALPHA);
    }

    /**
     * get wallpaper which is applied by blur effect
     *
     * @param context
     * @return
     */
    public static void setBlurWallpaper(ViewGroup rootView, Context context, float darkenAlpha) {
        Bitmap blurBitmap = null;
        Drawable wallpaperDrawable = WallpaperManager.getInstance(context).getDrawable();

        if (wallpaperDrawable != null && wallpaperDrawable instanceof BitmapDrawable) {
            Bitmap wallpaperBitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();

            if (wallpaperBitmap != null && rootView != null) {
                int wallpaperBitmapWidth = wallpaperBitmap.getWidth();
                int adjustWidth = wallpaperBitmap.getHeight() * ViewUtil.getDisplayWidth(context) / ViewUtil.getDisplayHeight(context);

                if (adjustWidth > wallpaperBitmapWidth) {
                    adjustWidth = wallpaperBitmapWidth;
                }

                int x = (wallpaperBitmap.getWidth() / 2 - adjustWidth / 2);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    new FastBlurWorkerTask(context, rootView, x, adjustWidth, darkenAlpha).execute(wallpaperBitmap);
                } else {
                    BitmapDrawable blurImageDrawable = ImageUtil.getBlurImage(context, wallpaperBitmap, x, adjustWidth);
                    applyDarken(blurImageDrawable, darkenAlpha);
                    View mirrorView = new View(context);
                    mirrorView.setBackgroundDrawable(blurImageDrawable);
                    rootView.addView(mirrorView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                            .MATCH_PARENT));
                }
            }
        }
    }

    /**
     * apply darken effect on BitmapDrawable
     *
     * @param darkenAlpha
     * @param bitmapDrawable
     */
    private static void applyDarken(BitmapDrawable bitmapDrawable, float darkenAlpha) {
        // apply darken color filter
        if (darkenAlpha > 0f && darkenAlpha < 1f && bitmapDrawable != null) {
            int colorFilterValue = (int) (255 * (1f - darkenAlpha));
            bitmapDrawable.setColorFilter(Color.rgb(colorFilterValue, colorFilterValue, colorFilterValue), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * get blur BitmapDrawable
     *
     * @param wallpaperBitmap
     * @param x
     * @param width
     * @return
     */
    public static BitmapDrawable getBlurImage(Context context, Bitmap wallpaperBitmap, int x, int width) {
        Bitmap cropBitmap = Bitmap.createBitmap(wallpaperBitmap, x, 0, width, wallpaperBitmap.getHeight());
        Bitmap blurBitmap = BlurBuilder.blur(context, cropBitmap, BlurBuilder.STRONG_BLUR);
        return new BitmapDrawable(context.getResources(), blurBitmap);
    }

    /**
     * apply blur effect to param's bitmap
     *
     * @param sentBitmap
     * @param radius
     * @return
     */
    public static Bitmap fastBlur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (bitmap == null) {
            return sentBitmap;
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];

        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    /**
     * get orientation of image which is matched to filePath
     *
     * @param filePath
     * @return
     */
    public static int getImageOrientation(String filePath) {
        int orientation = 0;

        if (!Utils.isEmptyString(filePath)) {
            try {
                ExifInterface exif = new ExifInterface(filePath);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90: {
                        orientation = 90;
                        break;
                    }
                    case ExifInterface.ORIENTATION_ROTATE_180: {
                        orientation = 180;
                        break;
                    }
                    case ExifInterface.ORIENTATION_ROTATE_270: {
                        orientation = 270;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return orientation;
    }

    /*******************************************************************************
     * inner class and interface
     *******************************************************************************/
    /**
     * handling background wallpaper blur effect by asynchronously
     */
    private static class FastBlurWorkerTask extends AsyncTasker<Bitmap, Void, BitmapDrawable> {
        private Context context;
        private ViewGroup rootView;
        private int x;
        private int width;
        private float darkenAlpha;

        public FastBlurWorkerTask(Context context, ViewGroup rootView, int x, int width, float darkenAlpha) {
            super();
            this.context = context;
            this.rootView = rootView;
            this.x = x;
            this.width = width;
            this.darkenAlpha = darkenAlpha;
        }

        @Override
        protected BitmapDrawable doInBackground(Bitmap... params) {
            if (params[0] != null) {
                Bitmap cropBitmap = Bitmap.createBitmap(params[0], x, 0, width, params[0].getHeight());
                Bitmap blurBitmap = fastBlur(cropBitmap, FAST_BLUR_RADIUS);
                return new BitmapDrawable(context.getResources(), blurBitmap);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            if (bitmapDrawable != null) {
                applyDarken(bitmapDrawable, darkenAlpha);
                View mirrorView = new View(context);
                mirrorView.setBackgroundDrawable(bitmapDrawable);
                rootView.addView(mirrorView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            super.onPostExecute(bitmapDrawable);
        }
    }
}
