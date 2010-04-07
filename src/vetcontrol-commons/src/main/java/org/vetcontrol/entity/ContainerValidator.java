/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.vetcontrol.sync.LongAdapter;
import org.vetcontrol.util.book.entity.annotation.ValidProperty;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "container_validator")
public class ContainerValidator implements IBook, ILongId, IUpdated, IQuery, IDisabled {

    public static final String CONTAINER_CODE_FORMAT = "XXXX999999-9";
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    public Query getInsertQuery(EntityManager em) {
        return em.createNativeQuery("insert into container_validator (id, carrier_abbr, carrier_name, `prefix` updated, disabled) "
                + "value (:id, :carrier_abbr, :carrier_name, :prefix, :updated, :disabled)").
                setParameter("id", id).
                setParameter("carrier_name", carrierName).
                setParameter("carrier_abbr", carrierAbbr).
                setParameter("prefix", prefix).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }

    @Override
    public Query getUpdateQuery(EntityManager em) {
        return em.createNativeQuery("update container_validator set carrier_abbr = :carrier_abbr, carrier_name = :carrier_name, "
                + "`prefix` = :prefix, updated = :updated, disabled = :disabled where id = :id").
                setParameter("id", id).
                setParameter("carrier_name", carrierName).
                setParameter("carrier_abbr", carrierAbbr).
                setParameter("prefix", prefix).
                setParameter("updated", updated).
                setParameter("disabled", disabled);
    }
}
