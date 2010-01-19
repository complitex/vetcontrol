package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** 
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:34:56
 *
 * Справочник единиц измерения
 */

@Entity
@Table(name = "unit_type")
public class UnitType extends Localizable{
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 20, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
