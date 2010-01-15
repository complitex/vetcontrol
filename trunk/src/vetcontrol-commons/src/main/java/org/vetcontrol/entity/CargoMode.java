/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.12 Справочник видов грузов
 *
 * @author Artem
 */
@Entity
@Table(name = "cargo_mode")
public class CargoMode implements Serializable {

    private Integer id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    private Long name;

    @Column(name = "name")
    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 20, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
    private CargoType cargoType;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_type", nullable = false)
    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }
    private UnitType unitType;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_type", nullable = false)
    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unit) {
        this.unitType = unit;
    }
}
