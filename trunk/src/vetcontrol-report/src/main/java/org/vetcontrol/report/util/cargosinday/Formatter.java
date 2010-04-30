/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.cargosinday;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.vetcontrol.entity.VehicleType;

/**
 *
 * @author Artem
 */
public final class Formatter {

    private Formatter() {
    }

    public static String formatReportTitleDate(Date date, Locale locale) {
        return new SimpleDateFormat("dd.MM.yyyy", locale).format(date);
    }

    public static String formatExistenceData(String fromDb, VehicleType vehicleType) {
        return vehicleType.name().equals(fromDb) ? "X" : "";
    }

    public static String formatCount(Number count, String unitTypeName, Locale locale) {
        if (count == null || count.doubleValue() <= 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
    }

    public static String formatCargoSender(String cargoSenderName, String cargoSenderCountry) {
        return cargoSenderCountry + ", " + cargoSenderName;
    }

    public static String formatCargoReceiver(String cargoReceiverName, String cargoReceiverAddress) {
        return cargoReceiverAddress + ", " + cargoReceiverName;
    }
}
