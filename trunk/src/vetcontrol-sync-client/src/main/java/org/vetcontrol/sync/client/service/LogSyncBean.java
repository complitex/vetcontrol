package org.vetcontrol.sync.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Log;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.SyncLog;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.03.2010 13:28:08
 */
@Singleton(name = "LogSyncBean")
public class LogSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(BookSyncBean.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    public void process(){
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: DocumentCargo ============================");

        start(new SyncEvent(0, Log.class));

        SyncRequestEntity syncRequestEntity = createJSONClient("/log/last_document")
                .post(SyncRequestEntity.class, new SyncRequestEntity(secureKey, null));

        List<Log> logs = em.createQuery("select l from Log l where l.date >= :date and l.module = :module", Log.class)
                .setParameter("date", syncRequestEntity.getUpdated())
                .setParameter("module", Log.MODULE.DOCUMENT)
                .getResultList();

        int size = logs.size();

        if (size > 0){
            sync(new SyncEvent(size, size, Log.class));
            createJSONClient("/log/document").put(new SyncLog(secureKey, null, logs));
        }
         
        complete(new SyncEvent(size, Log.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: DocumentCargo +++++++++++++++++++\n");
    }
}
