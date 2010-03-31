package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 15:11:00
 *
 * Сущность объектно-реляционного отображения груза
 */
@Entity
@Table(name = "cargo")
@IdClass(ClientEntityId.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Cargo extends Synchronized implements IUpdated{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_cargo_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "department_id", referencedColumnName = "department_id", insertable = false, updatable = false),
            @JoinColumn(name = "client_id", referencedColumnName = "client_id", insertable = false, updatable = false)})
    @XmlTransient
    private DocumentCargo documentCargo;

    @Column(name = "document_cargo_id")
    private Long documentCargoId;

    @ManyToOne
    @JoinColumn(name = "cargo_type_id")
    @XmlIDREF
    private CargoType cargoType;

    @ManyToOne
    @JoinColumn(name = "unit_type_id")
    @XmlIDREF
    private UnitType unitType;

    @Column(name = "count", nullable=true)
    private Integer count;

    @Column(name = "certificate_details", length = 255)
    private String certificateDetails;

    @Temporal(TemporalType.DATE)
    @Column(name = "certificate_date")
    private Date certificateDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_producer_id")
    @XmlIDREF
    private CargoProducer cargoProducer;

    @ManyToOne(optional=true)
    @JoinColumns({
        @JoinColumn(name="vehicle_id"),
        @JoinColumn(name="department_id"),
        @JoinColumn(name="client_id")
    })
    @XmlIDREF
    private Vehicle vehicle;

    @PrePersist @PreUpdate
    protected void preUpdate(){
        if (documentCargo != null){
            documentCargoId = documentCargo.getId();
            client = documentCargo.getClient();
            department = documentCargo.getDepartment();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public DocumentCargo getDocumentCargo() {
        return documentCargo;
    }

    public void setDocumentCargo(DocumentCargo documentCargo) {
        this.documentCargo = documentCargo;
    }

    public Long getDocumentCargoId() {
        return documentCargoId;
    }

    public void setDocumentCargoId(Long documentCargoId) {
        this.documentCargoId = documentCargoId;
    }


    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCertificateDetails() {
        return certificateDetails;
    }

    public void setCertificateDetails(String certificateDetails) {
        this.certificateDetails = certificateDetails;
    }

    public Date getCertificateDate() {
        return certificateDate;
    }

    public void setCertificateDate(Date certificateDate) {
        this.certificateDate = certificateDate;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public CargoProducer getCargoProducer() {
        return cargoProducer;
    }

    public void setCargoProducer(CargoProducer cargoProducer) {
        this.cargoProducer = cargoProducer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}

