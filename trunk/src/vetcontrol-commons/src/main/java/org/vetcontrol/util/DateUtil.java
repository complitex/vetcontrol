package org.vetcontrol.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.01.2010 0:30:49
 */
public class DateUtil {

    public static Date getCurrentDate(){
        return Calendar.getInstance().getTime();
    }

    public static Date getEndOfDay(Date date){
        Calendar c = Calendar.getInstance();

        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return c.getTime();
    }

    public static String getTimeDiff(long start, long end){
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
}
