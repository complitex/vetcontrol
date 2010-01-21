/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.BookReference;

/**
 * 2.4.3.12 Справочник видов грузов
 * 
 * @author Artem
 */
@Entity
@Table(name = "cargo_mode")
public class CargoMode extends Localizable {

    private CargoType cargoType;

    /**
     * Get the value of cargoType
     *
     * @return the value of cargoType
     */
    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "cargo_type", nullable = false)
    public CargoType getCargoType() {
        return cargoType;
    }

    /**
     * Set the value of cargoType
     *
     * @param cargoType new value of cargoType
     */
    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    private UnitType unitType;

    /**
     * Get the value of unitType
     *
     * @return the value of unitType
     */
    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "unit_type", nullable = false)
    public UnitType getUnitType() {
        return unitType;
    }

    /**
     * Set the value of unitType
     *
     * @param unitType new value of unitType
     */
    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }


}
