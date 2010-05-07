/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "arrest_document")
@IdClass(ClientEntityId.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArrestDocument extends Synchronized implements IUpdated, IQuery {

    @Id
//    @TableGenerator(name = "arrest_document", table = "generator", pkColumnName = "generatorName",
//            valueColumnName = "generatorValue", allocationSize = 1, initialValue = 100,
//            uniqueConstraints = @UniqueConstraint(columnNames = {"id", "client_id", "department_id"}))
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "arrest_document")
    @GeneratedValue(generator = "EnhancedTableGenerator")
    @GenericGenerator(name = "EnhancedTableGenerator", strategy = "org.vetcontrol.hibernate.id.TableFallbackToAssignedGenerator",
    parameters = {
        @Parameter(name = "segment_value", value = "arrest_document"),
        @Parameter(name = "table_name", value = "generator"),
        @Parameter(name = "segment_column_name", value = "generatorName"),
        @Parameter(name = "value_column_name", value = "generatorValue"),
        @Parameter(name = "initial_value", value = "100"),
        @Parameter(name = "increment_size", value = "1"),
        @Parameter(name = "property", value = "id")})
    @Column(nullable = false)
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "creator_id")
    @XmlIDREF
    private User creator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "arrest_date", nullable = false)
    private Date arrestDate;

    @ManyToOne
    @JoinColumn(name = "arrest_reason_id", nullable = false)
    @XmlIDREF
    private ArrestReason arrestReason;

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

    @Column(name = "count", nullable = true, precision = 2)
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
    @XmlIDREF
    private CountryBook senderCountry;

    @Column(name = "cargo_sender_name", nullable = false)
    private String senderName;

    @Column(name = "cargo_receiver_address", nullable = false)
    private String receiverAddress;

    @Column(name = "cargo_receiver_name", nullable = false)
    private String receiverName;

    @ManyToOne
    @JoinColumn(name = "passing_border_point_id", nullable = true)
    @XmlIDREF
    private PassingBorderPoint passingBorderPoint;

    @ManyToOne
    @JoinColumn(name = "cargo_mode_id", nullable = false)
    @XmlIDREF
    private CargoMode cargoMode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "document_cargo_created", nullable = false)
    private Date documentCargoCreated;

    @Column(name = "certificate_details", length = 255, nullable = false)
    private String certificateDetails;

    @Temporal(TemporalType.DATE)
    @Column(name = "certificate_date", nullable = false)
    private Date certificateDate;

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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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

    public ArrestReason getArrestReason() {
        return arrestReason;
    }

    public void setArrestReason(ArrestReason arrestReason) {
        this.arrestReason = arrestReason;
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

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getDisplayId() {
        return (department != null ? department.getId() : "0") + "."
                + (client != null ? client.getId() : "0") + "." + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrestDocument)) return false;
        if (!super.equals(o)) return false;

        ArrestDocument that = (ArrestDocument) o;

        if (arrestDate != null ? !arrestDate.equals(that.arrestDate) : that.arrestDate != null) return false;
        if (arrestReason != null ? !arrestReason.equals(that.arrestReason) : that.arrestReason != null) return false;
        if (arrestReasonDetails != null ? !arrestReasonDetails.equals(that.arrestReasonDetails) : that.arrestReasonDetails != null)
            return false;
        if (cargoMode != null ? !cargoMode.equals(that.cargoMode) : that.cargoMode != null) return false;
        if (cargoType != null ? !cargoType.equals(that.cargoType) : that.cargoType != null) return false;
        if (certificateDate != null ? !certificateDate.equals(that.certificateDate) : that.certificateDate != null)
            return false;
        if (certificateDetails != null ? !certificateDetails.equals(that.certificateDetails) : that.certificateDetails != null)
            return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (count != null ? !count.equals(that.count) : that.count != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (documentCargoCreated != null ? !documentCargoCreated.equals(that.documentCargoCreated) : that.documentCargoCreated != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (passingBorderPoint != null ? !passingBorderPoint.equals(that.passingBorderPoint) : that.passingBorderPoint != null)
            return false;
        if (receiverAddress != null ? !receiverAddress.equals(that.receiverAddress) : that.receiverAddress != null)
            return false;
        if (receiverName != null ? !receiverName.equals(that.receiverName) : that.receiverName != null) return false;
        if (senderCountry != null ? !senderCountry.equals(that.senderCountry) : that.senderCountry != null)
            return false;
        if (senderName != null ? !senderName.equals(that.senderName) : that.senderName != null) return false;
        if (unitType != null ? !unitType.equals(that.unitType) : that.unitType != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (vehicleDetails != null ? !vehicleDetails.equals(that.vehicleDetails) : that.vehicleDetails != null)
            return false;
        if (vehicleType != that.vehicleType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (arrestDate != null ? arrestDate.hashCode() : 0);
        result = 31 * result + (arrestReason != null ? arrestReason.hashCode() : 0);
        result = 31 * result + (arrestReasonDetails != null ? arrestReasonDetails.hashCode() : 0);
        result = 31 * result + (cargoType != null ? cargoType.hashCode() : 0);
        result = 31 * result + (unitType != null ? unitType.hashCode() : 0);
        result = 31 * result + (count != null ? count.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (vehicleType != null ? vehicleType.hashCode() : 0);
        result = 31 * result + (vehicleDetails != null ? vehicleDetails.hashCode() : 0);
        result = 31 * result + (senderCountry != null ? senderCountry.hashCode() : 0);
        result = 31 * result + (senderName != null ? senderName.hashCode() : 0);
        result = 31 * result + (receiverAddress != null ? receiverAddress.hashCode() : 0);
        result = 31 * result + (receiverName != null ? receiverName.hashCode() : 0);
        result = 31 * result + (passingBorderPoint != null ? passingBorderPoint.hashCode() : 0);
        result = 31 * result + (cargoMode != null ? cargoMode.hashCode() : 0);
        result = 31 * result + (documentCargoCreated != null ? documentCargoCreated.hashCode() : 0);
        result = 31 * result + (certificateDetails != null ? certificateDetails.hashCode() : 0);
        result = 31 * result + (certificateDate != null ? certificateDate.hashCode() : 0);
        return result;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into arrest_document " +
                "(id, client_id, department_id, creator_id, arrest_date, arrest_reason_id, arrest_reason_details, " +
                "cargo_type_id, unit_type_id, `count`, updated, vehicle_type, vehicle_details, cargo_sender_country_id, " +
                "cargo_sender_name, cargo_receiver_address, cargo_receiver_name, passing_border_point_id, cargo_mode_id, " +
                "document_cargo_created, certificate_details, certificate_date, sync_status) " +
                "value (:id, :client_id, :department_id, :creator_id, :arrest_date, :arrest_reason_id, :arrest_reason_details, " +
                ":cargo_type_id, :unit_type_id, :count, :updated, :vehicle_type, :vehicle_details, :cargo_sender_country_id, " +
                ":cargo_sender_name, :cargo_receiver_address, :cargo_receiver_name, :passing_border_point_id, :cargo_mode_id, " +
                ":document_cargo_created, :certificate_details, :certificate_date, :sync_status)")
                .setParameter("id", id)
                .setParameter("client_id", client != null ? client.getId() : null)
                .setParameter("department_id", department != null ? department.getId() : null)
                .setParameter("creator_id", creator != null ? creator.getId() : null)
                .setParameter("arrest_date", arrestDate)
                .setParameter("arrest_reason_id", arrestReason != null ? arrestReason.getId() : null)
                .setParameter("arrest_reason_details", arrestReasonDetails)
                .setParameter("cargo_type_id", cargoType != null ? cargoType.getId() : null)
                .setParameter("unit_type_id", unitType != null ? unitType.getId() : null)
                .setParameter("count", count)
                .setParameter("updated", updated)
                .setParameter("vehicle_type", vehicleType != null ? vehicleType.name() : null)
                .setParameter("vehicle_details", vehicleDetails)
                .setParameter("cargo_sender_country_id", senderCountry != null ? senderCountry.getId() : null)
                .setParameter("cargo_sender_name", senderName)
                .setParameter("cargo_receiver_address", receiverAddress)
                .setParameter("cargo_receiver_name", receiverName)
                .setParameter("passing_border_point_id", passingBorderPoint != null ? passingBorderPoint.getId() : null)
                .setParameter("cargo_mode_id", cargoMode != null ? cargoMode.getId() : null)
                .setParameter("document_cargo_created", documentCargoCreated)
                .setParameter("certificate_details", certificateDetails)
                .setParameter("certificate_date", certificateDate)
                .setParameter("sync_status", syncStatus != null ? syncStatus.name() : null);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return null;
    }
}
