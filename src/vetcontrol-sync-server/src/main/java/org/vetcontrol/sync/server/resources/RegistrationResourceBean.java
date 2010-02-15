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

    @PersistenceContext
    private EntityManager em;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Client processRegistration(Client client, @Context HttpServletRequest request){
        log.debug("Запрос на регистрацию клиента. [client: {}]", client);

        if (client == null){
            log.warn("Клиент не должен быть пустым. [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "Клиент не должен быть пустым. [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Клиент не должен быть null")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Регистрационный ключ
        if (!checkKey(client.getSecureKey())){
            log.warn("Неправильный регистрационный ключ. [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "Неправильный регистрационный ключ. [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Неправильный регистрационный ключ")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Уникальность MAC адреса
        if (client.getMac() == null){
            log.warn("MAC адрес не должен быть пустым. [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "MAC адрес не должен быть пустым. [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("MAC адрес не должен быть пустым")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        Long count = em.createQuery("select count(c) from Client c where c.mac = :mac and c.syncStatus = :syncStatus", Long.class)
                .setParameter("mac", client.getMac())
                .setParameter("syncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                .getSingleResult();

        if (count > 0){
            log.warn("Клиент уже зарегистрирован в системе. [mac: {}, ip: {}]", client.getMac(), request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "Клиент уже зарегистрирован в системе. [mac: {0}, ip: {1}]", client.getMac(), request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Клиент с mac адресом " + client.getMac() + " уже зарегистрирован в системе")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Структурное подразделение
        if (client.getDepartment() == null){
            log.warn("Структурное подразделение не должен быть пустым. [ip: {}]", request.getRemoteHost());

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "Структурное подразделение не должен быть пустым. [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Структурное подразделение не должено быть пустым")
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

                log.debug("Клиент сохранен в базу данных. Ожидаем подтверждение. [id:" + client.getId()+"]");
            } catch (Exception e) {
                log.error("Ошибка сохранения клиента. [ip: {}]", request.getRemoteHost());

                logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                        "Ошибка сохранения клиента. [ip: {0}]", request.getRemoteHost());

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Ошибка сохранения клиента")
                        .type("text/plain;charset=UTF-8")
                        .build());
            }
        }

        return client;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/commit")
    public String commit(String secureKey, @Context HttpServletRequest request){
        try {
            int row = em.createQuery("update Client c set c.syncStatus = :newSyncStatus, c.updated = :updated " +
                    "where c.secureKey = :secureKey and c.syncStatus = :oldSyncStatus")
                    .setParameter("newSyncStatus", Synchronized.SyncStatus.SYNCHRONIZED)
                    .setParameter("updated", DateUtil.getCurrentDate())
                    .setParameter("secureKey", secureKey)
                    .setParameter("oldSyncStatus", Synchronized.SyncStatus.PROCESSING)
                    .executeUpdate();

            if (row != 1){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Ошибка синхронизации. Клиент по ключу не найдет.")
                        .type("text/plain;charset=UTF-8")
                        .build());
            }

            Client client = em.createQuery("select c from Client c where c.secureKey = :secureKey", Client.class)
                    .setParameter("secureKey", secureKey)
                    .getSingleResult();

            log.debug("Подтверждение регистрации успешно обработано. Клиент добавлен. [id: {}, ip: {}, mac: {}]",
                    new String[]{client.getId().toString(), request.getRemoteHost(), client.getMac()});

            logBean.info(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "Клиент добавлен. [id: {0}, ip: {1}, mac: {2}]",
                    client.getId(), request.getRemoteHost(), client.getMac());

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);

            logBean.error(SYNC_SERVER, CREATE, RegistrationResourceBean.class, Client.class,
                    "Ошибка сохранения статуса синхронизации. [ip: {0}]", request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ошибка сохранения статуса синхронизации")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        return "SYNCHRONIZED";
    }

    private boolean checkKey(String key){
        return true;
    }
}
