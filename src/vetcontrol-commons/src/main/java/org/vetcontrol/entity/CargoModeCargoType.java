/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "cargo_mode_cargo_type")
public class CargoModeCargoType implements Serializable {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "cargo_mode_id")
        private Long cargoModeId;
        @Column(name = "cargo_type_id", unique = true)
        private Long cargoTypeId;

        public Id() {
        }

        public Id(Long cargoModeId, Long cargoTypeId) {
            this.cargoModeId = cargoModeId;
            this.cargoTypeId = cargoTypeId;
        }

        public void setCargoModeId(Long cargoModeId) {
            this.cargoModeId = cargoModeId;
        }

        public void setCargoTypeId(Long cargoTypeId) {
            this.cargoTypeId = cargoTypeId;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Id) {
                Id that = (Id) o;
                return this.cargoModeId.equals(that.cargoModeId)
                        && this.cargoTypeId.equals(that.cargoTypeId);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return cargoModeId.hashCode() + cargoTypeId.hashCode();
        }
    }
    @EmbeddedId
    private Id id = new Id();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_mode_id", insertable = false, updatable = false)
    private CargoMode cargoMode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_type_id", insertable = false, updatable = false)
    private CargoType cargoType;
    @Transient
    private boolean needToUpdateVersion;

    public CargoModeCargoType() {
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(CargoMode cargoMode) {
        this.cargoMode = cargoMode;
        id.setCargoModeId(cargoMode.getId());
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
        if (cargoType != null) {
            id.setCargoTypeId(cargoType.getId());
        } else {
            id.setCargoTypeId(null);
        }
    }

    public Id getId() {
        return id;
    }

    private void setId(Id id) {
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
    public String toString() {
        return "cmId = "+id.cargoModeId + " ctId = "+id.cargoTypeId;
    }
}
