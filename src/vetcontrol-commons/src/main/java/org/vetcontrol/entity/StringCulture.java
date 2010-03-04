package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.ValidProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@Entity
@Table(name = "stringculture")
@XmlRootElement
public class StringCulture implements IUpdated, IQuery, IEmbeddedId<StringCultureId> {
    public StringCulture() {
    }

    public StringCulture(StringCultureId id) {
        this.id = id;
    }

    public StringCulture(StringCultureId id, String value) {
        this.id = id;
        this.value = value;
    }

    private StringCultureId id;

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column =
        @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "locale", column =
        @Column(name = "locale", nullable = false, length = 2))})
    public StringCultureId getId() {
        return this.id;
    }

    public void setId(StringCultureId id) {
        this.id = id;
    }

    private String value;

    @Column(name = "value", length = 1024)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    private Date updated;

    @ValidProperty(false)
    @Column(name = "updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into stringculture (id, locale, `value`, updated) " +
                "value (:id, :locale, :value, :updated)")
                .setParameter("id", id.getId())
                .setParameter("locale", id.getLocale())
                .setParameter("value", value)
                .setParameter("updated", updated);
    }

    @Override
    public Query getUpdateQuery(EntityManager em){
        return em.createNativeQuery("update stringculture set `value` = :value, updated = :updated " +
                "where id = :id and locale = :locale")
                .setParameter("id", id.getId())
                .setParameter("locale", id.getLocale())
                .setParameter("value", value)
                .setParameter("updated", updated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringCulture)) return false;

        StringCulture that = (StringCulture) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[hash: " + Integer.toHexString(hashCode()) +
                ", id: " + id.getId() +
                ", locale: " + id.getLocale() +
                ", value: " + value +
                ", updated: " + updated + "]";
    }
}


