/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class MeatInDayReport implements Serializable {

    private String cargoModeName;
    private Number level;
    private Number inPreviousMonth;
    private Number inCurrentMonth;
    private Number inCurrentDate;
    private Number total;
    private boolean firstSubCargoMode;
    private String unitTypeName;

    public String getCargoModeName() {
        return cargoModeName;
    }

    public void setCargoModeName(String cargoModeName) {
        this.cargoModeName = cargoModeName;
    }

    public boolean isFirstSubCargoMode() {
        return firstSubCargoMode;
    }

    public void setFirstSubCargoMode(boolean firstSubCargoMode) {
        this.firstSubCargoMode = firstSubCargoMode;
    }

    public Number getInCurrentDate() {
        return inCurrentDate;
    }

    public void setInCurrentDate(Number inCurrentDate) {
        this.inCurrentDate = inCurrentDate;
    }

    public Number getInCurrentMonth() {
        return inCurrentMonth;
    }

    public void setInCurrentMonth(Number inCurrentMonth) {
        this.inCurrentMonth = inCurrentMonth;
    }

    public Number getInPreviousMonth() {
        return inPreviousMonth;
    }

    public void setInPreviousMonth(Number inPreviousMonth) {
        this.inPreviousMonth = inPreviousMonth;
    }

    public Number getLevel() {
        return level;
    }

    public void setLevel(Number isSubCargoMode) {
        this.level = isSubCargoMode;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public Number getTotal() {
        return total;
    }

    public void setTotal(Number total) {
        this.total = total;
    }

    //only for Hibernate transformer needs
    public void setFamilyId(Number familyId) {
    }

    /* Helper properties(not from db query resultset) */
    public boolean isRootCargoMode() {
        return getLevel().intValue() == 0;
    }

    public boolean isTotalEntry() {
        return getLevel().intValue() == -1;
    }
}
