package com.empire.tigerlibrary.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.View;

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
    /**
     * used by saveBitmapToFile()
     */
    public static final int BITMAP_QUALITY_HIGH = 0;
    public static final int BITMAP_QUALITY_LOW = 1;

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
}
