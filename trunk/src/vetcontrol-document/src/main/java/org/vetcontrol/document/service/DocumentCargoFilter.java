package org.vetcontrol.document.service;

import org.vetcontrol.entity.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.01.2010 22:07:57
 */
public class DocumentCargoFilter implements Serializable {

    private String id;
    private User creator;
    private Date created;
    private Department department;
    private boolean childDepartments;
    private MovementType movementType;
    private VehicleType vehicleType;
    private CountryBook senderCountry;
    private String senderName;
    private String receiverAddress;
    private String receiverName;
    private String detentionDetails;
    private String details;
    private Locale currentLocale;
    private Locale systemLocale;
    private Synchronized.SyncStatus syncStatus;

    public DocumentCargoFilter(Locale currentLocale, Locale systemLocale) {
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDetentionDetails() {
        return detentionDetails;
    }

    public void setDetentionDetails(String detentionDetails) {
        this.detentionDetails = detentionDetails;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
    
}
