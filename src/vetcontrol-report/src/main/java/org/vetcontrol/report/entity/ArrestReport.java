/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.entity;

import java.io.Serializable;
import java.util.Date;
import org.vetcontrol.report.commons.entity.Ordered;

/**
 *
 * @author Artem
 */
public class ArrestReport extends Ordered implements Serializable{

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
    private Date documentCargoCreated;

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

    public Date getDocumentCargoCreated() {
        return documentCargoCreated;
    }

    public void setDocumentCargoCreated(Date documentCargoCreated) {
        this.documentCargoCreated = documentCargoCreated;
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
}
