package org.vetcontrol.document.service;

import org.vetcontrol.entity.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.04.2010 19:51:44
 */
public class ArrestDocumentFilter implements Serializable {
    private String id;
    private User creator;
    private Department department;
    private boolean childDepartments;
    private Locale currentLocale;
    private Locale systemLocale;
    private Synchronized.SyncStatus syncStatus;
    private Date arrestDate;
    private ArrestReason arrestReason;
    private String cargoType;
    private String cargoMode;
    private String count;
    private UnitType unitType;
    private VehicleType vehicleType;
    private CountryBook senderCountry;
    private String senderName;
    private String receiverAddress;
    private String receiverName;

    public ArrestDocumentFilter(Locale currentLocale, Locale systemLocale) {
        this.currentLocale = currentLocale;
        this.systemLocale = systemLocale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }



    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean isChildDepartments() {
        return childDepartments;
    }

    public void setChildDepartments(boolean childDepartments) {
        this.childDepartments = childDepartments;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Locale getSystemLocale() {
        return systemLocale;
    }

    public void setSystemLocale(Locale systemLocale) {
        this.systemLocale = systemLocale;
    }

    public Synchronized.SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Synchronized.SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Date getArrestDate() {
        return arrestDate;
    }

    public void setArrestDate(Date arrestDate) {
        this.arrestDate = arrestDate;
    }

    public ArrestReason getArrestReason() {
        return arrestReason;
    }

    public void setArrestReason(ArrestReason arrestReason) {
        this.arrestReason = arrestReason;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public String getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(String cargoMode) {
        this.cargoMode = cargoMode;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public CountryBook getSenderCountry() {
        return senderCountry;
    }

    public void setSenderCountry(CountryBook senderCountry) {
        this.senderCountry = senderCountry;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
