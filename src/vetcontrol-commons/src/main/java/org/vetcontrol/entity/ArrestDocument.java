/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "arrest_document")
@IdClass(ClientEntityId.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrestDocument extends Synchronized implements IUpdated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Id
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @XmlIDREF
    private Client client;
    @Id
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @XmlIDREF
    private Department department;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "arrest_date", nullable = false)
    private Date arrestDate;
    @Column(name = "arrest_reason_details", nullable = false)
    private String arrestReasonDetails;
    @ManyToOne
    @JoinColumn(name = "cargo_type_id", nullable = false)
    @XmlIDREF
    private CargoType cargoType;
    @ManyToOne
    @JoinColumn(name = "unit_type_id", nullable = true)
    @XmlIDREF
    private UnitType unitType;
    @Column(name = "count", nullable = false, precision = 2)
    private Double count;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;
    @Column(name = "vehicle_details", nullable = false)
    private String vehicleDetails;
    @ManyToOne
    @JoinColumn(name = "cargo_sender_country_id", nullable = false)
    private CountryBook senderCountry;
    @Column(name = "cargo_sender_name", nullable = false)
    private String senderName;
    @Column(name = "cargo_receiver_address", nullable = false)
    private String receiverAddress;
    @Column(name = "cargo_receiver_name", nullable = false)
    private String receiverName;
    @ManyToOne
    @JoinColumn(name = "passing_border_point_id", nullable = false)
    @XmlIDREF
    private PassingBorderPoint passingBorderPoint;
    @ManyToOne
    @JoinColumn(name = "cargo_mode_id", nullable = false)
    @XmlIDREF
    private CargoMode cargoMode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "document_cargo_created", nullable = false)
    private Date documentCargoCreated;

    public Date getArrestDate() {
        return arrestDate;
    }

    public void setArrestDate(Date arrestDate) {
        this.arrestDate = arrestDate;
    }

    public String getArrestReasonDetails() {
        return arrestReasonDetails;
    }

    public void setArrestReasonDetails(String arrestReasonDetails) {
        this.arrestReasonDetails = arrestReasonDetails;
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(CargoMode cargoMode) {
        this.cargoMode = cargoMode;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PassingBorderPoint getPassingBorderPoint() {
        return passingBorderPoint;
    }

    public void setPassingBorderPoint(PassingBorderPoint passingBorderPoint) {
        this.passingBorderPoint = passingBorderPoint;
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

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public Date getDocumentCargoCreated() {
        return documentCargoCreated;
    }

    public void setDocumentCargoCreated(Date documentCargoCreated) {
        this.documentCargoCreated = documentCargoCreated;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
