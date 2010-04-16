/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util.arrest;

import java.util.Locale;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;

/**
 *
 * @author Artem
 */
public final class ExtendedFormatter {

    private ExtendedFormatter() {
    }

    public static String formatVehicleType(String fromDb, Locale locale) {
        return VehicleTypeChoicePanel.getDysplayName(VehicleType.valueOf(fromDb), locale);
    }
}
