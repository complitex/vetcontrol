/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.entity;

import org.vetcontrol.report.commons.entity.Ordered;
import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class MovementTypesReport extends Ordered implements Serializable {

    private String parentCargoModeName;
    private String cargoModeName;
    private String unitTypeName;
    private Number export;
    private Number imprt;
    private Number transit;
    private Number importTransit;
    private Number exportInCurrentMonth;
    private Number imprtInCurrentMonth;
    private Number transitInCurrentMonth;
    private Number importTransitInCurrentMonth;

    public String getParentCargoModeName() {
        return parentCargoModeName;
    }

    public void setParentCargoModeName(String parentCargoModeName) {
        this.parentCargoModeName = parentCargoModeName;
    }

    public String getCargoModeName() {
        return cargoModeName;
    }

    public void setCargoModeName(String cargoModeName) {
        this.cargoModeName = cargoModeName;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public Number getExport() {
        return export;
    }

    public void setExport(Number export) {
        this.export = export;
    }

    public Number getExportInCurrentMonth() {
        return exportInCurrentMonth;
    }

    public void setExportInCurrentMonth(Number exportInCurrentMonth) {
        this.exportInCurrentMonth = exportInCurrentMonth;
    }

    public Number getImportTransit() {
        return importTransit;
    }

    public void setImportTransit(Number importTransit) {
        this.importTransit = importTransit;
    }

    public Number getImportTransitInCurrentMonth() {
        return importTransitInCurrentMonth;
    }

    public void setImportTransitInCurrentMonth(Number importTransitInCurrentMonth) {
        this.importTransitInCurrentMonth = importTransitInCurrentMonth;
    }

    public Number getImprt() {
        return imprt;
    }

    public void setImprt(Number imprt) {
        this.imprt = imprt;
    }

    public Number getImprtInCurrentMonth() {
        return imprtInCurrentMonth;
    }

    public void setImprtInCurrentMonth(Number imprtInCurrentMonth) {
        this.imprtInCurrentMonth = imprtInCurrentMonth;
    }

    public Number getTransit() {
        return transit;
    }

    public void setTransit(Number transit) {
        this.transit = transit;
    }

    public Number getTransitInCurrentMonth() {
        return transitInCurrentMonth;
    }

    public void setTransitInCurrentMonth(Number transitInCurrentMonth) {
        this.transitInCurrentMonth = transitInCurrentMonth;
    }
}
