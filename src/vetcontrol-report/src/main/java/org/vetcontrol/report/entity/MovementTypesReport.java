/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.entity;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author Artem
 */
public class MovementTypesReport implements Serializable {

    private String cargoModeName;
    private String unitTypeName;

    private BigInteger export;
    private BigInteger imprt;
    private BigInteger transit;
    private BigInteger importTransit;

    private BigInteger exportInCurrentMonth;
    private BigInteger imprtInCurrentMonth;
    private BigInteger transitInCurrentMonth;
    private BigInteger importTransitInCurrentMonth;

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

    public BigInteger getExport() {
        return export;
    }

    public void setExport(BigInteger export) {
        this.export = export;
    }

    public BigInteger getExportInCurrentMonth() {
        return exportInCurrentMonth;
    }

    public void setExportInCurrentMonth(BigInteger exportInCurrentMonth) {
        this.exportInCurrentMonth = exportInCurrentMonth;
    }

    public BigInteger getImportTransit() {
        return importTransit;
    }

    public void setImportTransit(BigInteger importTransit) {
        this.importTransit = importTransit;
    }

    public BigInteger getImportTransitInCurrentMonth() {
        return importTransitInCurrentMonth;
    }

    public void setImportTransitInCurrentMonth(BigInteger importTransitInCurrentMonth) {
        this.importTransitInCurrentMonth = importTransitInCurrentMonth;
    }

    public BigInteger getImprt() {
        return imprt;
    }

    public void setImprt(BigInteger imprt) {
        this.imprt = imprt;
    }

    public BigInteger getImprtInCurrentMonth() {
        return imprtInCurrentMonth;
    }

    public void setImprtInCurrentMonth(BigInteger imprtInCurrentMonth) {
        this.imprtInCurrentMonth = imprtInCurrentMonth;
    }

    public BigInteger getTransit() {
        return transit;
    }

    public void setTransit(BigInteger transit) {
        this.transit = transit;
    }

    public BigInteger getTransitInCurrentMonth() {
        return transitInCurrentMonth;
    }

    public void setTransitInCurrentMonth(BigInteger transitInCurrentMonth) {
        this.transitInCurrentMonth = transitInCurrentMonth;
    }

}
