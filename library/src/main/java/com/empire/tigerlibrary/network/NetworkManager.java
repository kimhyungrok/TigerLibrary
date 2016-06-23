package com.empire.tigerlibrary.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.empire.tigerlibrary.manager.InstantSingletonManager;

/**
 * managing request and response using HTTP protocol which is bases by Volley library
 *
 * @author hyungrok.kim
 *
 */
public class NetworkManager implements InstantSingletonManager.SingleTon {
    private static NetworkManager sInstance = null;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private NetworkManager(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(mContext));
    }

    /**
     * get NetworkManager instances
     *
     * @param context
     * @return
     */
    public synchronized static NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkManager(context);
            InstantSingletonManager.getInstane().add(sInstance);
        }
        return sInstance;
    }

    /**
     * check whether singleton instance exist
     *
     * @return
     */
    public static boolean isInstanceExist() {
        return sInstance != null;
    }

    /**
     * clear NetworkManager instance
     */
    public static void clear() {
        sInstance = null;
    }

    @Override
    public void kill() {
        if (isInstanceExist()) {
            clear();
        }
    }

    /**
     * add request to request queue.
     *
     * @param request
     * @return
     */
    public Request addRequest(Request request) {
        return mRequestQueue.add(request);
    }

    /**
     * get imageloader for handling download image from network
     *
     * @return
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * cancel all requests which are included not executed request in queue
     *
     * @param tag
     */
    public void cancelAllRequests(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * get cache size
     *
     * @param ctx
     * @return
     */
    public int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        final int screenBytes = screenWidth * screenHeight * 4;
        return screenBytes * 3;
    }

    /*******************************************************************************
     * inner class and interface
     *******************************************************************************/

    /**
     * cache for storing bitmap image
     *
     * @author hyungrok.kim
     *
     */
    private class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

        public LruBitmapCache(int maxSize) {
            super(maxSize);
        }

        public LruBitmapCache(Context ctx) {
            this(getCacheSize(ctx));
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }
    }
}
