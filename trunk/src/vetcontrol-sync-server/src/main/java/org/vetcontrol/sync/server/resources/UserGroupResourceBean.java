package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.DeletedLongId;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.ID;
import org.vetcontrol.sync.SyncRequestEntity;

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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.02.2010 14:46:24
 */
@Stateless @Path("/usergroup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserGroupResourceBean {
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

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserGroupResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @POST @Path("/list")
    public GenericEntity<List<UserGroup>> getUserGroups(SyncRequestEntity requestEntity, @Context HttpServletRequest r){
        Client client = getClient(requestEntity, r);

        List<UserGroup> list = em.createQuery("select ug from UserGroup ug, User u " +
                "where ug.login = u.login and u.department = :department and ug.updated >= :updated", UserGroup.class)
                .setParameter("department", client.getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .getResultList();

        if (!list.isEmpty()){
            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserGroupResourceBean.class, UserGroup.class,
                    rb.getString("info.sync.processed"), client.getId(), list.size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация групп пользователей. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});
        }

        return new GenericEntity<List<UserGroup>>(list){};
    }

    @POST @Path("/count")
    public Count getUserGroupsCount(SyncRequestEntity requestEntity, @Context HttpServletRequest r){
        return new Count(em.createQuery("select count(ug) from UserGroup ug, User u " +
                "where ug.login = u.login and u.department = :department and ug.updated >= :updated", Long.class)
                .setParameter("department", getClient(requestEntity, r).getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .getSingleResult().intValue());
    }

    @POST @Path("/deleted/list")
    public GenericEntity<List<ID>> getDeletedUserGroups(SyncRequestEntity requestEntity, @Context HttpServletRequest r){
        Client client = getClient(requestEntity, r);

        List<Long> list = em.createQuery("select d.id.id from DeletedLongId d " +
                "where d.id.entity = :entity and d.deleted >= :updated", Long.class)
                .setParameter("entity", UserGroup.class.getCanonicalName())
                .setParameter("updated", requestEntity.getUpdated())
                .getResultList();

        List<ID> ids = new ArrayList<ID>(list.size());

        for (Long id : list){
            ids.add(new ID(id));
        }

        if (!list.isEmpty()){
            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserGroupResourceBean.class, DeletedLongId.class,
                    rb.getString("info.sync.processed"), client.getId(), list.size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация удаленных групп пользователей. " + rb.getString("info.sync.processed.log"), 
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});
        }

        return new GenericEntity<List<ID>>(ids){};
    }

    @POST @Path("/deleted/count")
    public Count getDeletedUserGroupsCount(SyncRequestEntity requestEntity, @Context HttpServletRequest r){
        getClient(requestEntity, r);

        return new Count( em.createQuery("select count(*) from DeletedLongId d " +
                "where d.id.entity = :entity and d.deleted >= :updated", Long.class)
                .setParameter("entity", UserGroup.class.getCanonicalName())
                .setParameter("updated", requestEntity.getUpdated())
                .getSingleResult().intValue());
    }
}
