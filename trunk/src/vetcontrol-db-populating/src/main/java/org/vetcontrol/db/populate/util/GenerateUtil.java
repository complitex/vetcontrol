/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.db.populate.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import org.vetcontrol.util.DateUtil;

/**
 *
 * @author Artem
 */
public class GenerateUtil {

    private final static Random RANDOM = new Random();
    private static final char[] SYMBOLS = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx) {
            SYMBOLS[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            SYMBOLS[idx] = (char) ('a' + idx - 10);
        }
    }

    public static String generateString(int length) {
        int min = length;
        StringBuilder result = new StringBuilder(min);
        for (int i = 0; i < min; i++) {
            result.append(SYMBOLS[RANDOM.nextInt(SYMBOLS.length)]);
        }
        return result.toString();
    }

    public static Date generateDate() {
        Calendar c = Calendar.getInstance();
        c.set(DateUtil.getCurrentYear(), RANDOM.nextInt(12), RANDOM.nextInt(c.getActualMaximum(Calendar.DAY_OF_MONTH)),
                RANDOM.nextInt(24), RANDOM.nextInt(60), RANDOM.nextInt(60));
        return c.getTime();
    }

    public static Date generateFutureDate() {
        Calendar now = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        c.set(DateUtil.getCurrentYear(), RANDOM.nextInt(12 - now.get(Calendar.MONTH)) + now.get(Calendar.MONTH),
                RANDOM.nextInt(c.getActualMaximum(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH) - 1) + now.get(Calendar.DAY_OF_MONTH) + 1,
                RANDOM.nextInt(24), RANDOM.nextInt(60), RANDOM.nextInt(60));
        return c.getTime();
    }

    public static int generateInt(int n) {
        return RANDOM.nextInt(n);
    }
}
