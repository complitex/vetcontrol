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
}
