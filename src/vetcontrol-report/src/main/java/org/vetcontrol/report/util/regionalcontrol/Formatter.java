/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.regionalcontrol;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.MovementTypeChoicePanel;

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

    public static String formatCargoArrived(Date cargoArrived, Locale locale) {
        return DateUtil.format(cargoArrived, locale);
    }

    public static String formatCargoType(String cargoTypeName, String cargoTypeCode) {
        return cargoTypeName + ", " + cargoTypeCode;
    }

    public static String formatCount(Number count, String unitTypeName, Locale locale) {
        if (count == null || count.doubleValue() <= 0) {
            return "";
        }
        return NumberFormat.getInstance(locale).format(count) + " " + unitTypeName;
    }

    public static String formatCargoReceiver(String cargoReceiverName, String cargoReceiverAddress) {
        return cargoReceiverAddress + ", " + cargoReceiverName;
    }

    public static String formatMovementType(String fromDb, Locale locale) {
        return MovementTypeChoicePanel.getDisplayName(MovementType.valueOf(fromDb), locale);
    }
}
