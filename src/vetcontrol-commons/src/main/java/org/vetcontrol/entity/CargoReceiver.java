package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artem
 *
 * Справочник получателей грузов
 */
@Entity
@Table(name = "cargo_receiver")
@XmlRootElement
public class CargoReceiver extends Localizable{
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
        return getInsertQuery(em, "cargo_receiver");
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return getUpdateQuery(em, "cargo_receiver");
    }
}
