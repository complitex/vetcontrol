package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlIDREF;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.BookReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:41:47
 *
 * Справочник производителей
 */

@Entity
@Table(name = "cargo_producer")
@XmlRootElement
public class CargoProducer extends Localizable{
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 50, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    private CountryBook country;

    @BookReference(referencedProperty="names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "country_id", nullable = false)
    @XmlIDREF
    public CountryBook getCountry() {
        return country;
    }

    public void setCountry(CountryBook country) {
        this.country = country;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into cargo_producer (id, `name`, country_id, updated, disabled) "
                + "value (:id, :name, :country_id, :updated, :disabled)").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("country_id", country.getId()).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update cargo_producer set `name` = :name, country_id = :country_id, "
                + "updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("country_id", country.getId()).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }
}
