package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/** 
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:34:56
 *
 * Справочник единиц измерения
 */
@Entity
@Table(name = "unit_type")
@XmlRootElement
public class UnitType extends Localizable {

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
    private Long shortName;

    @Column(name = "short_name")
    public Long getShortName() {
        return shortName;
    }

    public void setShortName(Long shortName) {
        this.shortName = shortName;
    }
    private List<StringCulture> shortNames = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("shortName")
    @Column(length = 10, nullable = true)
    @XmlTransient
    public List<StringCulture> getShortNames() {
        return shortNames;
    }

    public void setShortNames(List<StringCulture> shortNames) {
        this.shortNames = shortNames;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into unit_type (id, `name`, short_name, updated, disabled) "
                + "value (:id, :name, :short_name, :updated, :disabled)").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("short_name", shortName).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update unit_type set `name` = :name, short_name = :short_name, "
                + "updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("short_name", shortName).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }
}
