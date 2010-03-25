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
import java.util.Date;
import java.util.List;

/**
 * 2.4.3.6 Справочник перечня запретов по странам
 *
 * @author Artem
 */
@Entity
@Table(name = "prohibition_country")
@XmlRootElement
public class Prohibition implements ILongId, IUpdated, IQuery, IDisabled {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    private Date date;

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false, length = 10)
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    private String number;

    @Column(name = "number", nullable = false, length = 10)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    private CountryBook country;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "country_id", nullable = false)
    @XmlIDREF
    public CountryBook getCountry() {
        return country;
    }

    public void setCountry(CountryBook referencedCountry) {
        this.country = referencedCountry;
    }
    private Long reason;

    @Column(name = "reason")
    public Long getReason() {
        return reason;
    }

    public void setReason(Long reason) {
        this.reason = reason;
    }
    private List<StringCulture> reasons = new ArrayList<StringCulture>();

    @MappedProperty("reason")
    @Transient
    @Column(length = 50, nullable = false)
    @XmlTransient
    public List<StringCulture> getReasons() {
        return reasons;
    }

    public void setReasons(List<StringCulture> reasons) {
        this.reasons = reasons;
    }
    private Long region;

    @Column(name = "region")
    public Long getRegion() {
        return region;
    }

    public void setRegion(Long region) {
        this.region = region;
    }
    private List<StringCulture> regions = new ArrayList<StringCulture>();

    @MappedProperty("region")
    @Transient
    @Column(length = 50, nullable = false)
    @XmlTransient
    public List<StringCulture> getRegions() {
        return regions;
    }

    public void setRegions(List<StringCulture> regions) {
        this.regions = regions;
    }
    private Long target;

    @Column(name = "target")
    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }
    private List<StringCulture> targets = new ArrayList<StringCulture>();

    @MappedProperty("target")
    @Transient
    @Column(length = 50, nullable = false)
    @XmlTransient
    public List<StringCulture> getTargets() {
        return targets;
    }

    public void setTargets(List<StringCulture> targets) {
        this.targets = targets;
    }
    private Date updated;

    @Temporal(TemporalType.TIMESTAMP)
    @Override
    @ValidProperty(false)
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into prohibition_country (id, `date`, `number`, country_id, reason, region, target, updated, disabled) "
                + "value (:id, :date, :number, :country_id, :reason, :region, :target, :updated, :disabled)")
                .setParameter("id", id)
                .setParameter("date", date)
                .setParameter("number", number)
                .setParameter("country_id", country.getId())
                .setParameter("reason", reason)
                .setParameter("region", region)
                .setParameter("target", target)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update prohibition_country set `date` = :date, `number` = :number, country_id = :country_id, " +
                "reason = :reason, region = :region, target = :target, updated = :updated, disabled = :disabled where id = :id")
                .setParameter("id", id)
                .setParameter("date", date)
                .setParameter("number", number)
                .setParameter("country_id", country.getId())
                .setParameter("reason", reason)
                .setParameter("region", region)
                .setParameter("target", target)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prohibition)) return false;

        Prohibition that = (Prohibition) o;

        if (disabled != that.disabled) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (disabled ? 1 : 0);
        return result;
    }
}
