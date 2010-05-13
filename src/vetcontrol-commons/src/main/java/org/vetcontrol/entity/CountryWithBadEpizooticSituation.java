package org.vetcontrol.entity;

import org.vetcontrol.book.annotation.MappedProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artem
 *
 * Справочник стран с неблагоприятной эпизоотической ситуацией
 */
@Entity
@Table(name = "bad_epizootic_situation")
@XmlRootElement
public class CountryWithBadEpizooticSituation extends Localizable{   
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 20, nullable = false)
    @XmlTransient
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
