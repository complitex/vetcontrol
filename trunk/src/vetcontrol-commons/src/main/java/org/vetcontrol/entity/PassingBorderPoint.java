/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.sync.LongAdapter;
import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.ValidProperty;

/**
 * @author Artem
 *
 * Справочник пунктов пропуска через границу
 */
@Entity
@Table(name = "passing_border_point")
@XmlRootElement
public class PassingBorderPoint implements ILongId, IBook, IUpdated, IDisabled, IQuery {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlID
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    private String name;

    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    private Date updated;

    @ValidProperty(false)
    @Column(name = "updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    private boolean disabled;

    @ValidProperty(false)
    @Column(name = "disabled")
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into passing_border_point (id, `name`, department_id, updated, disabled) "
                + "value (:id, :name, :department_id, :updated, :disabled)").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("department_id", department.getId()).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update passing_border_point set `name` = :name, department_id = :department_id, "
                + "updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("department_id", department.getId()).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }
    private boolean needToUpdate;

    @ValidProperty(false)
    @Transient
    @XmlTransient
    public boolean isNeedToUpdate() {
        return needToUpdate;
    }

    public void setNeedToUpdate(boolean needToUpdate) {
        this.needToUpdate = needToUpdate;
    }
}
