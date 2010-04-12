/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.cargosinday;

import java.text.NumberFormat;
import java.util.Locale;
import org.vetcontrol.entity.VehicleType;

/**
 *
 * @author Artem
 */
public final class CellFormatter {

    private CellFormatter() {
    }

    public static String formatExistenceData(String fromDb, VehicleType vehicleType) {
        return vehicleType.name().equals(fromDb) ? "X" : "";
    }

    public static String formatCountData(Number count, String unitTypeName, Locale locale) {
        double value = count.doubleValue();
        if (value == 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
    }
}
