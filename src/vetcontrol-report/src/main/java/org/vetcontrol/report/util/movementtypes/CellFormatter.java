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
public class CellFormatter {

    public static String format(Number data, String unitTypeName, Locale locale) {
        double value = data.doubleValue();
        if (value == 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(data) + " " + unitTypeName;
    }

    public static String cargoModeName(String cargoModeName, String parentCargoModeName) {
        return parentCargoModeName + ", " + cargoModeName;
    }
}
