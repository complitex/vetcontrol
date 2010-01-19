package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.8 Справочник видов движения груза
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:14:57
 *
 */
@Entity
@Table(name = "movement_type")
public class MovementType extends Localizable{  
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
