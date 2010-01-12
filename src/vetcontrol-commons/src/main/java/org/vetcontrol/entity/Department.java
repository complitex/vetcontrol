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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "department")
public class Department implements Serializable {

    private Long id;

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }
    private Long name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    @Column(name = "name")
    public Long getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(Long name) {
        this.name = name;
    }
    private List<StringCulture> names = new ArrayList<StringCulture>();

    /**
     * Get the value of names
     *
     * @return the value of names
     */
    @Transient
    @MappedProperty("name")
    @Column(length = 50, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    /**
     * Set the value of names
     *
     * @param names new value of names
     */
    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
    private Department parent;

    /**
     * Get the value of parent
     *
     * @return the value of parent
     */
    @BookReference(referencedProperty = "names")
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "parent_id", nullable = true)
    public Department getParent() {
        return parent;
    }

    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public void setParent(Department parent) {
        this.parent = parent;
    }
}
