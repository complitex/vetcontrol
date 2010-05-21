package org.vetcontrol.entity;

import org.vetcontrol.book.annotation.MappedProperty;
import org.vetcontrol.book.annotation.ValidProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:34:56
 *
 * Справочник единиц измерения
 */
@Entity
@Table(name = "unit_type")
@XmlRootElement
public class UnitType extends Localizable {

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

    private Long shortName;

    @Column(name = "short_name")
    public Long getShortName() {
        return shortName;
    }

    public void setShortName(Long shortName) {
        this.shortName = shortName;
    }

    private List<StringCulture> shortNames = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("shortName")
    @Column(length = 10, nullable = false)
    @XmlTransient
    public List<StringCulture> getShortNames() {
        return shortNames;
    }

    public void setShortNames(List<StringCulture> shortNames) {
        this.shortNames = shortNames;
    }

    private Map<String, String> shortNamesMap;

    @ValidProperty(false)
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "locale", insertable = false, updatable = false)
    @CollectionTable(name = "stringculture",
    joinColumns =
    @JoinColumn(name = "id", referencedColumnName = "short_name", insertable = false, updatable = false),
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id", "locale"}))
    @Column(name = "value", insertable = false, updatable = false)
    @XmlTransient
    public Map<String, String> getShortNamesMap() {
        return shortNamesMap;
    }

    public void setShortNamesMap(Map<String, String> namesMap) {
        this.shortNamesMap = namesMap;
    }

    public String getDisplayShortName(java.util.Locale current, java.util.Locale system) {
        String displayName;
        if ((displayName = shortNamesMap.get(current.getLanguage())) != null && !displayName.isEmpty()) {
            return displayName;
        } else if ((displayName = shortNamesMap.get(system.getLanguage())) != null) {
            return displayName;
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnitType)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UnitType unitType = (UnitType) o;

        if (names != null ? !names.equals(unitType.names) : unitType.names != null) {
            return false;
        }
        if (shortName != null ? !shortName.equals(unitType.shortName) : unitType.shortName != null) {
            return false;
        }
        if (shortNames != null ? !shortNames.equals(unitType.shortNames) : unitType.shortNames != null) {
            return false;
        }
        if (shortNamesMap != null ? !shortNamesMap.equals(unitType.shortNamesMap) : unitType.shortNamesMap != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (names != null ? names.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (shortNames != null ? shortNames.hashCode() : 0);
        result = 31 * result + (shortNamesMap != null ? shortNamesMap.hashCode() : 0);
        return result;
    }
}
