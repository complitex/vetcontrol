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
public class MeatInYearReport implements Serializable {

    private String cargoModeName;
    private Number level;
    private Number total;
    private boolean firstSubCargoMode;
    private String unitTypeName;
    private Number january;
    private Number february;
    private Number march;
    private Number april;
    private Number may;
    private Number june;
    private Number july;
    private Number august;
    private Number september;
    private Number october;
    private Number november;
    private Number december;
    private Number currentDate;

    public Number getApril() {
        return april;
    }

    public void setApril(Number april) {
        this.april = april;
    }

    public Number getAugust() {
        return august;
    }

    public void setAugust(Number august) {
        this.august = august;
    }

    public String getCargoModeName() {
        return cargoModeName;
    }

    public void setCargoModeName(String cargoModeName) {
        this.cargoModeName = cargoModeName;
    }

    public Number getDecember() {
        return december;
    }

    public void setDecember(Number december) {
        this.december = december;
    }

    public Number getFebruary() {
        return february;
    }

    public void setFebruary(Number february) {
        this.february = february;
    }

    public boolean isFirstSubCargoMode() {
        return firstSubCargoMode;
    }

    public void setFirstSubCargoMode(boolean firstSubCargoMode) {
        this.firstSubCargoMode = firstSubCargoMode;
    }

    public Number getJanuary() {
        return january;
    }

    public void setJanuary(Number january) {
        this.january = january;
    }

    public Number getJuly() {
        return july;
    }

    public void setJuly(Number july) {
        this.july = july;
    }

    public Number getJune() {
        return june;
    }

    public void setJune(Number june) {
        this.june = june;
    }

    public Number getLevel() {
        return level;
    }

    public void setLevel(Number level) {
        this.level = level;
    }

    public Number getMarch() {
        return march;
    }

    public void setMarch(Number march) {
        this.march = march;
    }

    public Number getMay() {
        return may;
    }

    public void setMay(Number may) {
        this.may = may;
    }

    public Number getNovember() {
        return november;
    }

    public void setNovember(Number november) {
        this.november = november;
    }

    public Number getOctober() {
        return october;
    }

    public void setOctober(Number october) {
        this.october = october;
    }

    public Number getSeptember() {
        return september;
    }

    public void setSeptember(Number september) {
        this.september = september;
    }

    public Number getTotal() {
        return total;
    }

    public void setTotal(Number total) {
        this.total = total;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public Number getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Number currentDate) {
        this.currentDate = currentDate;
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
