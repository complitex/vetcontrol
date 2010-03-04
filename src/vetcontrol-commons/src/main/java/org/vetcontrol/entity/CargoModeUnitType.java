/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "cargo_mode_unit_type")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CargoModeUnitType implements IQuery, IUpdated, IEmbeddedId<CargoModeUnitType.Id> {

    @Embeddable
    @XmlType
    public static class Id implements Serializable {

        @Column(name = "cargo_mode_id")
        private Long cargoModeId;
        @Column(name = "unit_type_id")
        private Long unitTypeId;

        public Id() {
        }

        public Id(Long cargoModeId, Long unitTypeId) {
            this.cargoModeId = cargoModeId;
            this.unitTypeId = unitTypeId;
        }

        public void setCargoModeId(Long cargoModeId) {
            this.cargoModeId = cargoModeId;
        }

        public Long getCargoModeId() {
            return cargoModeId;
        }

        public void setUnitTypeId(Long unitTypeId) {
            this.unitTypeId = unitTypeId;
        }

        public Long getUnitTypeId() {
            return unitTypeId;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Id) {
                Id that = (Id) o;
                return this.cargoModeId.equals(that.cargoModeId)
                        && this.unitTypeId.equals(that.unitTypeId);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return 2*cargoModeId.hashCode() + 3*unitTypeId.hashCode();
        }
    }
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_mode_id", insertable = false, updatable = false)
    @XmlTransient
    private CargoMode cargoMode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_type_id", insertable = false, updatable = false)
    @XmlTransient
    private UnitType unitType;

    @Transient
    @XmlTransient
    private boolean needToUpdateVersion;

    public CargoModeUnitType() {
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(CargoMode cargoMode) {
        this.cargoMode = cargoMode;
        id.setCargoModeId(cargoMode.getId());
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
        id.setUnitTypeId(unitType.getId());
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public boolean isNeedToUpdateVersion() {
        return needToUpdateVersion;
    }

    public void setNeedToUpdateVersion(boolean needToUpdateVersion) {
        this.needToUpdateVersion = needToUpdateVersion;
    }

    @Column(name = "updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into cargo_mode_unit_type (cargo_mode_id, unit_type_id, updated) " +
                "value (:cargo_mode_id, :unit_type_id, :updated)")
                .setParameter("cargo_mode_id", id.cargoModeId)
                .setParameter("unit_type_id", id.unitTypeId)
                .setParameter("updated", updated);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update cargo_mode_unit_type set updated = :updated " +
                "where cargo_mode_id = :cargo_mode_id and unit_type_id = :unit_type_id")
                .setParameter("cargo_mode_id", id.cargoModeId)
                .setParameter("unit_type_id", id.unitTypeId)
                .setParameter("updated", updated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CargoModeUnitType)) return false;

        CargoModeUnitType that = (CargoModeUnitType) o;

        if (cargoMode != null ? !cargoMode.equals(that.cargoMode) : that.cargoMode != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (unitType != null ? !unitType.equals(that.unitType) : that.unitType != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cargoMode != null ? cargoMode.hashCode() : 0);
        result = 31 * result + (unitType != null ? unitType.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "cmId = "+id.cargoModeId + " utId = "+id.unitTypeId;
    }


}
