package com.arakadds.arak.common.helper;

import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeShow {
    public static final long AVERAGE_MONTH_IN_MILLIS = DateUtils.DAY_IN_MILLIS * 30;
    private static final String TAG = "TimeShow";

    public String getDate(long time) {
        return DateFormat.format("MMM dd yyyy", new Date(time * 1000)).toString();
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";


        try {

            SimpleDateFormat df_input = new SimpleDateFormat(inputFormat);
            SimpleDateFormat df_output = new SimpleDateFormat(outputFormat);
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    public static String parseDate(String newDate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant instant = Instant.parse(newDate);
            java.util.Date date = java.util.Date.from(instant);
            SimpleDateFormat sdf3 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

            return formatedDate;
        }
        return newDate;
    }

    public String getRelationTime(long time) {
        final long now = new Date().getTime();
        final long diff = time * 1000;
        final long delta = now - diff;
        long resolution;
        if (delta <= DateUtils.MINUTE_IN_MILLIS) {
            resolution = DateUtils.SECOND_IN_MILLIS;
        } else if (delta <= DateUtils.HOUR_IN_MILLIS) {
            resolution = DateUtils.MINUTE_IN_MILLIS;
        } else if (delta <= DateUtils.DAY_IN_MILLIS) {
            resolution = DateUtils.HOUR_IN_MILLIS;
        } else if (delta <= DateUtils.WEEK_IN_MILLIS) {
            resolution = DateUtils.DAY_IN_MILLIS;
        } else if (delta <= AVERAGE_MONTH_IN_MILLIS) {
            return Integer.toString((int) (delta / DateUtils.WEEK_IN_MILLIS)) + " weeks(s) ago";
        } else if (delta <= DateUtils.YEAR_IN_MILLIS) {
            return Integer.toString((int) (delta / AVERAGE_MONTH_IN_MILLIS)) + " month(s) ago";
        } else {
            return Integer.toString((int) (delta / DateUtils.YEAR_IN_MILLIS)) + " year(s) ago";
        }
        return DateUtils.getRelativeTimeSpanString(diff, now, resolution).toString();
    }
}
