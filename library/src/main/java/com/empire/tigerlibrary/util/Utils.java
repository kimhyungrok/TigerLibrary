package com.empire.tigerlibrary.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.empire.tigerlibrary.tool.SimpleActionListener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * set class which is composed of useful methods
 * Created by lordvader on 2015. 10. 12..
 */
public class Utils {
    /**
     * check whether string is null or empty
     *
     * @param str
     * @return
     */
    public static boolean isEmptyString(String str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get resource uri which is related to resourceId
     *
     * @param activity
     * @param resourceId
     * @return
     */
    public static Uri getResourceUri(Activity activity, int resourceId) {
        return Uri.parse("android:resource://" + activity.getPackageName() + "/" + resourceId);
    }

    /**
     * change dp to px
     *
     * @param context
     * @param dimensionId
     * @return
     */
    public static int getPxFromDp(Context context, int dimensionId) {
        return context.getResources().getDimensionPixelSize(dimensionId);
    }

    /**
     * get px from dp value
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int getPxFromDpValue(Context context, int dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density);
    }

    /**
     * check device desity
     *
     * @param context
     */
    public static void checkDeviceDensity(Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                Log.v("VV", "device density : LDPI");
//                Toast.makeText(context, "LDPI", Toast.LENGTH_SHORT).show();
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Log.v("VV", "device density : MDPI");
//                Toast.makeText(context, "MDPI", Toast.LENGTH_SHORT).show();
                break;
            case DisplayMetrics.DENSITY_HIGH:
                Log.v("VV", "device density : HDPI");
//                Toast.makeText(context, "HDPI", Toast.LENGTH_SHORT).show();
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                Log.v("VV", "device density : XHDPI");
//                Toast.makeText(context, "XHDPI", Toast.LENGTH_SHORT).show();
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                Log.v("VV", "device density : XXHDPI");
//                Toast.makeText(context, "XXHDPI", Toast.LENGTH_SHORT).show();
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                Log.v("VV", "device density : XXXHDPI");
//                Toast.makeText(context, "XXXHDPI", Toast.LENGTH_SHORT).show();
                break;
            default: {
                Log.v("VV", "device density : unknown");
//                Toast.makeText(context, "unknown", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    /**
     * check device screen layout size
     *
     * @param context
     */
    public static void checkDeviceScreenLayoutSize(Context context) {
        int screenSize = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
//                Toast.makeText(context, "Large screen", Toast.LENGTH_LONG).show();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//                Toast.makeText(context, "Normal screen", Toast.LENGTH_LONG).show();
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
//                Toast.makeText(context, "Small screen", Toast.LENGTH_LONG).show();
                break;
            default:
//                Toast.makeText(context, "Screen size is neither large, normal or small", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * get current locale's currency symbol
     *
     * @param context
     * @return
     */
    public static String geCurrrencySymbol(Context context) {
        return Currency.getInstance(context.getResources().getConfiguration().locale).getSymbol();
    }

    /**
     * remove comma in string
     *
     * @param str
     * @return
     */
    public static String removeComma(String str) {
        if (!Utils.isEmptyString(str)) {
            return str.replaceAll(",", "");
        } else {
            return str;
        }
    }

    /**
     * check whether locale is korean or foreign language
     *
     * @param context
     * @return
     */
    public static boolean isKoreanLocale(Context context) {
        String currentLocale = Locale.getDefault().getLanguage();

        if (Utils.isEmptyString(currentLocale)) {
            currentLocale = "ko";
        }

        return "ko".equals(currentLocale);
    }

    /**
     * get current locale
     *
     * @return
     */
    public static String getCurrentLocale() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * show simple progress dialog
     *
     * @param activity
     * @param message
     * @return
     */
    public static ProgressDialog showSimpleProgressDialog(Activity activity, String message) {
        ProgressDialog progressDlg = new ProgressDialog(activity);
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setMessage(message);
        progressDlg.setCancelable(false);
        progressDlg.show();

        return progressDlg;
    }

    /**
     * show toast pop-up message
     *
     * @param text
     */
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * show toast pop-up message
     *
     * @param context
     * @param textResId
     */
    public static void showToast(Context context, int textResId) {
        Toast.makeText(context, context.getString(textResId), Toast.LENGTH_SHORT).show();
    }

    /**
     * check whether current screen is lock screen
     *
     * @param context
     * @return
     */
    public static boolean isOnLockScreenCurrently(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.isKeyguardLocked();
    }

    /**
     * check whether specific service is running currently
     *
     * @param context
     * @param serviceClass
     * @return
     */
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * apply bold and different font size to specific words in TextView
     *
     * @param context
     * @param text
     * @param specificWordList
     * @param boldSize
     * @return
     */
    public static Spannable applyBoldInSpecificWord(Context context, String text, ArrayList<String> specificWordList, int boldSize) {
        if (!isEmptyString(text)) {
            Spannable sp = new SpannableString(text);

            for (String specificWord : specificWordList) {
                int startIndex = text.indexOf(specificWord);

                if (startIndex < 0) {
                    continue;
                }

                int endIndex = startIndex + specificWord.length();

                sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // bold
                sp.setSpan(new AbsoluteSizeSpan(Utils.getPxFromDp(context, boldSize)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//
                // resize
            }

            return sp;
        }

        return new SpannableString(text);
    }

    /**
     * start media scanning
     *
     * @param activity
     * @param scanningPath
     */
    public static void startMediaScanning(Activity activity, String scanningPath) {
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + scanningPath)));
    }

    /**
     * launch system location activity for checking and setting location info
     * using (handle ui listener)
     */
    public static void checkSystemLocationSetting(Context context, SimpleActionListener uiListener) {
        LocationManager locationMaanger = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled = locationMaanger.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = locationMaanger.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!(isGpsProviderEnabled || isNetworkProviderEnabled)) {
            Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            gpsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(gpsIntent);

            if (uiListener != null) {
                uiListener.doAction();
            }
        }
    }

    /**
     * launch system location activity for checking and setting location info
     * using
     */
    public static void checkSystemLocationSetting(Context context) {
        checkSystemLocationSetting(context, null);
    }

    /**
     * get general environment file path
     *
     * @return
     */
    public static String getFilePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;
    }

    /**
     * get device UUID
     *
     * @param context
     * @return
     */
    public static String getDeviceUUID(final Context context) {
        UUID uuid = null;
        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            e.getStackTrace();
        }

        return uuid != null ? uuid.toString() : null;
    }

    /**
     * get address using by latitude and longitude
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getAddress(Context context, double latitude, double longitude) {
        StringBuilder address = new StringBuilder();

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && !addressList.isEmpty()) {
                Address addrss = addressList.get(0);

                if (addrss != null) {
                    address.append(addrss.getAdminArea());
                    address.append(" ");
                    address.append(addrss.getLocality());
                    address.append(" ");
                    address.append(addrss.getThoroughfare());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address.toString();
    }

    /**
     * check whether device's GPS is enabled
     *
     * @param context
     * @return
     */
    public static boolean isUsingGPS(Context context) {
        LocationManager locationMaanger = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled = locationMaanger.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = locationMaanger.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGpsProviderEnabled || isNetworkProviderEnabled;
    }
}


