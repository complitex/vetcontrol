package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.ValidProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlID;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.01.2010 18:44:59
 */
@MappedSuperclass
public abstract class Localizable implements Serializable {
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlID
    public  Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    private Long name;

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
    public Map<String, String> getNamesMap() {
        return namesMap;
    }

    public void setNamesMap(Map<String, String> namesMap) {
        this.namesMap = namesMap;
    }

    @Transient
    public String getDisplayName(java.util.Locale current, java.util.Locale system){
        String name;
        if ((name = namesMap.get(current.getLanguage())) != null  && !name.isEmpty()){
            return name;
        }else if((name = namesMap.get(system.getLanguage())) != null){
            return name;
        }
        return "";
    }

    private Date version;

    @Version
    @ValidProperty(false)
    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }
}
