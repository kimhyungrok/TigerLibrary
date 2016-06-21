package com.empire.tigerlibrary.tool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;

import java.util.List;

/**
 * for cleaning memory and check current memory status
 *
 * @author lordvader
 *
 */
public class MemoryCleaner {
	final static String EXCLUDE_PKG[] = { "com.skp.ria" };

	/**
	 * get current available memory
	 *
	 * @param context
	 * @return
	 */
	public static long getAvailableMemory(Context context) {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		return mi.availMem;
	}

	/**
	 * get current total memory
	 *
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context) {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		return mi.totalMem;
	}

	/**
	 * run task killer for cleaning memory
	 *
	 * @param context
	 * @return
	 */
	private static int runTaskKiller(Context context) {
		final int prevPercent = (int) (100.0f - ((float) getAvailableMemory(context) / (float) getTotalMemory(context)) * 100.0f);
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		boolean isExcludePkg;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			List<ApplicationInfo> proc = context.getPackageManager().getInstalledApplications(0);

			for (ApplicationInfo appInfo : proc) {
				String pkgName = appInfo.packageName;

				if (pkgName == null)
					continue;

				isExcludePkg = false;
				for (String pkg : EXCLUDE_PKG) {
					if (pkgName.contains(pkg)) {
						isExcludePkg = true;
						break;
					}
				}
				if (!isExcludePkg) {
					am.killBackgroundProcesses(pkgName);
				}
			}
		}

		List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		if (processes != null) {
			for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
				String[] pkgList = processInfo.pkgList;
				if (pkgList == null)
					continue;
				for (String packageName : pkgList) {
					if (!packageName.equals(context.getPackageName())) {
						am.killBackgroundProcesses(packageName);
					}
				}
			}
		}
		return prevPercent;
	}

	/**
	 * run task killer and show result toast message
	 *
	 * @param context
	 */
	public static void cleanMemory(final Context context) {
		final int prevPercent = runTaskKiller(context);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				int currentPercent = (int) (100.0f - ((float) getAvailableMemory(context) / (float) getTotalMemory(context)) * 100.0f);
				int diff = Math.max(prevPercent - currentPercent, 0);

//				String message = String.format(context.getString(R.string.toast_clear_memory), diff);
//				Utils.showToast(context, message);
			}
		}, 300);
	}
}
