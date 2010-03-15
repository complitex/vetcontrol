package org.vetcontrol.sync.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.SyncCargo;
import org.vetcontrol.sync.SyncDocumentCargo;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.03.2010 14:11:26
 */
@Singleton(name = "DocumentCargoSyncBean")
public class DocumentCargoSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(UserSyncBean.class);
    private static final int MAX_RESULTS = 100;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process(){
        processDocumentCargo();
        processCargo();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void processDocumentCargo(){
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: DocumentCargo ============================");

        start(new SyncEvent(0, DocumentCargo.class));

        em.createQuery("update DocumentCargo set syncStatus = :newSyncStatus where syncStatus = :oldSyncStatus")
                .setParameter("newSyncStatus", Synchronized.SyncStatus.PROCESSING)
                .setParameter("oldSyncStatus", Synchronized.SyncStatus.NOT_SYNCHRONIZED)
                .executeUpdate();

        List<DocumentCargo> documentCargos = em.createQuery("select dc from DocumentCargo dc " +
                "where dc.syncStatus = :syncStatus", DocumentCargo.class)
                .setParameter("syncStatus", Synchronized.SyncStatus.PROCESSING)
                .getResultList();

        int size = documentCargos.size();

        if (size > 0) {
            for (int i = 0; i <= size/MAX_RESULTS; ++i) {
                int from = i*MAX_RESULTS < size ? i*MAX_RESULTS : size-1;
                int to  = (i+1)*MAX_RESULTS < size ? (i+1)*MAX_RESULTS : size;

                List<DocumentCargo> subList = documentCargos.subList(from, to);

                createJSONClient("/document/document_cargo").put(new SyncDocumentCargo(secureKey,
                        getUpdated(DocumentCargo.class), subList));

                sync(new SyncEvent(size, from, DocumentCargo.class));

                em.createQuery("update DocumentCargo set syncStatus = :newSyncStatus where syncStatus = :oldSyncStatus")
                        .setParameter("newSyncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                        .setParameter("oldSyncStatus", Synchronized.SyncStatus.PROCESSING)
                        .executeUpdate();

                sync(new SyncEvent(size, size, DocumentCargo.class));

                createJSONClient("/document/document_cargo/commit").put(new SyncRequestEntity(secureKey,
                        getUpdated(DocumentCargo.class)));
            }
        }

        complete(new SyncEvent(size, DocumentCargo.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: DocumentCargo +++++++++++++++++++\n");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void processCargo(){
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: Cargo ============================");

        start(new SyncEvent(0, Cargo.class));

        em.createQuery("update Cargo set syncStatus = :newSyncStatus where syncStatus = :oldSyncStatus")
                .setParameter("newSyncStatus", Synchronized.SyncStatus.PROCESSING)
                .setParameter("oldSyncStatus", Synchronized.SyncStatus.NOT_SYNCHRONIZED)
                .executeUpdate();

        List<Cargo> cargos = em.createQuery("select c from Cargo c " +
                "where c.syncStatus = :syncStatus", Cargo.class)
                .setParameter("syncStatus", Synchronized.SyncStatus.PROCESSING)
                .getResultList();

        int size = cargos.size();

        if (size > 0) {
            for (int i = 0; i <= size/MAX_RESULTS; ++i) {
                int from = i*MAX_RESULTS < size ? i*MAX_RESULTS : size-1;
                int to  = (i+1)*MAX_RESULTS < size ? (i+1)*MAX_RESULTS : size;

                List<Cargo> subList = cargos.subList(from, to);

                createJSONClient("/document/cargo").put(new SyncCargo(secureKey, getUpdated(Cargo.class), subList));

                sync(new SyncEvent(size, from, Cargo.class));

                em.createQuery("update Cargo set syncStatus = :newSyncStatus where syncStatus = :oldSyncStatus")
                        .setParameter("newSyncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                        .setParameter("oldSyncStatus", Synchronized.SyncStatus.PROCESSING)
                        .executeUpdate();

                sync(new SyncEvent(size, size, Cargo.class));

                createJSONClient("/document/cargo/commit").put(new SyncRequestEntity(secureKey, getUpdated(Cargo.class)));
            }
        }

        complete(new SyncEvent(size, Cargo.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: DocumentCargo +++++++++++++++++++\n");
    }

    private Date getUpdated(Class<? extends IUpdated> entity){
        Date updated = em.createQuery("select max(e.updated) from " + entity.getSimpleName() +" e", Date.class)
                .getSingleResult();
        if (updated == null){
            return new Date(0);
        }

        return updated;
    }
}
