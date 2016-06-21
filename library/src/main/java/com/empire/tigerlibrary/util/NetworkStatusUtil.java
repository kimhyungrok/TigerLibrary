package com.empire.tigerlibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("InlinedApi")
public class NetworkStatusUtil {

	/**
	 * AirplaneMode
	 *
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static boolean isAirplaneMode(Context context) {
		if (context == null) {
			return false;
		}

		int result;
		try {
			// if (android.os.Build.VERSION.SDK_INT > 16) {
			// result = Settings.Global.getInt(context.getContentResolver(),
			// Settings.Global.AIRPLANE_MODE_ON);
			// } else {
			result = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
			// }
			return result != 0;
		} catch (Exception e) {
			Log.d("KNE_EXE", e.toString());
			return false;
		}
	}

	/**
	 * 3G, Wifi
	 *
	 * @param context
	 * @return
	 */
	public static boolean use3GnWifi(Context context) {
		if (context == null) {
			return false;
		}

		// 네트워크 연결 관리자의 핸들을 얻습니다.
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) {
			return false;
		}

		// 기본 모바일 네트워크 연결자(3G) 관련 정보를 얻습니다.
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn;
		if (ni == null) {
			isMobileConn = false;
		} else {
			isMobileConn = ni.isConnected();
		}

		boolean isWifiConn = useWifi(context);

		if (isWifiConn || isMobileConn) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean useWifi(Context context) {
		if (context == null) {
			return false;
		}

		// 네트워크 연결 관리자의 핸들을 얻습니다.
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) {
			return false;
		}

		// 기본 모바일 네트워크 연결자(3G) 관련 정보를 얻습니다.
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		// WiFi 관련 정보를 얻습니다.
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn;
		if (ni == null) {
			isWifiConn = false;
		} else {
			isWifiConn = ni.isConnected();
		}

		return isWifiConn;
	}

	/**
	 * 긴급통화
	 *
	 * @param context
	 * @return
	 */
	public static boolean useSim(Context context) {
		if (context == null) {
			return false;
		}

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if (telephonyManager == null) {
			return false;
		}

		int simState = telephonyManager.getSimState();
		if (simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 로밍
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkRoaming(Context context) {
		if (context == null) {
			return false;
		}

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if (telephonyManager == null) {
			return false;
		}

		boolean isRoaming = telephonyManager.isNetworkRoaming();
		return isRoaming;
	}

	/**
	 * 3G 차단
	 */
	public static boolean use3GData(Context context) {
		if (context == null) {
			return false;
		}

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if (telephonyManager == null) {
			return false;
		}

		if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
			return true;
		} else {
			if (useWifi(context)) {
				return true;
			} else {
				return false;
			}
		}
	}

}
