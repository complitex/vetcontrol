package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.MappedProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:31:39
 *
 * тип груза
 */
@Entity
@Table(name = "cargo_type")
public class CargoType extends Localizable{
    private String uktZedCode;

    @Column(name = "ukt_zed_code", nullable = false, length = 10, unique = true)
    public String getCode() {
        return uktZedCode;
    }

    public void setCode(String uktZedCode) {
        this.uktZedCode = uktZedCode;
    }     

    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 500, nullable = false)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }
}
