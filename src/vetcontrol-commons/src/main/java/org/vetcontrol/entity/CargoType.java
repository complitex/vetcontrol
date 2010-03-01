package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:31:39
 *
 * тип груза
 */
@Entity
@Table(name = "cargo_type")
@XmlRootElement
public class CargoType extends Localizable {

    private String uktZedCode;

    @Column(name = "ukt_zed_code", nullable = false, length = 10, unique = true)
    public String getCode() {
        return uktZedCode;
    }

    public void setCode(String uktZedCode) {
        this.uktZedCode = uktZedCode;
    }
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 500, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into cargo_type (id, `name`, ukt_zed_code, updated, disabled) " +
                "value (:id, :name, :ukt_zed_code, :updated, :disabled)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("updated", updated)
                .setParameter("ukt_zed_code", uktZedCode)
                .setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em){
        return em.createNativeQuery("update cargo_type set `name` = :name, ukt_zed_code = :ukt_zed_code, " +
                "updated = :updated, disabled = :disabled where id = :id")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("updated", updated)
                .setParameter("ukt_zed_code", uktZedCode)
                .setParameter("disabled", disabled);
    }

    @Override
    public String toString() {
        return getCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CargoType) {
            CargoType that = (CargoType) obj;
            if (getId() != null) {
                return getId().equals(that.getId());
            } else {
                return that.getId() == null;
            }
        }
        return false;
    }
}
