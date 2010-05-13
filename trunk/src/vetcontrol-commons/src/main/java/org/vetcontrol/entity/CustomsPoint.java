package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;
import org.vetcontrol.util.book.entity.annotation.ViewLength;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:46:58
 *
 * Справочник места таможенного оформления грузов
 */
@Entity
@Table(name = "customs_point")
@XmlRootElement
public class CustomsPoint extends Localizable{
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 100, nullable = false)
    @XmlTransient
    @ViewLength(100)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
