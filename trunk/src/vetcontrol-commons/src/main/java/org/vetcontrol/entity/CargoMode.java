/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

/**
 * 2.4.3.12 Справочник видов грузов
 * 
 * @author Artem
 */
@Entity
@Table(name = "cargo_mode")
public class CargoMode extends Localizable {

    private List<CargoModeCargoType> cargoModeCargoTypes = new ArrayList<CargoModeCargoType>();

    @OneToMany(mappedBy = "cargoMode", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    public List<CargoModeCargoType> getCargoModeCargoTypes() {
        return cargoModeCargoTypes;
    }

    public void setCargoModeCargoTypes(List<CargoModeCargoType> cargoModeCargoTypes) {
        this.cargoModeCargoTypes = cargoModeCargoTypes;
    }

    public void addCargoModeCargoType(CargoModeCargoType cargoModeCargoType) {
        cargoModeCargoType.setCargoMode(this);
        cargoModeCargoTypes.add(cargoModeCargoType);
    }
    private List<CargoModeUnitType> cargoModeUnitTypes = new ArrayList<CargoModeUnitType>();

    @OneToMany(mappedBy = "cargoMode", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    public List<CargoModeUnitType> getCargoModeUnitTypes() {
        return cargoModeUnitTypes;
    }

    public void setCargoModeUnitTypes(List<CargoModeUnitType> cargoModeUnitTypes) {
        this.cargoModeUnitTypes = cargoModeUnitTypes;
    }

    public void addCargoModeUnitType(CargoModeUnitType cargoModeUnitType) {
        cargoModeUnitType.setCargoMode(this);
        cargoModeUnitTypes.add(cargoModeUnitType);
    }
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 500, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
