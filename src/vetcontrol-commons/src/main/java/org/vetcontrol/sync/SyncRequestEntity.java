package org.vetcontrol.sync;

import org.vetcontrol.entity.Log.STATUS;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 21:06:59
 */
@XmlRootElement
public class SyncRequestEntity {

    private String secureKey;
    private Date updated;
    private STATUS lastSyncStatus;
    private String version;

    public SyncRequestEntity() {
    }

    public SyncRequestEntity(String secureKey, Date updated) {
        this.secureKey = secureKey;
        this.updated = updated;
    }

    public SyncRequestEntity(String secureKey, Date updated, STATUS lastSyncStatus) {
        this.secureKey = secureKey;
        this.updated = updated;
        this.lastSyncStatus = lastSyncStatus;
    }

    public SyncRequestEntity(String secureKey, Date updated, STATUS lastSyncStatus, String version) {
        this.secureKey = secureKey;
        this.updated = updated;
        this.lastSyncStatus = lastSyncStatus;
        this.version = version;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public STATUS getLastSyncStatus() {
        return lastSyncStatus;
    }

    public void setLastSyncStatus(STATUS lastSyncStatus) {
        this.lastSyncStatus = lastSyncStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
