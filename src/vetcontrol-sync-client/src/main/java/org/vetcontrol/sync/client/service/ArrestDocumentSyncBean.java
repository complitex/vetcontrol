package org.vetcontrol.sync.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.SyncArrestDocument;
import org.vetcontrol.sync.client.service.exception.DBOperationException;
import org.vetcontrol.sync.client.service.exception.NetworkConnectionException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.Date;
import java.util.List;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.04.2010 10:58:23
 */
@Singleton(name = "ArrestDocumentSyncBean")
@TransactionManagement(TransactionManagementType.BEAN)
public class ArrestDocumentSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(ArrestDocumentSyncBean.class);
    private static final int NETWORK_BATCH = 100;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction ut;

    public void process(){
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: ArrestDocument ============================");

        start(new SyncEvent(0, ArrestDocument.class));

        Date maxUpdated = getMaxUpdated(ArrestDocument.class);

        int count = em.createQuery("select count(ad) from ArrestDocument ad where ad.syncStatus = :syncStatus", Long.class).
                setParameter("syncStatus", Synchronized.SyncStatus.NOT_SYNCHRONIZED).
                getSingleResult().intValue();


        boolean isNotSynchronizedExist = count > 0;
        int i = 0;
        while (isNotSynchronizedExist) {
            List<ArrestDocument> documents = em.createQuery("select ad from ArrestDocument ad where ad.syncStatus = :syncStatus order by ad.updated",
                    ArrestDocument.class).
                    setParameter("syncStatus", Synchronized.SyncStatus.NOT_SYNCHRONIZED).
                    setFirstResult(0).
                    setMaxResults(NETWORK_BATCH).
                    getResultList();
            if (documents != null && !documents.isEmpty()) {
                try {
                    ut.begin();
                    for (ArrestDocument ad : documents) {
                        ad.setSyncStatus(Synchronized.SyncStatus.SYNCHRONIZED);
                        em.merge(ad);
                    }
                    ut.commit();
                } catch (Exception e) {
                    try {
                        ut.rollback();
                    } catch (Exception rollbackExc) {
                        log.error("Couldn't to rollback transaction.", rollbackExc);
                    }
                    throw new DBOperationException("DB operation exception.", e, ArrestDocument.class, Log.EVENT.SYNC);
                }

                try {
                    createJSONClient("/document/arrest_document").put(new SyncArrestDocument(secureKey, maxUpdated, documents));
                    sync(new SyncEvent(count, i * NETWORK_BATCH, ArrestDocument.class));
                } catch (Exception e) {
                    StringBuilder query = new StringBuilder("update ArrestDocument ad set ad.syncStatus = :newSyncStatus"
                            + " where ad.id in (");
                    for (int j = 0; j < documents.size(); j++) {
                        query.append(documents.get(j).getId());
                        if (j < documents.size() - 1) {
                            query.append(", ");
                        }
                    }
                    query.append(")");

                    try {
                        ut.begin();
                        em.createQuery(query.toString()).
                                setParameter("newSyncStatus", Synchronized.SyncStatus.NOT_SYNCHRONIZED).
                                executeUpdate();
                        ut.commit();
                    } catch (Exception updateExc) {
                        log.error("Couldn't to recover documents state.", updateExc);
                        try {
                            ut.rollback();
                        } catch (Exception rollbackExc) {
                            log.error("Couldn't to rollback transaction.", rollbackExc);
                        }
                    }
                    throw new NetworkConnectionException("Network connection exception.", e, ArrestDocument.class, Log.EVENT.SYNC);
                }
                i++;
            } else {
                isNotSynchronizedExist = false;
            }
        }

        complete(new SyncEvent(count, ArrestDocument.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: ArrestDocument +++++++++++++++++++\n");

    }

     private Date getMaxUpdated(Class<? extends IUpdated> entity) {
        Date updated = em.createQuery("select max(e.updated) from " + entity.getSimpleName() + " e", Date.class).getSingleResult();
        if (updated == null) {
            return new Date(0);
        }

        return updated;
    }

}
