package com.empire.tigerlibrary.tool.image;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import java.lang.ref.WeakReference;

/**
 * image fetcher for solving concurrent issue when show download image to
 * ImageView in ListView
 *
 * @author lordvader
 */
public class AdapterImageFetcher {
    private static AdapterImageFetcher mInstance;
    private ImageLoader mImageLoader;
    private Bitmap mLoadingImage;
    private LruCache<String, Bitmap> mMemoryCache;
    private boolean mIsShowFadeInEffect;

    private AdapterImageFetcher(Context context) {
        mImageLoader = ImageDownloader.getInstance(context).getImageLoader();

        initAdapterImageFetcher();
        setShowFadeInEffect(true);
    }

    public synchronized static AdapterImageFetcher getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AdapterImageFetcher(context);
        }

        return mInstance;
    }

    /**
     * get bitmap worker task
     *
     * @param imageView
     * @return
     */
    private static ImageDownloadTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * cancel previous or potential work task
     *
     * @param imgUrl
     * @param imageView
     * @return
     */
    private static boolean cancelPotentialWork(String imgUrl, ImageView imageView) {
        final ImageDownloadTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.mUrl;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(bitmapData)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * initialize image fetcher.
     * In this time, allocate LRU cache memory
     */
    private void initAdapterImageFetcher() {
        final int nMaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int nCacheSize = nMaxMemory / 16;
        mMemoryCache = new LruCache<String, Bitmap>(nCacheSize) {
            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            protected int sizeOf(String key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB) {
                    return bitmap.getByteCount() / 1024;
                } else {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }
            }
        };
    }

    /**
     * set whether show fade in effect when loading image
     *
     * @param isShowFadeInEffect
     */
    public void setShowFadeInEffect(boolean isShowFadeInEffect) {
        mIsShowFadeInEffect = isShowFadeInEffect;
    }

    /**
     * add bitmap to memory cache
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * get bitmap from memory cache
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * set image to ImageView
     *
     * @param view
     * @param imgUrl
     */
    public void loadImage(Context context, final ImageView view, String imgUrl, int defaultImgId) {
        final Bitmap bitmap = getBitmapFromMemCache(imgUrl);

        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(imgUrl, view)) {
                final ImageDownloadTask imageDownloadTask = new ImageDownloadTask(view);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mLoadingImage, imageDownloadTask);
                view.setImageDrawable(asyncDrawable);
                imageDownloadTask.execute(new ImageDownloadTaskParam(context, imgUrl, defaultImgId));
            }
        }
    }

    /**
     * set preview image which is showed in temporary time, during bitmap is
     * downloading
     *
     * @param loadingImageResId
     */
    public void setLoadingImage(Context context, int loadingImageResId) {
        mLoadingImage = BitmapFactory.decodeResource(context.getResources(), loadingImageResId);
    }

    /**
     * release bitmap LRU cache
     */
    public void releaseBitmapCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    /**
     * clear all cache and singleTon instance
     */
    public void clear() {
        releaseBitmapCache();
        mInstance = null;
    }

    /**
     * class for managing work task
     *
     * @author lordvader
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<ImageDownloadTask> mBitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ImageDownloadTask bitmapWorkerTask) {
            super(res, bitmap);
            mBitmapWorkerTaskReference = new WeakReference<ImageDownloadTask>(bitmapWorkerTask);
        }

        public ImageDownloadTask getBitmapWorkerTask() {
            return mBitmapWorkerTaskReference.get();
        }
    }

    /**
     * AsyncTask for downloading and set bitmap
     *
     * @author lordvader
     */
    class ImageDownloadTask extends AsyncTask<ImageDownloadTaskParam, Void, Bitmap> implements ImageListener {
        private final WeakReference<ImageView> mImageViewReference;
        private String mUrl;
        private int mDefaultImgId;
        private Context mContext;

        public ImageDownloadTask(ImageView imageView) {
            mImageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(final ImageDownloadTaskParam... params) {
            mUrl = params[0].url;
            mDefaultImgId = params[0].defaultImgId;
            mContext = params[0].context;

            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    mImageLoader.get(mUrl, ImageDownloadTask.this);
                }
            });

            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (mDefaultImgId != -1) {
                ImageView imageView = mImageViewReference.get();
                imageView.setBackgroundResource(mDefaultImgId);
            }
        }

        @Override
        public void onResponse(ImageContainer container, boolean arg1) {
            ImageView imageView = mImageViewReference.get();
            Bitmap bitmap = container.getBitmap();

            if (imageView != null) {
                final ImageDownloadTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

                if (this == bitmapWorkerTask && imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
                        addBitmapToMemoryCache((String) mUrl, bitmap);
                    }
                }
            }
        }
    }

    /**
     * ImageDownloadTaskParam for handling multiple primitive parameter
     *
     * @author lordvader
     */
    class ImageDownloadTaskParam {
        private final Context context;
        private final String url;
        private final int defaultImgId;

        public ImageDownloadTaskParam(Context context, String url, int defaultImgId) {
            this.context = context;
            this.url = url;
            this.defaultImgId = defaultImgId;
        }
    }
}
