package org.vetcontrol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:31:39
 *
 * тип груза
 */
@Entity
@Table(name = "cargo_type")
public class CargoType implements IBook, Serializable {

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
    private String uktZedCode;

    @Column(name = "ukt_zed_code", nullable = false, length = 10)
    public String getCode() {
        return uktZedCode;
    }

    public void setCode(String uktZedCode) {
        this.uktZedCode = uktZedCode;
    }
    private Long name;

    @Column(name = "name")
    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

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

    private Boolean controled;

    /**
     * Get the value of controled
     *
     * @return the value of controled
     */
    @Column(name="controled", nullable=false)
    public Boolean getControled() {
        return controled;
    }

    /**
     * Set the value of controled
     *
     * @param controled new value of controled
     */
    public void setControled(Boolean controled) {
        this.controled = controled;
    }

}
