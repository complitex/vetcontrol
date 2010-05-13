/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.book.annotation.BookReference;
import org.vetcontrol.book.annotation.MappedProperty;
import org.vetcontrol.book.annotation.ViewLength;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private Set<UnitType> unitTypes;

    @XmlTransient
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cargo_mode_unit_type",
            joinColumns = @JoinColumn(name = "cargo_mode_id", insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "unit_type_id", insertable = false, updatable = false))
    public Set<UnitType> getUnitTypes() {
        return unitTypes;
    }

    public void setUnitTypes(Set<UnitType> unitTypes) {
        this.unitTypes = unitTypes;
    }

    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 500, nullable = false)
    @XmlTransient
    @ViewLength(50)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
    private CargoMode parent;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "parent_id", nullable = true)
    @XmlIDREF
    @ViewLength(50)
    public CargoMode getParent() {
        return parent;
    }

    public void setParent(CargoMode parent) {
        this.parent = parent;
    }
}
