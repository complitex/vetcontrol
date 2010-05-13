package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.03.2010 12:56:10
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResourceBean {
    private static final Logger log = LoggerFactory.getLogger(DocumentResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    private static final int DB_BATCH_SIZE = 50;

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "LogBean")
    private LogBean logBean;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PUT
    @Path("/document_cargo")
    public void putDocumentCargos(SyncDocumentCargo syncDocumentCargo, @Context HttpServletRequest r) {
        Client client = getClient(syncDocumentCargo, r);

        List<DocumentCargo> documents = syncDocumentCargo.getDocumentCargos();
        if (documents != null && !documents.isEmpty()) {
            int size = documents.size();

            try {
                int index = 0;

                for (DocumentCargo documentCargo : syncDocumentCargo.getDocumentCargos()) {
                    if (documentCargo.getId() == null) {
                        continue;
                    }
                    securityCheck(client, DocumentCargo.class, documentCargo.getClient(), r);

                    EntityPersisterUtil.insert(em, documentCargo);

                    if (index++ % DB_BATCH_SIZE == 0){
                        EntityPersisterUtil.executeBatch(em);
                    }
                }

                EntityPersisterUtil.executeBatch(em);

                logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                        rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

                log.info("Синхронизация карточек на груз. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            } catch (WebApplicationException e) {
                throw e;
            } catch (Exception e) {
                logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                        rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

                log.error("Синхронизация карточек на груз. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
                log.error(e.getLocalizedMessage(), e);

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(rb.getString("error.db.internal_server_error")).type("text/plain;charset=UTF-8").build());
            }
        }
    }

    @PUT
    @Path("/cargo")
    public void putCargos(SyncCargo syncCargo, @Context HttpServletRequest r) {
        Client client = getClient(syncCargo, r);

        List<Cargo> cargos = syncCargo.getCargos();
        if (cargos != null && !cargos.isEmpty()) {
            int size = cargos.size();

            try {
                int index = 0;

                for (Cargo cargo : cargos) {
                    if (cargo.getId() == null) {
                        continue;
                    }
                    securityCheck(client, Cargo.class, cargo.getClient(), r);

                    cargo.setDocumentCargo(em.getReference(DocumentCargo.class,
                            new ClientEntityId(cargo.getDocumentCargoId(), cargo.getClient(), cargo.getDepartment())));

                    EntityPersisterUtil.insert(em, cargo);

                    if (index++ % DB_BATCH_SIZE == 0){
                        EntityPersisterUtil.executeBatch(em);
                    }
                }

                EntityPersisterUtil.executeBatch(em);

                logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Cargo.class,
                        rb.getString("info.sync.processed"), size,
                        r.getRemoteHost(), client.getIp());

                log.info("Синхронизация грузов. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            } catch (Exception e) {
                logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Cargo.class,
                        rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

                log.error("Ошибка синхронизации грузов. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
                log.error(e.getLocalizedMessage(), e);

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(rb.getString("error.db.internal_server_error")).type("text/plain;charset=UTF-8").build());
            }
        }
    }

    @PUT
    @Path("/vehicle")
    public void putVehicles(SyncVehicle sycnVehicle, @Context HttpServletRequest r) {
        Client client = getClient(sycnVehicle, r);

        List<Vehicle> vehicles = sycnVehicle.getVehicles();
        if (vehicles != null && !vehicles.isEmpty()) {
            int size = vehicles.size();

            try {
                int index = 0;

                for (Vehicle vehicle : vehicles) {
                    if (vehicle.getId() == null) {
                        continue;
                    }
                    securityCheck(client, Vehicle.class, vehicle.getClient(), r);

                    vehicle.setDocumentCargo(em.getReference(DocumentCargo.class,
                            new ClientEntityId(vehicle.getDocumentCargoId(), vehicle.getClient(), vehicle.getDepartment())));

                    EntityPersisterUtil.insert(em, vehicle);

                    if (index++ % DB_BATCH_SIZE == 0){
                        EntityPersisterUtil.executeBatch(em);
                    }
                }

                EntityPersisterUtil.executeBatch(em);  

                logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Vehicle.class,
                        rb.getString("info.sync.processed"), size,
                        r.getRemoteHost(), client.getIp());

                log.info("Синхронизация транспортных средств. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            } catch (Exception e) {
                logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Vehicle.class,
                        rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

                log.error("Ошибка синхронизации транспортных средств. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
                log.error(e.getLocalizedMessage(), e);

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(rb.getString("error.db.internal_server_error"))
                        .type("text/plain;charset=UTF-8")
                        .build());
            }
        }
    }

    @PUT
    @Path("/arrest_document")
    public void putArrestDocument(SyncArrestDocument syncArrestDocument, @Context HttpServletRequest r) {
        Client client = getClient(syncArrestDocument, r);

        List<ArrestDocument> arrestDocuments = syncArrestDocument.getArrestDocuments();
        if (arrestDocuments != null && !arrestDocuments.isEmpty()) {
            int size = arrestDocuments.size();

            try {
                int index = 0;

                for (ArrestDocument arrestDocument : arrestDocuments) {
                    if (arrestDocument.getId() == null) {
                        continue;
                    }
                    securityCheck(client, ArrestDocument.class, arrestDocument.getClient(), r);

                    EntityPersisterUtil.insert(em, arrestDocument);                  

                    if (index++ % DB_BATCH_SIZE == 0){
                        EntityPersisterUtil.executeBatch(em);
                    }
                }

                EntityPersisterUtil.executeBatch(em);

                logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, ArrestDocument.class,
                        rb.getString("info.sync.processed"), size,
                        r.getRemoteHost(), client.getIp());

                log.info("Синхронизация актов задержания груза. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            } catch (Exception e) {
                logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, ArrestDocument.class,
                        rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

                log.error("Ошибка синхронизации актов задержания груза. " + rb.getString("info.sync.processed.log"),
                        new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
                log.error(e.getLocalizedMessage(), e);

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(rb.getString("error.db.internal_server_error"))
                        .type("text/plain;charset=UTF-8")
                        .build());
            }
        }
    }

    private <T extends SyncRequestEntity> Client getClient(T re, HttpServletRequest r) {
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


    private void securityCheck(Client client, Class entity, Client entityClient, HttpServletRequest r) {
        if (!entityClient.getId().equals(client.getId())) {
            log.error(rb.getString("error.document.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, entity,
                    rb.getString("error.document.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.document.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }
}
