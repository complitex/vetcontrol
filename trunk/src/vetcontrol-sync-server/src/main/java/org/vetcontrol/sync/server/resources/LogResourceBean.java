package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.User;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.SyncLog;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.03.2010 12:54:15
 */
@Stateless
@Path("/log")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogResourceBean {
    private static final Logger log = LoggerFactory.getLogger(RegistrationResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    @PersistenceContext
    private EntityManager em;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @POST @Path("/last_document")
    public SyncRequestEntity getLastDocumentLog(SyncRequestEntity re, @Context HttpServletRequest r){
        Client client = getClient(re, r);

        Date date = em.createQuery("select max(l.date) from Log l where l.client = :client and l.module = :module", Date.class)
                .setParameter("client", client)
                .setParameter("module", Log.MODULE.DOCUMENT)
                .getSingleResult();

        if (date == null){
            date = new Date(0);
        }

        return new SyncRequestEntity(client.getSecureKey(), date);
    }

    @PUT @Path("/document")
    public void putDocumentLog(SyncLog syncLog, @Context HttpServletRequest r){
        Client client = getClient(syncLog, r);

        int size = syncLog.getLogs().size();

        try {
            for (Log log : syncLog.getLogs()){
                if (log.getId() == null) continue;

                log.setId(null);
                log.setClient(client);
                log.setUser(em.getReference(User.class, log.getUser().getId()));

                em.persist(log);
            }

            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, LogResourceBean.class, Log.class,
                    rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

            log.info("Синхронизация лога документов. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});

            //Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);
        } catch (Exception e) {
            logBean.error(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, LogResourceBean.class, Log.class,
                    rb.getString("info.sync.processed"), size, r.getRemoteHost(), client.getIp());

            log.error("Синхронизация лога документов. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), size, r.getRemoteHost(), client.getIp()});
            log.error(e.getLocalizedMessage(), e);

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(rb.getString("error.db.internal_server_error"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    private Client getClient(SyncRequestEntity re, HttpServletRequest r){
        try {
            return clientBean.getClient(re.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, LogResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }
}
