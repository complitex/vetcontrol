/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;
import org.vetcontrol.sync.adapter.LongAdapter;
import org.vetcontrol.book.annotation.ValidProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "container_validator")
@XmlRootElement
public class ContainerValidator implements IBook, ILongId, IUpdated, IDisabled {

    public static final String CONTAINER_CODE_FORMAT = "XXXX999999-9";
    private Long id;

    @Id
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
    @XmlID
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    private String carrierName;

    @Column(name = "carrier_name", nullable = false, length = 100)
    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String name) {
        this.carrierName = name;
    }
    private String carrierAbbr;

    @Column(name = "carrier_abbr", nullable = true, length = 50)
    public String getCarrierAbbr() {
        return carrierAbbr;
    }

    public void setCarrierAbbr(String carrierAbbr) {
        this.carrierAbbr = carrierAbbr;
    }
    private String prefix;

    @Column(name = "prefix", nullable = false, length = 4)
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    private Date updated;

    @ValidProperty(false)
    @Column(name = "updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    private boolean disabled;

    @ValidProperty(false)
    @Column(name = "disabled")
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerValidator other = (ContainerValidator) obj;
        if ((this.carrierName == null) ? (other.carrierName != null) : !this.carrierName.equals(other.carrierName)) {
            return false;
        }
        if ((this.carrierAbbr == null) ? (other.carrierAbbr != null) : !this.carrierAbbr.equals(other.carrierAbbr)) {
            return false;
        }
        if ((this.prefix == null) ? (other.prefix != null) : !this.prefix.equals(other.prefix)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.carrierName != null ? this.carrierName.hashCode() : 0);
        hash = 31 * hash + (this.carrierAbbr != null ? this.carrierAbbr.hashCode() : 0);
        hash = 31 * hash + (this.prefix != null ? this.prefix.hashCode() : 0);
        return hash;
    }

}
