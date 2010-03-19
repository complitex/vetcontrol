package org.vetcontrol.sync.server.service;

import org.vetcontrol.entity.Update;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 16:33:54
 */
public class UpdateFilter implements Serializable{
    private Long id;
    private String version;
    private Update.TYPE type;
    private Boolean active;
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Update.TYPE getType() {
        return type;
    }

    public void setType(Update.TYPE type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
