package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 15:11:00
 */
@Entity
@Table(name = "cargo")
public class Cargo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_cargo_id")
    private DocumentCargo documentCargo;

    @ManyToOne
    @JoinColumn(name = "cargo_type_id")
    private CargoType cargoType;

    @ManyToOne
    @JoinColumn(name = "cargo_mode_id")
    private CargoMode cargoMode;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentCargo getDocumentCargo() {
        return documentCargo;
    }

    public void setDocumentCargo(DocumentCargo documentCargo) {
        this.documentCargo = documentCargo;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(CargoMode cargoMode) {
        this.cargoMode = cargoMode;
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
}

