/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.document.util.arrest;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Artem
 */
public final class Formatter {

    private Formatter() {
    }

    public static String formatVehicleType(String vehicleType) {
        if (Strings.isEmpty(vehicleType)) {
            return "";
        }
        char first = vehicleType.charAt(0);
        return Character.toLowerCase(first) + vehicleType.substring(1);
    }

    public static String formatArrestDate(Date date, Locale locale) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
        return dateFormat.format(date);
    }

    public static String formatCount(Number count, String unitTypeName, Locale locale) {
        String countAndUnitType = null;
        double value = count.doubleValue();
        if (value == 0) {
            countAndUnitType = "";
        } else {
            countAndUnitType = NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
        }
        return countAndUnitType;
    }

    public static String formatCargoSender(String cargoSenderName, String cargoSenderCountry) {
        return cargoSenderName + ", " + cargoSenderCountry;
    }

    public static String formatCargoReceiver(String cargoReceiverName, String cargoReceiverAddress) {
        return cargoReceiverName + ", " + cargoReceiverAddress;
    }

    public static String formatArrestReason(String arrestReason, String arrestReasonDetails) {
        return arrestReason + ". " + arrestReasonDetails;
    }

    public static String formatCertificate(Date certificateDate, String certificateDetails, Locale locale) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
        return certificateDetails + " " + dateFormat.format(certificateDate);
    }
}
