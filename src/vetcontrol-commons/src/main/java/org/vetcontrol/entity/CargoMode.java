/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.12 Справочник видов грузов
 * 
 * @author Artem
 */
@Entity
@Table(name = "cargo_mode")
@XmlRootElement
public class CargoMode extends Localizable {

    private List<CargoModeCargoType> cargoModeCargoTypes = new ArrayList<CargoModeCargoType>();

    @OneToMany(mappedBy = "cargoMode", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @XmlTransient
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
    @XmlTransient
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
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return getInsertQuery(em, "cargo_mode");
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return getUpdateQuery(em, "cargo_mode");
    }
}
