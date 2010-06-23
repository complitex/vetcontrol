package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;
import org.vetcontrol.sync.adapter.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 15:30:29
 */
@Entity
@Table(name = "client_update")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Update implements ILongId{
    public static enum TYPE {CRITICAL, NOT_CRITICAL}

    @Id
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
    @XmlID @XmlJavaTypeAdapter(LongAdapter.class)
    private Long id;

    @OneToMany(mappedBy = "update", cascade = CascadeType.ALL)
    private List<UpdateItem> items;

    @Enumerated(EnumType.STRING)
    private TYPE type;

    private String version;

    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UpdateItem> getItems() {
        return items;
    }

    public void setItems(List<UpdateItem> items) {
        this.items = items;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
