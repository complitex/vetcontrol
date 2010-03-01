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
 * Справочник оснований для задержания груза
 */
@Entity
@Table(name = "arrest_reason")
@XmlRootElement
public class ArrestReason extends Localizable{
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 50, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return getInsertQuery(em, "arrest_reason");
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return getUpdateQuery(em, "arrest_reason");
    }
}
