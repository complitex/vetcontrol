package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.SyncCargo;
import org.vetcontrol.sync.SyncDocumentCargo;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.03.2010 12:56:10
 */
@Stateless
@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResourceBean {
    private static final Logger log = LoggerFactory.getLogger(RegistrationResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    @PersistenceContext
    private EntityManager em;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PUT @Path("/document_cargo")
    public void putDocumentCargos(SyncDocumentCargo syncDocumentCargo, @Context HttpServletRequest r){
        Client client = getClient(syncDocumentCargo, r);

        int size = syncDocumentCargo.getDocumentCargos().size();

        try {
            for (DocumentCargo documentCargo : syncDocumentCargo.getDocumentCargos()){
                securityCheck(client, documentCargo, r);

                documentCargo.setSyncStatus(Synchronized.SyncStatus.PROCESSING);
                
                documentCargo.setClient(em.getReference(Client.class, documentCargo.getClient().getId()));
                documentCargo.setDepartment(em.getReference(Department.class, documentCargo.getDepartment().getId()));

                em.merge(documentCargo);
            }

            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.processed"), client.getId(), size, r.getRemoteHost(), client.getIp());

            log.info("Синхронизация карточек на груз. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
        } catch (Exception e) {
            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, BookResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.processed"), client.getId(), size, r.getRemoteHost(), client.getIp());

            log.error("Синхронизация карточек на груз. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            log.error(e.getLocalizedMessage(), e);

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(rb.getString("error.db.internal_server_error"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @PUT @Path("/document_cargo/commit")
    public void commitDocumentCargos(SyncRequestEntity syncRequestEntity, @Context HttpServletRequest r ){
        Client client = getClient(syncRequestEntity, r);

        try {
            em.createQuery("update DocumentCargo set syncStatus = :newSyncStatus where syncStatus = :oldSyncStatus " +
                    "and client = :client and department = :department")
                    .setParameter("newSyncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                    .setParameter("oldSyncStatus", Synchronized.SyncStatus.PROCESSING)
                    .setParameter("client", client)
                    .setParameter("department", client.getDepartment())
                    .executeUpdate();

            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_COMMIT, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.commit"), client.getId(), r.getRemoteHost(), client.getIp());

            log.info("Подтверждение синхронизации карточки на груз. " + rb.getString("info.sync.commit.log"),
                    new Object[]{client.getId(), r.getRemoteHost(), client.getIp()});
        } catch (Exception e) {
            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_COMMIT, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.commit"), client.getId(), r.getRemoteHost(), client.getIp());

            log.error("Ошибка подтверждения синхронизации карточки на груз. " + rb.getString("info.sync.commit.log"),
                    new Object[]{client.getId(), r.getRemoteHost(), client.getIp()});
            log.error(e.getLocalizedMessage(), e);

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(rb.getString("error.db.internal_server_error"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @PUT @Path("/cargo")
    public void putCargos(SyncCargo syncCargo, @Context HttpServletRequest r){
        Client client = getClient(syncCargo, r);

        int size = syncCargo.getCargos().size();

        try {
            for (Cargo cargo : syncCargo.getCargos()){
                securityCheck(client, cargo, r);

                cargo.setSyncStatus(Synchronized.SyncStatus.PROCESSING);
                cargo.setClient(em.getReference(Client.class, cargo.getClient().getId()));
                cargo.setDepartment(em.getReference(Department.class, cargo.getDepartment().getId()));

                em.merge(cargo);
            }

            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.processed"), client.getId(), syncCargo.getCargos().size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация грузов. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
        } catch (Exception e) {
            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Cargo.class,
                    rb.getString("info.sync.processed"), client.getId(), size, r.getRemoteHost(), client.getIp());

            log.error("Ошибка синхронизации грузов. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            log.error(e.getLocalizedMessage(), e);

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(rb.getString("error.db.internal_server_error"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @PUT @Path("/cargo/commit")
    public void commitCargo(SyncRequestEntity syncRequestEntity, @Context HttpServletRequest r){
        Client client = getClient(syncRequestEntity, r);

        try {
            em.createQuery("update Cargo set syncStatus = :newSyncStatus where syncStatus = :oldSyncStatus " +
                    "and client = :client and department = :department")
                    .setParameter("newSyncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                    .setParameter("oldSyncStatus", Synchronized.SyncStatus.PROCESSING)
                    .setParameter("client", client)
                    .setParameter("department", client.getDepartment())
                    .executeUpdate();

            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_COMMIT, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.commit"), client.getId(), r.getRemoteHost(), client.getIp());

            log.info("Подтверждение синхронизации грузов. " + rb.getString("info.sync.commit.log"),
                    new Object[]{client.getId(), r.getRemoteHost(), client.getIp()});

        } catch (Exception e) {
            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_COMMIT, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("info.sync.commit"), client.getId(), r.getRemoteHost(), client.getIp());

            log.error("Ошибка подтверждения синхронизации грузов. " + rb.getString("info.sync.commit.log"),
                    new Object[]{client.getId(), r.getRemoteHost(), client.getIp()});
            log.error(e.getLocalizedMessage(), e);

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(rb.getString("error.db.internal_server_error"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    private <T extends SyncRequestEntity> Client getClient(T re, HttpServletRequest r){
        try {
            return clientBean.getClient(re.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    private void securityCheck(Client client, DocumentCargo documentCargo, HttpServletRequest r){
        if (!documentCargo.getClient().getId().equals(client.getId()) ||
                !documentCargo.getDepartment().getId().equals(client.getDepartment().getId())){
            log.error(rb.getString("error.document.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("error.document.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.document.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    private void securityCheck(Client client, Cargo cargo, HttpServletRequest r){
        if (!cargo.getClient().getId().equals(client.getId()) ||
                !cargo.getDepartment().getId().equals(client.getDepartment().getId())){
            log.error(rb.getString("error.document.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Cargo.class,
                    rb.getString("error.document.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.document.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }
}
