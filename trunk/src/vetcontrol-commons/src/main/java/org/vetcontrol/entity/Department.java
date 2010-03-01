/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.1 Справочник структурных единиц
 *
 * @author Artem
 */
@Entity
@Table(name = "department")
@XmlRootElement
public class Department extends Localizable{    
    private List<StringCulture> names = new ArrayList<StringCulture>();

    /**
     * Get the value of names
     *
     * @return the value of names
     */
    @Transient
    @MappedProperty("name")
    @Column(length = 50, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    /**
     * Set the value of names
     *
     * @param names new value of names
     */
    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
    private Department parent;

    /**
     * Get the value of parent
     *
     * @return the value of parent
     */
    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "parent_id", nullable = true)
    @XmlIDREF
    public Department getParent() {
        return parent;
    }

    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public void setParent(Department parent) {
        this.parent = parent;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into department (id, `name`, parent_id, updated, disabled) " +
                "value (:id, :name, :parent_id, :updated, :disabled)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("parent_id", parent != null ? parent.getId() : null)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em){
        return em.createNativeQuery("update department set `name` = :name, parent_id = :parent_id, " +
                "updated = :updated, disabled = :disabled where id = :id")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("parent_id", parent != null ? parent.getId() : null)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("[hash: ").append(Integer.toHexString(hashCode()))
                .append(", id: ").append(id)
                .append(", parent_id: ").append(parent != null ? parent.getId() : null)
                .append(", namesMap: ").append(getNamesMap())
                .append("]").toString();
    }
}
