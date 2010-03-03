package org.vetcontrol.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.01.2010 0:30:49
 */
public class DateUtil {

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar c = Calendar.getInstance();

        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return c.getTime();
    }

    public static String getTimeDiff(long start, long end) {
        long time = end - start;

        long msec = time % 1000;
        time = time / 1000;
        long sec = time % 60;
        time = time / 60;
        long min = time % 60;
        time = time / 60;
        long hour = time;

        return String.format("%d:%02d:%02d.%03d", hour, min, sec, msec);
    }

    public static Date getFirstDateOfYear() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return c.getTime();
    }

    public static Date getLastDateOfMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), month + 1, 1);
        c.add(Calendar.DATE, -1);
        return getEndOfDay(c.getTime());
    }

    public static String getDisplayMonth(int month, Locale locale) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), month, 1);
        return c.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
    }

    public static String format(Date date, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        return dateFormat.format(date);
    }

    public static int getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth(){
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static Date getBeginOfDay(Date date){
        Calendar c = Calendar.getInstance();

        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }
}
