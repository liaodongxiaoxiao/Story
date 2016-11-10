package com.ldxx.story.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangzhuo-neu
 * on 2016/9/13.
 */

public class DateUtils {

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }
}
