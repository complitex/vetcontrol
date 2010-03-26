package org.vetcontrol.sync;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import org.vetcontrol.entity.Log.STATUS;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 21:06:59
 */
@XmlRootElement
public class SyncRequestEntity {

    private String secureKey;
    private Date updated;
    private STATUS lastSyncStatus;

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
}
