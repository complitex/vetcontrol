package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.ValidProperty;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.02.2010 16:19:22
 */
@MappedSuperclass
public class Synchronized implements Serializable{
    public static enum SyncStatus{
        SYNCHRONIZED, NOT_SYNCHRONIZED, PROCESSING
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false)
    protected SyncStatus syncStatus = SyncStatus.NOT_SYNCHRONIZED;

    @XmlTransient
    @ValidProperty(false)
    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Synchronized)) return false;

        Synchronized that = (Synchronized) o;

        if (syncStatus != that.syncStatus) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return syncStatus != null ? syncStatus.hashCode() : 0;
    }
}
