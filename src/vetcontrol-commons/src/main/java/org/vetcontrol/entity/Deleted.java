/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.entity;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.Timeout;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Artem
 */
@Entity
@Table(name="deleted")
public class Deleted implements Serializable {

    @Id
    @Column(name="id")
    private String id;

    @Column(name="entity")
    private String entity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="deleted")
    private Date deleted;

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

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


}
