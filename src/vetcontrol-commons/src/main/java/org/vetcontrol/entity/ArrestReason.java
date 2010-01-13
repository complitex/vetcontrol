/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

/**
 * 2.4.3.3 Справочник оснований для задержания груза
 *
 * @author Artem
 */
@Entity
@Table(name = "arrest_reason")
public class ArrestReason implements Serializable {

    private Integer id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    private long name;

    @Column(name = "name")
    public long getName() {
        return this.name;
    }

    public void setName(long name) {
        this.name = name;
    }

    private List<StringCulture> names = new ArrayList<StringCulture>();

    @MappedProperty("name")
    @Transient
    @Column(length = 50, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
