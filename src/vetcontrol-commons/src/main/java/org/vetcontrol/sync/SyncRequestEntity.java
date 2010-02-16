package org.vetcontrol.sync;

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

    public SyncRequestEntity() {
    }

    public SyncRequestEntity(String secureKey, Date updated) {
        this.secureKey = secureKey;
        this.updated = updated;
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
}
