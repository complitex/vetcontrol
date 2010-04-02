package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.Update;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
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
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 5:16:18
 */
@Stateless
@Path("/update")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UpdateResourceBean {
    private static final Logger log = LoggerFactory.getLogger(UpdateResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;

    @POST
    @Path("/list")
    public List<Update> getLastClientUpdate(SyncRequestEntity re, @Context HttpServletRequest r){
        Client client = getClient(re, r);

        List<Update> list = em.createQuery("select u from Update u join fetch u.items " +
                "where u.version > :version and u.created > :created and u.active = true", Update.class)
                .setParameter("version", re.getVersion())
                .setParameter("created", re.getUpdated())
                .getResultList();

        if (!list.isEmpty()){
             logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_UPDATED, BookResourceBean.class, Update.class,
                    rb.getString("info.sync.processed"), list.size(), r.getRemoteHost(), client.getIp());

            log.info("Синхронизация " + Update.class.getSimpleName() + ". " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});
        }

        return list;
    }

    private Client getClient(SyncRequestEntity re, HttpServletRequest r) {
        try {
            return clientBean.getClient(re.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, BookResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8").build());
        }
    }
}
