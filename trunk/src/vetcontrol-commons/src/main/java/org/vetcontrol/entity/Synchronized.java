package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.ValidProperty;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.02.2010 16:19:22
 */
@MappedSuperclass
public class Synchronized {
    public static enum SyncStatus{
        SYNCHRONIZED, NOT_SYNCHRONIZED, PROCESSING
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    private SyncStatus syncStatus = SyncStatus.NOT_SYNCHRONIZED;

    @XmlTransient
    @ValidProperty(false)
    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }
}
