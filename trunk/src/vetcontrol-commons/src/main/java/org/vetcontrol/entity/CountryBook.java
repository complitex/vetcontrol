package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * Справочник стран
 *
 */
@Entity
@Table(name = "countrybook")
@XmlRootElement
public class CountryBook extends Localizable {

    private String code;

    public CountryBook() {
    }

    public CountryBook(String code) {
        this.code = code;
    }

    @Column(name = "code", nullable = false, length = 2)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 10, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return this.names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    public void addName(StringCulture name) {
        names.add(name);
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into countrybook (id, `name`, code, updated) " +
                "value (:id, :name, :code, :updated)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("code", code)
                .setParameter("updated", updated);
    }
}


