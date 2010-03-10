package org.vetcontrol.sync;

import org.vetcontrol.entity.Log;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.03.2010 13:17:13
 */
@XmlRootElement
public class SyncLog extends SyncRequestEntity{
    private List<Log> logs;

    public SyncLog() {
    }

    public SyncLog(String secureKey, Date updated, List<Log> logs) {
        super(secureKey, updated);
        this.logs = logs;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }
}
