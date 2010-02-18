/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.18 Справочник типов транспортных средств
 *
 * @author Artem
 */
@Entity
@Table(name = "vehicletypes")
@XmlRootElement
public class VehicleType extends Localizable{
    private List<StringCulture> names = new ArrayList<StringCulture>();          

    @Transient
    @MappedProperty("name")
    @Column(length = 20, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return getInsertQuery(em, "vehicletypes");
    }
}
