package org.vetcontrol.entity;

import org.vetcontrol.sync.LongAdapter;
import org.vetcontrol.util.book.entity.annotation.ValidProperty;

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
public abstract class Localizable implements IBook, ILongId, IUpdated, IQuery, IDisabled {

    protected Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlID @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public  Long getId(){
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

    private  Map<String, String> namesMap;

    @ValidProperty(false)
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "locale")
    @CollectionTable(name = "stringculture",
            joinColumns = @JoinColumn(name="id", referencedColumnName="name"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id", "locale"}))
    @Column(name = "value")
    @XmlTransient
    public Map<String, String> getNamesMap() {
        return namesMap;
    }

    public void setNamesMap(Map<String, String> namesMap) {
        this.namesMap = namesMap;
    }

    public String getDisplayName(java.util.Locale current, java.util.Locale system) {
        String displayName;
        if ((displayName = namesMap.get(current.getLanguage())) != null  && !displayName.isEmpty()){
            return displayName;
        }else if((displayName = namesMap.get(system.getLanguage())) != null){
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
    @Column(name="disabled")
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected Query getInsertQuery(EntityManager em, String table){
        return em.createNativeQuery("insert into " + table + " (id, `name`, updated, disabled) value (:id, :name, :updated, :disabled)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }

    protected Query getUpdateQuery(EntityManager em, String table){
        return em.createNativeQuery("update " + table + " set `name` = :name, updated = :updated, disabled = :disabled where `id` = :id")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("updated", updated)
                .setParameter("disabled", disabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Localizable)) return false;

        Localizable that = (Localizable) o;

        if (disabled != that.disabled) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (disabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[hash: " + Integer.toHexString(hashCode()) +
                ", id: " + id +
                ", name: " + name +
                ", namesMap: " + namesMap + "]";
    }
}
