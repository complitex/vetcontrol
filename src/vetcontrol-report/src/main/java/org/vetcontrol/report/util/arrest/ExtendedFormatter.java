/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.arrest;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.util.string.Strings;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;

/**
 *
 * @author Artem
 */
public final class ExtendedFormatter {

    private ExtendedFormatter() {
    }

    public static String formatVehicleType(String fromDb, Locale locale) {
        return VehicleTypeChoicePanel.getDisplayName(VehicleType.valueOf(fromDb), locale);
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
        if (count == null || count.doubleValue() <= 0) {
            countAndUnitType = "";
        } else {
            countAndUnitType = NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
        }
        String result = cargoTypeName;
        if (!Strings.isEmpty(countAndUnitType)) {
            result += ", " + countAndUnitType;
        }
        return result;
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
