/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;
import org.vetcontrol.util.book.entity.annotation.ValidProperty;

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
public class Department extends Localizable {

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
    private Department parent;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "parent_id", nullable = true)
    @XmlIDREF
    public Department getParent() {
        return parent;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }
    private CustomsPoint customsPoint;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "custom_point_id", nullable = true)
    @XmlIDREF
    public CustomsPoint getCustomsPoint() {
        return customsPoint;
    }

    public void setCustomsPoint(CustomsPoint customsPoint) {
        this.customsPoint = customsPoint;
    }
    private Integer level;

    @ValidProperty(false)
    @Column(name = "level", nullable = false)
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    private List<PassingBorderPoint> passingBorderPoints = new ArrayList<PassingBorderPoint>();

    @ValidProperty(false)
    @Transient
    @XmlTransient
    public List<PassingBorderPoint> getPassingBorderPoints() {
        return passingBorderPoints;
    }

    public void setPassingBorderPoints(List<PassingBorderPoint> passingBorderPoints) {
        this.passingBorderPoints = passingBorderPoints;
    }

    public void addPassingBorderPoint(PassingBorderPoint borderPoint) {
        borderPoint.setDepartment(this);
        passingBorderPoints.add(borderPoint);
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into department (id, `name`, parent_id, custom_point_id, `level`, updated, disabled) "
                + "value (:id, :name, :parent_id, :updated, :custom_point_id, :level, :disabled)").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("parent_id", parent != null ? parent.getId() : null).
                setParameter("custom_point_id", customsPoint != null ? customsPoint.getId() : null).
                setParameter("level", level).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update department set `name` = :name, parent_id = :parent_id, "
                + "custom_point_id = :custom_point_id, `level` = :level, updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("name", name).
                setParameter("parent_id", parent != null ? parent.getId() : null).
                setParameter("custom_point_id", customsPoint != null ? customsPoint.getId() : null).
                setParameter("level", level).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        if (!super.equals(o)) return false;

        Department that = (Department) o;

        if (customsPoint != null ? !customsPoint.equals(that.customsPoint) : that.customsPoint != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (customsPoint != null ? customsPoint.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[hash: ").append(Integer.toHexString(hashCode())).append(", id: ").append(id).append(", parent_id: ").append(parent != null ? parent.getId() : null).append(", namesMap: ").append(getNamesMap()).append("]").toString();
    }
}
