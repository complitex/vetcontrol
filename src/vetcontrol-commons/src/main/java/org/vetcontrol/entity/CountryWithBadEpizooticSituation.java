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
 * Справочник стран с неблагоприятной эпизоотической ситуацией
 */
@Entity
@Table(name = "bad_epizootic_situation")
@XmlRootElement
public class CountryWithBadEpizooticSituation extends Localizable{   
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 10, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return getInsertQuery(em, "bad_epizootic_situation");
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return getUpdateQuery(em, "bad_epizootic_situation");
    }
}
