package org.vetcontrol.sync.server.resources;

import java.util.List;
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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

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
    @PersistenceContext
    private EntityManager em;
    @EJB
    private LogBean logBean;
    @EJB
    private ClientBean clientBean;

    @PUT
    @Path("/document_cargo")
    public void putDocumentCargos(SyncDocumentCargo syncDocumentCargo, @Context HttpServletRequest r) {
        Client client = getClient(syncDocumentCargo, r);

        List<DocumentCargo> documents = syncDocumentCargo.getDocumentCargos();
        if (documents != null && !documents.isEmpty()) {
            int size = documents.size();

            try {
                for (DocumentCargo documentCargo : syncDocumentCargo.getDocumentCargos()) {
                    if (documentCargo.getId() == null) {
                        continue;
                    }
                    securityCheck(client, documentCargo, r);

                    documentCargo.setClient(em.getReference(Client.class, documentCargo.getClient().getId()));
                    documentCargo.setDepartment(em.getReference(Department.class, documentCargo.getDepartment().getId()));

                    em.merge(documentCargo);
                }

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
                for (Cargo cargo : syncCargo.getCargos()) {
                    if (cargo.getId() == null) {
                        continue;
                    }
                    securityCheck(client, cargo, r);

                    cargo.setClient(em.getReference(Client.class, cargo.getClient().getId()));
                    cargo.setDepartment(em.getReference(Department.class, cargo.getDepartment().getId()));

                    em.merge(cargo);
                }

                logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                        rb.getString("info.sync.processed"), syncCargo.getCargos().size(),
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

    private <T extends SyncRequestEntity> Client getClient(T re, HttpServletRequest r) {
        try {
            return clientBean.getClient(re.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(rb.getString("error.secure_key.check")).type("text/plain;charset=UTF-8").build());
        }
    }

    private void securityCheck(Client client, DocumentCargo documentCargo, HttpServletRequest r) {
        if (!documentCargo.getClient().getId().equals(client.getId())) {
            log.error(rb.getString("error.document.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, DocumentCargo.class,
                    rb.getString("error.document.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(rb.getString("error.document.check")).type("text/plain;charset=UTF-8").build());
        }
    }

    private void securityCheck(Client client, Cargo cargo, HttpServletRequest r) {
        if (!cargo.getClient().getId().equals(client.getId())) {
            log.error(rb.getString("error.document.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, DocumentResourceBean.class, Cargo.class,
                    rb.getString("error.document.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(rb.getString("error.document.check")).type("text/plain;charset=UTF-8").build());
        }
    }
}
