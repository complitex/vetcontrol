package org.vetcontrol.sync.server.resources;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.util.DateUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Client processRegistration(Client client, @Context HttpServletRequest request){
        if (client == null){
            log.warn("Клиент не должен быть пустым. [ip: {}]", request.getRemoteHost());
            
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Клиент не должен быть null")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        log.debug("Запрос на регистрацию клиента. [client: {}]", client);

        //Уникальность MAC адреса
        if (client.getMac() == null){
            log.warn("MAC адрес не должен быть пустым. [ip: {}]", request.getRemoteHost());

             throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("MAC адрес не должен быть пустым")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        Long count = em.createQuery("select count(c) from Client c where c.mac = :mac", Long.class)
                .setParameter("mac", client.getMac())
                .getSingleResult();

        if (count > 0){
            log.warn("Клиент уже зарегистрирован в системе. [mac: {}, ip: {}]", client.getMac(), request.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Клиент с mac адресом " + client.getMac() + " уже зарегистрирован в системе")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        //Подразделение
        if (client.getDepartment() == null){
            log.warn("Подразделение не должен быть пустым. [ip: {}]", request.getRemoteHost());

             throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Подразделение не должено быть пустым")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        if (client.getId() == null){
            //Генерация ключа
            String secureKey = DigestUtils.md5Hex(client.getMac());
            client.setSecureKey(secureKey);

            //Дата создания
            client.setCreated(DateUtil.getCurrentDate());            

            //Сохранение информации о клиенте
            try {
                em.persist(client);
            } catch (Exception e) {
                log.error("Ошибка сохранения клиента. [id: {}, ip: {}]", client.getId(), request.getRemoteHost());

                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ошибка сохранения клиента")
                    .type("text/plain;charset=UTF-8")
                    .build());
            }
        }
        
        return client;
    }
}
