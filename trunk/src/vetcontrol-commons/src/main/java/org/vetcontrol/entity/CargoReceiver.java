/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

/**
 * получатели грузов
 *
 * @author Artem
 */
//@Entity
//@Table(name = "cargo_receiver")
public class CargoReceiver {

    private Integer id;
    private Long name;
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name")
    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

    @Transient
    @MappedProperty("name")
    @Column(length = 20, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
