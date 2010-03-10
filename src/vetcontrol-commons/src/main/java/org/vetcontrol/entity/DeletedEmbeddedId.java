/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "deleted_embedded_id")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeletedEmbeddedId implements Serializable {
    @Embeddable
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Id implements Serializable {
        public Id() {
        }

        public Id(String id, String entity) {
            this.id = id;
            this.entity = entity;
        }

        @Column(name = "id")
        @XmlValue
        private String id;

        @Column(name = "entity")
        @XmlTransient
        private String entity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Id other = (Id) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            if ((this.entity == null) ? (other.entity != null) : !this.entity.equals(other.entity)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 73 * hash + (this.id != null ? this.id.hashCode() : 0);
            hash = 73 * hash + (this.entity != null ? this.entity.hashCode() : 0);
            return hash;
        }
    }
    @EmbeddedId
    private Id id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted")
    @XmlTransient
    private Date deleted;

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public DeletedEmbeddedId(Id id, Date deleted) {
        this.id = id;
        this.deleted = deleted;
    }

    public DeletedEmbeddedId() {
    }
}
