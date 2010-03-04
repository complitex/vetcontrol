package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * Справочник стран
 *
 */
@Entity
@Table(name = "countrybook")
@XmlRootElement
public class CountryBook extends Localizable {

    private String code;

    public CountryBook() {
    }

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
    @XmlTransient
    public List<StringCulture> getNames() {
        return this.names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    public void addName(StringCulture name) {
        names.add(name);
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into countrybook (id, `name`, code, updated, disabled) " +
                "value (:id, :name, :code, :updated, :disabled)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("code", code)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em){
        return em.createNativeQuery("update countrybook set `name` = :name, code = :code, updated = :updated, " +
                "disabled = :disabled where id = :id")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("code", code)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountryBook)) return false;
        if (!super.equals(o)) return false;

        CountryBook that = (CountryBook) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (names != null ? !names.equals(that.names) : that.names != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (names != null ? names.hashCode() : 0);
        return result;
    }
}


