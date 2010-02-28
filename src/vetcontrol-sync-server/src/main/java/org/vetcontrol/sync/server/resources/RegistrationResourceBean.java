package org.vetcontrol.sync.server.resources;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Synchronized;
import org.vetcontrol.service.LogBean;
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

import java.util.ResourceBundle;

import static org.vetcontrol.entity.Log.EVENT.CREATE;
import static org.vetcontrol.entity.Log.MODULE.SYNC_SERVER;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.02.2010 16:12:07
 */
@Path("/registration")
@Stateless
public class RegistrationResourceBean {
    private static final Logger log = LoggerFactory.getLogger(RegistrationResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    @PersistenceContext
    private EntityManager em;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Client processRegistration(Client client, @Context HttpServletRequest request){
        log.debug(rb.getString("info.registration.request") + " [client: {}]", client);

        if (client == null){
            log.warn(rb.getString("error.client.null") + " [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("error.client.null") + " [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(rb.getString("error.client.null"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Регистрационный ключ
        if (!checkKey(client.getSecureKey())){
            log.warn(rb.getString("error.secure_key.check") + " [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + " [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Уникальность MAC адреса
        if (client.getMac() == null){
            log.warn(rb.getString("error.mac.null") + " [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("error.mac.null") + " [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(rb.getString("error.mac.null"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        Long count = em.createQuery("select count(c) from Client c where c.mac = :mac and c.syncStatus = :syncStatus", Long.class)
                .setParameter("mac", client.getMac())
                .setParameter("syncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                .getSingleResult();

        if (count > 0){
            log.warn(rb.getString("error.mac.registered") + " [mac: {}, ip: {}]", client.getMac(), request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("error.mac.registered") + " [mac: {0}, ip: {1}]", client.getMac(), request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(rb.getString("error.mac.registered") + " [mac: " + client.getMac()+"]")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Структурное подразделение
        if (client.getDepartment() == null){
            log.warn(rb.getString("error.department.null") + " [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("error.department.null") + " [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(rb.getString("error.department.null"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        if (client.getId() == null){
            //Генерация ключа
            String secureKey = DigestUtils.md5Hex(client.getMac());
            client.setSecureKey(secureKey);

            //Дата создания
            client.setCreated(DateUtil.getCurrentDate());
            client.setSyncStatus(Synchronized.SyncStatus.PROCESSING);

            //Сохранение информации о клиенте
            try {
                Client processing = null;
                try {
                    processing = em.createQuery("select c from Client c where c.mac = :mac and c.syncStatus = :syncStatus", Client.class)
                            .setParameter("mac", client.getMac())
                            .setParameter("syncStatus", Synchronized.SyncStatus.PROCESSING)
                            .getSingleResult();
                } catch (Exception e) {
                    //nothing
                }

                if (processing == null){
                    em.persist(client);
                }else{
                    client.setId(processing.getId());
                    em.merge(client);
                }

                log.debug(rb.getString("info.registration.processing") + " [id: {}]",  client.getId());
            } catch (Exception e) {
                log.error(rb.getString("error.client.save") + " [ip: {}]", request.getRemoteHost());

                logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                        rb.getString("error.client.save") + " [ip: {0}]", request.getRemoteHost());

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(rb.getString("error.client.save"))
                        .type("text/plain;charset=UTF-8")
                        .build());
            }
        }

        return client;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/commit")
    public void commit(String secureKey, @Context HttpServletRequest request){
        try {
            int row = em.createQuery("update Client c set c.syncStatus = :newSyncStatus, c.updated = :updated " +
                    "where c.secureKey = :secureKey and c.syncStatus = :oldSyncStatus")
                    .setParameter("newSyncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                    .setParameter("updated", DateUtil.getCurrentDate())
                    .setParameter("secureKey", secureKey)
                    .setParameter("oldSyncStatus", Synchronized.SyncStatus.PROCESSING)
                    .executeUpdate();

            if (row != 1){
                log.error(rb.getString("error.client.find") + " [ip: {}]", request.getRemoteHost());

                logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                        rb.getString("error.client.find") + " [ip: {0}]", request.getRemoteHost());

                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(rb.getString("error.client.find"))
                        .type("text/plain;charset=UTF-8")
                        .build());
            }

            Client client = em.createQuery("select c from Client c where c.secureKey = :secureKey", Client.class)
                    .setParameter("secureKey", secureKey)
                    .getSingleResult();

            log.debug(rb.getString("info.registration.success") + " [id: {}, ip: {}, mac: {}]",
                    new String[]{client.getId().toString(), request.getRemoteHost(), client.getMac()});

            logBean.info(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("info.registration.success") + " [id: {0}, ip: {1}, mac: {2}]",
                    client.getId(), request.getRemoteHost(), client.getMac());

        } catch (WebApplicationException e){
            throw e;
        } catch (Exception e) {
            log.error(rb.getString("error.registration.save") + " [ip: {0}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    rb.getString("error.registration.save") + " [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(rb.getString("error.registration.save"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }          
    }

    private boolean checkKey(String key){
        return true;
    }
}
