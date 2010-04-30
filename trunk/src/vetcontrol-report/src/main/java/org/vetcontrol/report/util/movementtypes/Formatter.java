/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.movementtypes;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public final class Formatter {

    private Formatter() {
    }

    public static String formatCount(Number count, String unitTypeName, Locale locale) {
        if (count == null || count.doubleValue() <= 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
    }

    public static String cargoModeName(String cargoModeName, String parentCargoModeName) {
        return parentCargoModeName + ", " + cargoModeName;
    }
}
