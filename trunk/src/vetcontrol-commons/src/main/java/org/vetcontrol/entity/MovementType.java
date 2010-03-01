package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.8 Справочник видов движения груза
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:14:57
 *
 */
@Entity
@Table(name = "movement_type")
@XmlRootElement
public class MovementType extends Localizable{  
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
        return getInsertQuery(em, "movement_type");
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return getUpdateQuery(em, "movement_type");
    }
}
