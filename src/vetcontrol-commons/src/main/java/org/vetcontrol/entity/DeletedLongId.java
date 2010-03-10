package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.2010 11:41:07
 */
@Entity
@Table(name = "deleted_long_id")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeletedLongId  implements Serializable{
    @Embeddable
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Id implements Serializable{
        @XmlValue
        private Long id;

        @XmlTransient
        private String entity;

        public Id() {
        }

        public Id(Long id, String entity) {
            this.id = id;
            this.entity = entity;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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

            if (!(obj instanceof Id)) {
                return false;
            }

            final Id other = (Id) obj;

            if ((id == null) ? (other.id != null) : !id.equals(other.id)) {
                return false;
            }

            if ((entity == null) ? (other.entity != null) : !entity.equals(other.entity)) {
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
    @XmlTransient
    private Date deleted;

    public DeletedLongId() {
    }

    public DeletedLongId(Long lid, String entity, Date deleted) {        
        this.id = new Id(lid, entity);
        this.deleted = deleted;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }
}
