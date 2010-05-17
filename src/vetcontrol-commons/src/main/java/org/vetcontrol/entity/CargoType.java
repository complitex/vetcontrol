package org.vetcontrol.entity;

import org.vetcontrol.book.annotation.MappedProperty;
import org.vetcontrol.book.annotation.ViewLength;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
@XmlRootElement
public class CargoType extends Localizable {

    private String code;

    @Column(name = "ukt_zed_code", nullable = false, length = 10, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String uktZedCode) {
        this.code = uktZedCode;
    }

    private List<StringCulture> names = new ArrayList<StringCulture>();

    @Transient
    @MappedProperty("name")
    @Column(length = 500, nullable = false)
    @XmlTransient
    @ViewLength(100)
    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CargoType)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CargoType cargoType = (CargoType) o;

        if (code != null ? !code.equals(cargoType.code) : cargoType.code != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
