/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.util.cargosinday;

import org.vetcontrol.entity.VehicleType;

/**
 *
 * @author Artem
 */
public class CellFormatter {

    public static String formatExistenceData(String fromDb, VehicleType vehicleType){
        return vehicleType.name().equals(fromDb) ? "X" : "";
    }

    public static String formatCountData(Number count, String unitTypeName){
        int value = count.intValue();
        if (value == 0) {
            return "";
        }
        return value + " " + unitTypeName;
    }

}
