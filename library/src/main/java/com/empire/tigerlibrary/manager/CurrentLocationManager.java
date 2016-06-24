package com.empire.tigerlibrary.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * current location manager for getting current location using by network or GPS
 *
 * @author hyungrok.kim
 */
public class CurrentLocationManager {
    public static final float DEFAULT_LATITUDE = 37.40249f;
    public static final float DEFAULT_LONGITUDE = 127.10293f;
    private static CurrentLocationManager mInstance;
    private final int UPDATE_INTERVAL = 5 * 1000;
    private final int MINIMUM_DISTANCE = 0;
    /**
     * switch flag
     */
    private final boolean IS_USE_LAST_KNOWN_LOCATION = false;
    private final boolean IS_USE_LOCATION = true;

    private Context mContext;
    private LocationManager mLocationManager;
    private Criteria mCriteria;
    private Location mDefaultLocation;
    private CurrentLocationListener mLocationListener;

    private CurrentLocationManager(Context context) {
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new CurrentLocationListener(context);

        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setCostAllowed(true);
        mCriteria.setSpeedRequired(true);
        mCriteria.setAltitudeRequired(true);

        mDefaultLocation = new Location(LocationManager.NETWORK_PROVIDER);
//        mDefaultLocation.setLatitude(Preference.COMMON.getFloatValue(context, PrefKey.COMMON_LATITUE.key(), Define.DEFAULT_LATITUDE));
//        mDefaultLocation.setLongitude(Preference.COMMON.getFloatValue(context, PrefKey.COMMON_LONGITUDE.key(), Define.DEFAULT_LONGITUDE));

        mDefaultLocation.setLatitude(DEFAULT_LATITUDE);
        mDefaultLocation.setLongitude(DEFAULT_LONGITUDE);
    }

    /**
     * get CurrentLocationManager instance
     *
     * @return
     */
    public synchronized static CurrentLocationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CurrentLocationManager(context);
        }
        return mInstance;
    }

    /**
     * clear CurrentLocationManager instance
     */
    public static void clear() {
        mInstance = null;
    }

    /**
     * get current location
     *
     * @param context
     */
    public Location getCurrentLocation(Context context) {
        Location location = null;

//        if (StatusChecker.isUseLocation(context)) {
        if (IS_USE_LOCATION) {
            if (IS_USE_LAST_KNOWN_LOCATION) {
                boolean isGpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || ContextCompat.checkSelfPermission(mContext, android
                        .Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    if (isGpsProviderEnabled) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }

                    if (location == null && isNetworkProviderEnabled) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }

            if (location == null) {
                location = new Location(mDefaultLocation);
            }

            refreshCurrentLocation();
        } else {
            location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(DEFAULT_LATITUDE);
            location.setLongitude(DEFAULT_LONGITUDE);
        }

        return location;

    }

    /**
     * refresh current location
     */
    public void refreshCurrentLocation() {
        if (((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || ContextCompat.checkSelfPermission(mContext, android
                .Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mLocationManager.requestLocationUpdates(UPDATE_INTERVAL, MINIMUM_DISTANCE, mCriteria, mLocationListener, null);
        }
    }

    /**
     * check whether network or gps provider is enable
     *
     * @param context
     * @return
     */
    public boolean isEnableProvider(Context context) {
        boolean isGpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGpsProviderEnabled || isNetworkProviderEnabled;
    }

    /**
     * customized LocationListener
     *
     * @author hyungrok.kim
     */
    private class CurrentLocationListener implements LocationListener {
        private Context context;

        public CurrentLocationListener(Context context) {
            this.context = context;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || ContextCompat.checkSelfPermission(mContext, android
                    .Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                mLocationManager.removeUpdates(this);
            }

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (latitude > 0 && longitude > 0) {
//                if (context != null) {
//                    Preference.COMMON.setValue(context, PrefKey.COMMON_LATITUE.key(), latitude);
//                    Preference.COMMON.setValue(context, PrefKey.COMMON_LONGITUDE.key(), longitude);
//                }

                Log.v("VV", "onLocationChanged() / latitude = " + latitude);
                Log.v("VV", "onLocationChanged() / longitude = " + longitude);

                mDefaultLocation.setProvider(location.getProvider());
                mDefaultLocation.setLatitude(latitude);
                mDefaultLocation.setLongitude(longitude);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }
    }
}
