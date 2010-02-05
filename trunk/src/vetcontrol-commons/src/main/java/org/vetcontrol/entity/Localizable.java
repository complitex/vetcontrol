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
    public  Long getId(){
        return id;
    }

    public void setId(Long id) {
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

    private String xmlID;

    @XmlID
    @Transient
    public String getXmlID() {
        return id != null ? String.valueOf(id) : "-1";
    }

    public void setXmlID(String xmlID) {
        this.xmlID = xmlID;
    }

//    private  Map<String, String> namesMap;

//    @ValidProperty(false)
//    @ElementCollection(fetch = FetchType.EAGER)
//    @MapKeyColumn(name = "locale")
//    @CollectionTable(name = "stringculture",
//            joinColumns = @JoinColumn(name="id", referencedColumnName="name"),
//            uniqueConstraints = @UniqueConstraint(columnNames = {"id", "locale"}))
//    @Column(name = "value")
//    public Map<String, String> getNamesMap() {
//        return namesMap;
//    }
//
//    public void setNamesMap(Map<String, String> namesMap) {
//        this.namesMap = namesMap;
//    }
//
//    public String getDisplayName(java.util.Locale current, java.util.Locale system) {
//        String name;
//        if ((name = namesMap.get(current.getLanguage())) != null  && !name.isEmpty()){
//            return name;
//        }else if((name = namesMap.get(system.getLanguage())) != null){
//            return name;
//        }
//        return "";
//    }

    private  Map<String, StringCulture> namesMap;

    @ValidProperty(false)
    @OneToMany(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "locale")
    @JoinColumn(name = "id", referencedColumnName = "name")
    public Map<String, StringCulture> getNamesMap() {
        return namesMap;
    }  

    public void setNamesMap(Map<String, StringCulture> namesMap) {
        this.namesMap = namesMap;
    }

    @Transient
    public String getDisplayName(java.util.Locale current, java.util.Locale system){
        StringCulture stringCulture = null;
        if ((stringCulture = namesMap.get(current.getLanguage())) != null
                && stringCulture.getValue() != null
                && !stringCulture.getValue().isEmpty()){
            return stringCulture.getValue();
        }else if((stringCulture = namesMap.get(system.getLanguage())) != null){
            return stringCulture.getValue();
        }
        return "";
    }


    private Date updated;

    @ValidProperty(false)
    @Column(name = "updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

}
