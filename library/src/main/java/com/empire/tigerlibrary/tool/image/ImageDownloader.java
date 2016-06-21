package com.empire.tigerlibrary.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.empire.tigerlibrary.manager.InstantSingletonManager;

/**
 * managing download image which is belong to specific URL and set related ImageView
 * Created by lordvader on 2015. 7. 20..
 */
public class ImageDownloader implements InstantSingletonManager.SingleTon {
    private static ImageDownloader mInstance;
    private ImageLoader mImageLoader;
    private LruBitmapCache mBitmapCache;

    private ImageDownloader(Context context) {
        mBitmapCache = new LruBitmapCache(LruBitmapCache.getCacheSize(context));
        mImageLoader = new ImageLoader(Volley.newRequestQueue(context), new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return mBitmapCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mBitmapCache.put(url, bitmap);
            }
        });
    }

    /**
     * get ImageDownloader instance
     *
     * @return
     */
    public synchronized static ImageDownloader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ImageDownloader(context);
            InstantSingletonManager.getInstane().add(mInstance);
        }

        return mInstance;
    }

    /**
     * check whether instance exist
     *
     * @return
     */
    public static boolean isInstanceExist() {
        return mInstance != null;
    }

    public static ImageListener getCircleImageListener(final ImageView view, final int defaultImageResId, final int errorImageResId) {
        return new ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorImageResId != 0) {
                    view.setImageResource(errorImageResId);
                }
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    view.setImageBitmap(getCircleBitmap(response.getBitmap()));

                } else if (defaultImageResId != 0) {
                    view.setImageResource(defaultImageResId);
                }
            }
        };

    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int size = (bitmap.getWidth() / 2);
        canvas.drawCircle(size, size, size, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * get ImageLoader instance
     *
     * @return
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * release memory and resource
     */
    public void release() {
        if (mBitmapCache != null) {
            mBitmapCache.evictAll();
        }
    }

    /**
     * clear ImageDownloader instance and release related bitmap cache
     */
    public void clear() {
        release();
        mInstance = null;
    }

    @Override
    public void kill() {
        clear();
    }
}
