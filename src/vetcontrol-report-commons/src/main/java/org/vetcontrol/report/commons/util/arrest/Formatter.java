/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.util.arrest;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.vetcontrol.util.DateUtil;

/**
 *
 * @author Artem
 */
public final class Formatter {

    private Formatter() {
    }

    public static String formatReportTitleDate(Date date, Locale locale) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
        return dateFormat.format(date);
    }

    public static String formatArrestDate(Date date, Locale locale) {
        return DateUtil.format(date, locale);
    }

    public static String formatCargoInfo(String cargoTypeName, Number count, String unitTypeName, Locale locale) {
        String countAndUnitType = null;
        double value = count.doubleValue();
        if (value == 0) {
            countAndUnitType = "";
        } else {
            countAndUnitType = NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
        }
        return cargoTypeName + ", " + countAndUnitType;
    }

    public static String formatCargoSender(String cargoSenderName, String cargoSenderCountry) {
        return cargoSenderCountry + ", " + cargoSenderName;
    }

    public static String formatCargoReceiver(String cargoReceiverName, String cargoReceiverAddress) {
        return cargoReceiverAddress + ", " + cargoReceiverName;
    }

    public static String formatArrestReason(String arrestReason, String arrestReasonDetails) {
        return arrestReason + ". " + arrestReasonDetails;
    }

    public static String formatDocumentCargoCreatedDate(Date date, Locale locale) {
        return DateUtil.format(date, locale);
    }
}
