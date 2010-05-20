package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;
import org.vetcontrol.sync.LongAdapter;
import org.vetcontrol.book.annotation.ValidProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.01.2010 18:44:59
 */
@MappedSuperclass
public abstract class Localizable implements IBook, ILongId, IUpdated, IDisabled {

    protected Long id;

    @Id
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
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

    protected Long name;

    @Column(name = "name")
    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }
    private Map<String, String> namesMap;

    @ValidProperty(false)
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "locale", insertable = false, updatable = false)
    @CollectionTable(name = "stringculture",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "name", insertable = false, updatable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id", "locale"}))
    @Column(name = "value", insertable = false, updatable = false)
    @XmlTransient
    public Map<String, String> getNamesMap() {
        return namesMap;
    }

    public void setNamesMap(Map<String, String> namesMap) {
        this.namesMap = namesMap;
    }

    public String getDisplayName(java.util.Locale current, java.util.Locale system) {
        String displayName;
        if ((displayName = namesMap.get(current.getLanguage())) != null && !displayName.isEmpty()) {
            return displayName;
        } else if ((displayName = namesMap.get(system.getLanguage())) != null) {
            return displayName;
        }
        return "";
    }

    protected Date updated;

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
    protected boolean disabled;

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
        if (!(o instanceof Localizable)) return false;

        Localizable that = (Localizable) o;

        if (disabled != that.disabled) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (namesMap != null ? !namesMap.equals(that.namesMap) : that.namesMap != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (namesMap != null ? namesMap.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (disabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[hash: " + Integer.toHexString(hashCode())
                + ", id: " + id
                + ", name: " + name
                + ", namesMap: " + namesMap + "]";
    }
}
