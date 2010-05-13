/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.document.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import org.vetcontrol.entity.ArrestDocument;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import static org.vetcontrol.web.component.VehicleTypeChoicePanel.getDysplayName;

/**
 *
 * @author Artem
 */
public class ArrestDocumentReport implements Serializable {

    private String departmentName;
    private String passingBorderPointName;
    private Date arrestDate;
    private String cargoTypeName;
    private Number count;
    private String unitTypeName;
    private String cargoSenderName;
    private String cargoSenderCountry;
    private String cargoReceiverName;
    private String cargoReceiverAddress;
    private String arrestReason;
    private String arrestReasonDetails;
    private String vehicleType;
    private String vehicleDetails;
    private Date certificateDate;
    private String certificateDetails;
    private String id;

    public Date getArrestDate() {
        return arrestDate;
    }

    public void setArrestDate(Date arrestDate) {
        this.arrestDate = arrestDate;
    }

    public String getArrestReason() {
        return arrestReason;
    }

    public void setArrestReason(String arrestReason) {
        this.arrestReason = arrestReason;
    }

    public String getArrestReasonDetails() {
        return arrestReasonDetails;
    }

    public void setArrestReasonDetails(String arrestReasonDetails) {
        this.arrestReasonDetails = arrestReasonDetails;
    }

    public String getCargoReceiverAddress() {
        return cargoReceiverAddress;
    }

    public void setCargoReceiverAddress(String cargoReceiverAddress) {
        this.cargoReceiverAddress = cargoReceiverAddress;
    }

    public String getCargoReceiverName() {
        return cargoReceiverName;
    }

    public void setCargoReceiverName(String cargoReceiverName) {
        this.cargoReceiverName = cargoReceiverName;
    }

    public String getCargoSenderCountry() {
        return cargoSenderCountry;
    }

    public void setCargoSenderCountry(String cargoSenderCountry) {
        this.cargoSenderCountry = cargoSenderCountry;
    }

    public String getCargoSenderName() {
        return cargoSenderName;
    }

    public void setCargoSenderName(String cargoSenderName) {
        this.cargoSenderName = cargoSenderName;
    }

    public String getCargoTypeName() {
        return cargoTypeName;
    }

    public void setCargoTypeName(String cargoTypeName) {
        this.cargoTypeName = cargoTypeName;
    }

    public Number getCount() {
        return count;
    }

    public void setCount(Number count) {
        this.count = count;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPassingBorderPointName() {
        return passingBorderPointName;
    }

    public void setPassingBorderPointName(String passingBorderPointName) {
        this.passingBorderPointName = passingBorderPointName;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public Date getCertificateDate() {
        return certificateDate;
    }

    public void setCertificateDate(Date certificateDate) {
        this.certificateDate = certificateDate;
    }

    public String getCertificateDetails() {
        return certificateDetails;
    }

    public void setCertificateDetails(String certificateDetails) {
        this.certificateDetails = certificateDetails;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrestDocumentReport() {
    }

    public ArrestDocumentReport(ArrestDocument arrestDocument, Locale reportLocale) {
        setArrestDate(arrestDocument.getArrestDate());
        setArrestReason(getLocalizablePropertyAsStringInLocale(arrestDocument.getArrestReason().getNames(), reportLocale));
        setArrestReasonDetails(arrestDocument.getArrestReasonDetails());
        setCargoReceiverAddress(arrestDocument.getReceiverAddress());
        setCargoReceiverName(arrestDocument.getReceiverName());
        setCargoSenderCountry(getLocalizablePropertyAsStringInLocale(arrestDocument.getSenderCountry().getNames(), reportLocale));
        setCargoSenderName(arrestDocument.getSenderName());
        setCargoTypeName(getLocalizablePropertyAsStringInLocale(arrestDocument.getCargoType().getNames(), reportLocale));
        setDepartmentName(getLocalizablePropertyAsStringInLocale(arrestDocument.getDepartment().getNames(), reportLocale));
        setPassingBorderPointName(arrestDocument.getPassingBorderPoint().getName());
        setCount(arrestDocument.getCount());
        setUnitTypeName(getLocalizablePropertyAsStringInLocale(arrestDocument.getUnitType().getShortNames(), reportLocale));
        setVehicleType(getDysplayName(arrestDocument.getVehicleType(), reportLocale));
        setVehicleDetails(arrestDocument.getVehicleDetails());
        setCertificateDate(arrestDocument.getCertificateDate());
        setCertificateDetails(arrestDocument.getCertificateDetails());
        setId(arrestDocument.getDisplayId());
    }
}
