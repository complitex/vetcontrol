/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.meat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Artem
 */
public final class Formatter {

    private static final String RESOURCE_BUNDLE_PATH = "org.vetcontrol.report.web.pages.MeatInDayReportPage";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);

    private Formatter() {
    }

    public static String formatReportTitleDate(Date date, Locale locale) {
        return new SimpleDateFormat("dd.MM.yyyy", locale).format(date);
    }

    public static String formatMonth(Date day, Locale locale) {
        Calendar c = Calendar.getInstance(locale);
        c.setTime(day);
        return Strings.capitalize(c.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
    }

    public static String formatPreviousMonth(Date day, Locale locale) {
        Calendar c = Calendar.getInstance(locale);
        c.setTime(day);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, -1);
        return Strings.capitalize(c.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
    }

    public static String formatCurrentDate(Date date, Locale locale) {
        return new SimpleDateFormat("dd.MM.yy", locale).format(date);
    }

    public static String formatCargoMode(String cargoModeName, boolean isTotalEntry, boolean isRootCargoMode, boolean isFirstSubCargoMode) {
        if (isTotalEntry) {
            return RESOURCE_BUNDLE.getString("report.total.name");
        } else {
            if (isRootCargoMode) {
                return cargoModeName + RESOURCE_BUNDLE.getString("report.root_suffix");
            } else if (isFirstSubCargoMode) {
                return RESOURCE_BUNDLE.getString("report.sub_prefix") + " " + cargoModeName;
            } else {
                return cargoModeName;
            }
        }
    }

    public static String formatCount(Number count, String unitTypeName, Locale locale) {
        if (count == null || count.doubleValue() <= 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
    }
}
