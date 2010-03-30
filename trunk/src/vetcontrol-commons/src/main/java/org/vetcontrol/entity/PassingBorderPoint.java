/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Artem
 *
 * Справочник пунктов пропуска через границу
 */
@Entity
@Table(name = "passing_border_point")
@XmlRootElement
public class PassingBorderPoint extends Localizable{
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

    private Department department;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "department_id", nullable = false)
    @XmlIDREF
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
         return em.createNativeQuery("insert into passing_border_point (id, `name`, department_id, updated, disabled) "
                + "value (:id, :name, :department_id, :updated, :disabled)").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("department_id", department != null ? department.getId() : null).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update passing_border_point set `name` = :name, department_id = :department_id, "
                + "updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("department_id", department != null ? department.getId() : null).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

}