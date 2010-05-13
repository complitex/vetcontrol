package org.vetcontrol.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.book.annotation.BookReference;
import org.vetcontrol.book.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Справочник перечня препаратов, зарегестрированных в Украине
 *
 */
@Entity
@Table(name = "registered_products")
@XmlRootElement
public class RegisteredProducts extends Localizable{
    public RegisteredProducts() {
    }

    public RegisteredProducts(String regnumber, Date date) {
        this.regnumber = regnumber;
        this.date = date;
    }

//  classificator

    private Long classificator;

    @Column(name = "classificator", nullable = false)
    public Long getClassificator() {
        return this.classificator;
    }

    public void setClassificator(Long classificator) {
        this.classificator = classificator;
    }

    private List<StringCulture> classificators = new ArrayList<StringCulture>();

    @MappedProperty("classificator")
    @Transient
    @Column(length = 100, nullable = false)
    @XmlTransient
    public List<StringCulture> getClassificators() {
        return classificators;
    }

    public void setClassificators(List<StringCulture> classificators) {
        this.classificators = classificators;
    }

    public void addClassificator(StringCulture classificator) {
        classificators.add(classificator);
    }

//  cargoProducer

    private CargoProducer cargoProducer;

    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "cargo_producer_id", nullable = false)
    public CargoProducer getCargoProducer() {
        return this.cargoProducer;
    }

    public void setCargoProducer(CargoProducer cargoProducer) {
        this.cargoProducer = cargoProducer;
    }

//  regnumber

    private String regnumber;

    @Column(name = "regnumber", nullable = false, length = 50)
    public String getRegnumber() {
        return this.regnumber;
    }

    public void setRegnumber(String regnumber) {
        this.regnumber = regnumber;
    }

//  date

    private Date date;

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false, length = 10)
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

// names

    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 100, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

//  country

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisteredProducts)) return false;
        if (!super.equals(o)) return false;

        RegisteredProducts that = (RegisteredProducts) o;

        if (cargoProducer != null ? !cargoProducer.equals(that.cargoProducer) : that.cargoProducer != null)
            return false;
        if (classificator != null ? !classificator.equals(that.classificator) : that.classificator != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (regnumber != null ? !regnumber.equals(that.regnumber) : that.regnumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (classificator != null ? classificator.hashCode() : 0);
        result = 31 * result + (cargoProducer != null ? cargoProducer.hashCode() : 0);
        result = 31 * result + (regnumber != null ? regnumber.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}


