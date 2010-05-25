package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.User;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 19:27:32
 */
@Stateless @Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResourceBean {
    private static final Logger log = LoggerFactory.getLogger(UserResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @EJB(beanName = "LogBean")
    private LogBean logBean;

    private Client getClient(SyncRequestEntity requestEntity, HttpServletRequest request){
        try {
            return clientBean.getClient(requestEntity.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + " [ip: {}]", request.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + " [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @POST @Path("/list")       
    public GenericEntity<List<User>> getUsers(SyncRequestEntity requestEntity, @Context HttpServletRequest r){
        Client client = getClient(requestEntity, r);

        List<User> list = em.createQuery("select u from User u where u.department = :department " +
                "and u.updated > :updated order by u.updated", User.class)
                .setParameter("department", client.getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .getResultList();

        if (!list.isEmpty()){
            //Client Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);

            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserResourceBean.class, User.class,
                    rb.getString("info.sync.processed"), list.size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация пользователей. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});
        }

        return new GenericEntity<List<User>>(list){};
    }

    @POST @Path("/count")
    public Count getUsersCount(SyncRequestEntity requestEntity, @Context HttpServletRequest request){
         return new Count(em.createQuery("select count(u) from User u where u.department = :department " +
                "and u.updated > :updated", Long.class)
                .setParameter("department", getClient(requestEntity, request).getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .getSingleResult().intValue());
    }
}
