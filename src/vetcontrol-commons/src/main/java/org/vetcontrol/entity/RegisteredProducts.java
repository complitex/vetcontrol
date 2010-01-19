package org.vetcontrol.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Справочник перечня препаратов, зарегестрированных в Украине
 *
 */
@Entity
@Table(name = "registered_products")
public class RegisteredProducts extends Localizable{
    private long classificator;
    private String regnumber;
    private Date date;

    public RegisteredProducts() {
    }

    public RegisteredProducts(String regnumber, Date date) {
        this.regnumber = regnumber;
        this.date = date;
    }

    @Column(name = "classificator", nullable = false)
    public long getClassificator() {
        return this.classificator;
    }

    public void setClassificator(long classificator) {
        this.classificator = classificator;
    }
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

    @Column(name = "regnumber", nullable = false, length = 50)
    public String getRegnumber() {
        return this.regnumber;
    }

    public void setRegnumber(String regnumber) {
        this.regnumber = regnumber;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false, length = 10)
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    private List<StringCulture> names = new ArrayList<StringCulture>();

    /**
     * Get the value of names
     *
     * @return the value of names
     */
    @MappedProperty("name")
    @Transient
    @Column(length = 10, nullable = false)
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

    public void addName(StringCulture name) {
        names.add(name);
    }
    private List<StringCulture> classificators = new ArrayList<StringCulture>();

    /**
     * Get the value of classificators
     *
     * @return the value of classificators
     */
    @MappedProperty("classificator")
    @Transient
    @Column(length = 10, nullable = false)
    public List<StringCulture> getClassificators() {
        return classificators;
    }

    /**
     * Set the value of classificators
     *
     * @param classificators new value of classificators
     */
    public void setClassificators(List<StringCulture> classificators) {
        this.classificators = classificators;
    }

    public void addClassificator(StringCulture classificator) {
        classificators.add(classificator);
    }
    private CountryBook country;

    /**
     * Get the value of referencedCountry
     *
     * @return the value of referencedCountry
     */
    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "country_id", nullable = false)
    public CountryBook getCountry() {
        return country;
    }

    /**
     * Set the value of referencedCountry
     *
     * @param referencedCountry new value of referencedCountry
     */
    public void setCountry(CountryBook referencedCountry) {
        this.country = referencedCountry;
    }
}


