package org.vetcontrol.entity;

import java.io.Serializable;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 2.4.3.10 Справочник единиц измерения
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:34:56
 *
 */

@Entity
@Table(name = "unit_type")
public class UnitType implements IBook, Serializable{
    private Integer id;
    private Long name;
    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name")
    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

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
