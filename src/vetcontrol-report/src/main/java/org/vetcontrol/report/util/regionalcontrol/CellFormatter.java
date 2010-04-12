/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.regionalcontrol;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import org.vetcontrol.util.DateUtil;

/**
 *
 * @author Artem
 */
public class CellFormatter {

    public static final String CARGO_TYPE_DELIMETER = ", ";

    public static String formatCargoArrived(Date cargoArrived, Locale locale) {
        return DateUtil.format(cargoArrived, locale);
    }

    public static String formatCargoType(String cargoTypeName, String cargoTypeCode) {
        return cargoTypeName + CARGO_TYPE_DELIMETER + cargoTypeCode;
    }

    public static String formatCount(Number count, String unitTypeName, Locale locale) {
        double value = count.doubleValue();
        if (value == 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
    }
}
