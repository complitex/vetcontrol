package org.vetcontrol.sync;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.05.2010 17:02:23
 */
@XmlRootElement
public class SyncProcess {
    private Date syncStart;

    public SyncProcess() {
    }

    public SyncProcess(Date syncStart) {
        this.syncStart = syncStart;
    }

    public Date getSyncStart() {
        return syncStart;
    }

    public void setSyncStart(Date syncStart) {
        this.syncStart = syncStart;
    }
}
