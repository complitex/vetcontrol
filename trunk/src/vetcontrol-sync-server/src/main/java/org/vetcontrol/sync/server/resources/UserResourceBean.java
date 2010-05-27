package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncProcess;
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

    @POST
    @Path("/start")
    public SyncProcess processSyncStart(
            SyncRequestEntity re,
            @Context HttpServletRequest r) {
        getClient(re, r);

        return new SyncProcess(DateUtil.getCurrentDate());
    }

    @POST @Path("/list")
    public GenericEntity<List<User>> getUsers(SyncRequestEntity requestEntity, @Context HttpServletRequest r){
        Client client = getClient(requestEntity, r);

        List<User> list = em.createQuery("select u from User u where u.department = :department " +
                "and u.updated > :updated and u.updated <= :syncStart order by u.updated", User.class)
                .setParameter("department", client.getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .setParameter("syncStart", requestEntity.getSyncStart())
                .getResultList();

        if (!list.isEmpty()){
            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserResourceBean.class, User.class,
                    rb.getString("info.sync.processed"), list.size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация пользователей. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});

            //Client Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);
        }

        return new GenericEntity<List<User>>(list){};
    }

    @POST @Path("/count")
    public Count getUsersCount(SyncRequestEntity requestEntity, @Context HttpServletRequest request){
        return new Count(em.createQuery("select count(u) from User u where u.department = :department " +
                "and u.updated > :updated and u.updated <= :syncStart", Long.class)
                .setParameter("department", getClient(requestEntity, request).getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .setParameter("syncStart", requestEntity.getSyncStart())
                .getSingleResult().intValue());
    }

    @POST
    @Path("/usergroup/list")
    public GenericEntity<List<UserGroup>> getUserGroups(SyncRequestEntity requestEntity, @Context HttpServletRequest r) {
        Client client = getClient(requestEntity, r);

        List<UserGroup> list = em.createQuery("select ug from UserGroup ug, User u "
                + "where ug.login = u.login and u.department = :department and ug.updated > :updated " +
                "and ug.updated <= :syncStart order by ug.updated", UserGroup.class)
                .setParameter("department", client.getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .setParameter("syncStart", requestEntity.getSyncStart())
                .getResultList();

        if (!list.isEmpty()) {
            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserResourceBean.class, UserGroup.class,
                    rb.getString("info.sync.processed"), list.size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация групп пользователей. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});

            //Client Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);
        }

        return new GenericEntity<List<UserGroup>>(list) {
        };
    }

    @POST
    @Path("/usergroup/count")
    public Count getUserGroupsCount(SyncRequestEntity requestEntity, @Context HttpServletRequest r) {
        return new Count(em.createQuery("select count(ug) from UserGroup ug, User u " +
                "where ug.login = u.login and u.department = :department " +
                "and ug.updated > :updated and ug.updated <= :syncStart ", Long.class)
                .setParameter("department", getClient(requestEntity, r).getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .setParameter("syncStart", requestEntity.getSyncStart())
                .getSingleResult()
                .intValue());
    }

    @POST
    @Path("/usergroup/deleted/list")
    public GenericEntity<List<DeletedLongId>> getDeletedUserGroups(SyncRequestEntity requestEntity, @Context HttpServletRequest r) {
        Client client = getClient(requestEntity, r);

        List<DeletedLongId> list = em.createQuery("select d from DeletedLongId d " +
                "where d.id.entity = :entity and d.deleted > :updated " +
                "and d.deleted <= :syncStart order by d.deleted", DeletedLongId.class)
                .setParameter("entity", UserGroup.class.getCanonicalName())
                .setParameter("updated", requestEntity.getUpdated())
                .setParameter("syncStart", requestEntity.getSyncStart())
                .getResultList();

        if (!list.isEmpty()) {
            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, UserResourceBean.class, UserGroup.class,
                    rb.getString("info.sync.processed"), list.size(),
                    r.getRemoteHost(), client.getIp());

            log.info("Синхронизация удаленных групп пользователей. " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});

            //Client Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);
        }

        return new GenericEntity<List<DeletedLongId>>(list) {
        };
    }

    @POST
    @Path("/usergroup/deleted/count")
    public Count getDeletedUserGroupsCount(SyncRequestEntity requestEntity, @Context HttpServletRequest r) {
        getClient(requestEntity, r);

        int count = em.createQuery("select count(*) from DeletedLongId d " +
                "where d.id.entity = :entity and d.deleted > :updated and d.deleted <= :syncStart", Long.class)
                .setParameter("entity", UserGroup.class.getCanonicalName())
                .setParameter("updated", requestEntity.getUpdated())
                .setParameter("syncStart", requestEntity.getSyncStart())
                .getSingleResult()
                .intValue();

        return new Count(count);
    }
}
