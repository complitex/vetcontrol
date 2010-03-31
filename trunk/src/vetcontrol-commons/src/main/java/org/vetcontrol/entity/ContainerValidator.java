/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "container_validator")
public class ContainerValidator extends Localizable {

    public static final String CONTAINER_CODE_FORMAT = "XXXX999999-9";
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 100, nullable = false)
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
    @Column(length = 50, nullable = true)
    @XmlTransient
    public List<StringCulture> getShortNames() {
        return shortNames;
    }

    public void setShortNames(List<StringCulture> shortNames) {
        this.shortNames = shortNames;
    }
    private String prefix;

    @Column(name = "prefix", nullable = false, length = 4)
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into container_validator (id, carrier_abbr, carrier_name, `prefix` updated, disabled) "
                + "value (:id, :carrier_abbr, :carrier_name, :prefix, :updated, :disabled)").
                setParameter("id", id).
                setParameter("carrier_name", name).
                setParameter("carrier_abbr", shortName).
                setParameter("prefix", prefix).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update container_validator set carrier_abbr = :carrier_abbr, carrier_name = :carrier_name, "
                + "`prefix` = :prefix, updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("carrier_name", name).
                setParameter("carrier_abbr", shortName).
                setParameter("prefix", prefix).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }
}
