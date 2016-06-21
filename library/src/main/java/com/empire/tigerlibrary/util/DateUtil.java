package com.empire.tigerlibrary.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.empire.tigerlibrary.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * useful class composed by methods for Date and Time
 *
 * @author lordvader
 */
public class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_ONLY_HOUR = "HH";
    public static final int CHECK_NIGHT_FROM_HOUR = 19;
    public static final int CHECK_NIGHT_TO_HOUR = 5;

    /**
     * get current day's or after day's beginning and end time
     *
     * @param days
     * @return
     */
    public static long[] getDayBeginNEndTime(int days) {
        long[] dayTimes = new long[2];

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK) + days);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        dayTimes[0] = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        dayTimes[1] = calendar.getTimeInMillis();

        return dayTimes;
    }

    /**
     * get current day's or after day's end time
     *
     * @param days
     * @return
     */
    public static long getDayEndTime(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK) + days);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    /**
     * check whether such time is afternoon
     *
     * @param dateInMilliseconds
     * @return
     */
    public static boolean isAfternoon(Context context, long dateInMilliseconds) {
        String rawTime = DateFormat.format("a", dateInMilliseconds).toString();

        if (Utils.isKoreanLocale(context)) {
            return "오후".equals(rawTime);
        } else {
            return "PM".equals(rawTime);
        }
    }

    /**
     * check whether such time is night (handle Milliseconds)
     *
     * @param dateInMilliseconds
     * @return
     */
    public static boolean isNight(long dateInMilliseconds) {
        return isNight(getMillisToTime(dateInMilliseconds, DATE_FORMAT_ONLY_HOUR));
    }

    /**
     * check whether such time is night (handle String type hour)
     *
     * @param rawHour
     * @return
     */
    public static boolean isNight(String rawHour) {
        if (!Utils.isEmptyString(rawHour)) {
            int hour = Integer.parseInt(rawHour);
            return hour >= CHECK_NIGHT_FROM_HOUR || hour < CHECK_NIGHT_TO_HOUR;
        } else {
            return false;
        }
    }

    /**
     * get specific date with added day and settting hour
     *
     * @param addedDay
     * @param sethour
     * @return
     */
    public static long getSpecificDate(int addedDay, int sethour, int setMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + addedDay);
        calendar.set(Calendar.HOUR_OF_DAY, sethour);
        calendar.set(Calendar.MINUTE, setMinute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * get today's day of week
     *
     * @param context
     * @return
     */
    public static String getTodaysDayOfWeek(Context context) {
        Calendar calendar = Calendar.getInstance();
        final String[] WEEKS = context.getResources().getStringArray(R.array.day_of_week);
        return WEEKS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * get day of week after days
     *
     * @param context
     * @return
     */
    public static String getDayOfWeekAfterDay(Context context, int afterDays) {
        final String[] WEEKS = context.getResources().getStringArray(R.array.day_of_week);

        Calendar calendar = Calendar.getInstance();
        return WEEKS[(calendar.get(Calendar.DAY_OF_WEEK) - 1 + afterDays) % (WEEKS.length)];
    }

    /**
     * convert date (format : Define.DATE_FORMAT) to millisecond
     *
     * @param rawDate
     * @return
     */
    public static long getTimeToMillis(String rawDate) {
        if (!Utils.isEmptyString(rawDate)) {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

            try {
                Date date = formatter.parse(rawDate);
                return date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * convert millisecond to date (format : Define.DATE_FORMAT)
     *
     * @param dateInMilliseconds
     * @return
     */
    public static String getMillisToTime(long dateInMilliseconds) {
        return DateFormat.format(DATE_FORMAT, dateInMilliseconds).toString();
    }

    /**
     * convert millisecond to date by applying date format
     *
     * @param dateInMilliseconds
     * @param dateFormat
     * @return
     */
    public static String getMillisToTime(long dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }

    /**
     * get hour from date
     *
     * @param rawDate
     * @return
     */
    public static int getHourFromDate(String rawDate) {
        if (!Utils.isEmptyString(rawDate)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTimeToMillis(rawDate));
            return calendar.get(Calendar.HOUR_OF_DAY);
        }

        return 0;
    }
}
