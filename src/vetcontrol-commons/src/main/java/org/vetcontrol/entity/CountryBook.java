package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Справочник стран
 *
 */
@Entity
@Table(name = "countrybook")
public class CountryBook extends Localizable{
    private String code;                         

    public CountryBook() {}

    public CountryBook(String code) {
        this.code = code;
    }

    @Column(name = "code", nullable = false, length = 2)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 10, nullable = false)
    public List<StringCulture> getNames() {
        return this.names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    public void addName(StringCulture name) {
        names.add(name);
    }
}


