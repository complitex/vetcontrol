package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;
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
public class Cargo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Id
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Id
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "document_cargo_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "department_id", referencedColumnName = "department_id", insertable = false, updatable = false),
            @JoinColumn(name = "client_id", referencedColumnName = "client_id", insertable = false, updatable = false)})
    private DocumentCargo documentCargo;

    @Column(name = "document_cargo_id")
    private Long documentCargoId;

    @ManyToOne
    @JoinColumn(name = "cargo_type_id")
    private CargoType cargoType;

    @ManyToOne
    @JoinColumn(name = "unit_type_id")
    private UnitType unitType;

    @Column(name = "count")
    private Integer count;

    @Column(name = "certificate_details", length = 255)
    private String certificateDetails;

    @Temporal(TemporalType.DATE)
    @Column(name = "certificate_date")
    private Date certificateDate;

    @PrePersist @PreUpdate
    protected void preUpdate(){
        documentCargoId = documentCargo.getId();
        client = documentCargo.getClient();
        department = documentCargo.getDepartment();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cargo)) return false;

        Cargo cargo = (Cargo) o;

        if (cargoType != null ? !cargoType.equals(cargo.cargoType) : cargo.cargoType != null) return false;
        if (certificateDate != null ? !certificateDate.equals(cargo.certificateDate) : cargo.certificateDate != null)
            return false;
        if (certificateDetails != null ? !certificateDetails.equals(cargo.certificateDetails) : cargo.certificateDetails != null)
            return false;
        if (client != null ? !client.equals(cargo.client) : cargo.client != null) return false;
        if (count != null ? !count.equals(cargo.count) : cargo.count != null) return false;
        if (department != null ? !department.equals(cargo.department) : cargo.department != null) return false;
        if (documentCargo != null ? !documentCargo.equals(cargo.documentCargo) : cargo.documentCargo != null)
            return false;
        if (id != null ? !id.equals(cargo.id) : cargo.id != null) return false;
        if (unitType != null ? !unitType.equals(cargo.unitType) : cargo.unitType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (documentCargo != null ? documentCargo.hashCode() : 0);
        result = 31 * result + (cargoType != null ? cargoType.hashCode() : 0);
        result = 31 * result + (unitType != null ? unitType.hashCode() : 0);
        result = 31 * result + (count != null ? count.hashCode() : 0);
        result = 31 * result + (certificateDetails != null ? certificateDetails.hashCode() : 0);
        result = 31 * result + (certificateDate != null ? certificateDate.hashCode() : 0);
        return result;
    }
}

