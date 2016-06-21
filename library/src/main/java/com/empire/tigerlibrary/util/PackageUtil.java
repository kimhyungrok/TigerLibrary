package com.empire.tigerlibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

/**
 * useful class composed by methods for using PackageManager
 *
 * @author lordvader
 */
public class PackageUtil {
    public static final int CHK_PKG_NO_EXIST = 0;
    public static final int CHK_PKG_SAME_VER = 1;
    public static final int CHK_PKG_NEW_VER = 2;
    public static final int CHK_PKG_OLD_VER = 3;
    public static final int CHK_PKG_INVALID_VER = 4;

    /**
     * check such package exist
     *
     * @param activity
     * @param pkgName
     * @return
     */
    public static boolean checkExistPacakge(Activity activity, String pkgName) {
        boolean isExist = false;
        PackageManager pkgManager = activity.getPackageManager();

        try {
            pkgManager.getApplicationInfo(pkgName, 0);
            isExist = true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            isExist = false;
        }

        return isExist;
    }

    /**
     * compare exist package's version code and param's version code
     *
     * @param activity
     * @param pkgName
     * @param versionCode
     * @return
     */
    public static int checkPackageStatus(Activity activity, String pkgName, String versionCode) {
        int result = CHK_PKG_NO_EXIST;

        PackageManager pkgManager = activity.getPackageManager();
        try {
            PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
            int versionCodeDiffer = Integer.valueOf(versionCode) - pkgInfo.versionCode;

            if (versionCodeDiffer == 0) {
                result = CHK_PKG_SAME_VER;
            } else if (versionCodeDiffer > 0) {
                result = CHK_PKG_NEW_VER;
            } else {
                result = CHK_PKG_OLD_VER;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result = CHK_PKG_INVALID_VER;
        }

        return result;
    }

    /**
     * get application label
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppName(Context context, String packageName) {
        if (Utils.isEmptyString(packageName)) {
            return null;
        }

        PackageManager pkgManager = context.getPackageManager();
        String appName = null;

        try {
            ApplicationInfo appInfo = pkgManager.getApplicationInfo(packageName, 0);
            CharSequence appLabel = appInfo.loadLabel(pkgManager);
            appName = appLabel != null ? appLabel.toString() : "";
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            appName = null;
        }

        return appName;
    }

    /**
     * get application label
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Info getAppNameNIcon(Context context, String packageName) {
        if (Utils.isEmptyString(packageName)) {
            return null;
        }

        PackageManager pkgManager = context.getPackageManager();
        Info pkgInfo = null;

        try {
            ApplicationInfo appInfo = pkgManager.getApplicationInfo(packageName, 0);
            CharSequence appLabel = appInfo.loadLabel(pkgManager);
            String appName = appLabel != null ? appLabel.toString() : "";
            pkgInfo = new Info(appInfo.loadIcon(pkgManager), appName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            pkgInfo = null;
        }

        return pkgInfo;
    }

    /**
     * check such package exist
     *
     * @param activity
     * @param pkgName
     * @return
     */
    public static int getAppVersionCode(Activity activity, String pkgName) {
        int versionCode = -1;
        PackageManager pkgManager = activity.getPackageManager();

        try {
            PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
            versionCode = pkgInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            versionCode = -1;
        }
        return versionCode;
    }

    /**
     * temporary storage for package info
     *
     * @author lordvader
     */
    public static class Info {
        public final Drawable icon;
        public final String label;

        private Info(Drawable icon, String label) {
            this.icon = icon;
            this.label = label;
        }
    }

}
