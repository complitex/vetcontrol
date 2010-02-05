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
import java.io.Serializable;
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
public class Prohibition implements Serializable {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

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
    public List<StringCulture> getTargets() {
        return targets;
    }

    public void setTargets(List<StringCulture> targets) {
        this.targets = targets;
    }
}