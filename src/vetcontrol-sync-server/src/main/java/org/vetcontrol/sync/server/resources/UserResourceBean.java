package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.User;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 19:27:32
 */
@Stateless @Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResourceBean {
    private static final Logger log = LoggerFactory.getLogger(UserResourceBean.class);
//  private static final ResourceBundle rb = ResourceBundle.getBundle("");

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @EJB(beanName = "LogBean")
    private LogBean logBean;

    private Client getClient(SyncRequestEntity requestEntity){
        try {
            return clientBean.getClient(requestEntity.getSecureKey());
        } catch (Exception e) {
            log.error("Неверный ключ регистрации. Доступ запрещен.", e);

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("Неверный ключ регистрации. Доступ запрещен.")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @POST @Path("/list")       
    public GenericEntity<List<User>> getUsers(SyncRequestEntity requestEntity){
        return new GenericEntity<List<User>>(em.createQuery("select u from User u where u.department = :department " +
                "and u.updated >= :updated", User.class)
                .setParameter("department", getClient(requestEntity).getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .getResultList()){};
    }

    @POST @Path("/count")
    public String getUsersCount(SyncRequestEntity requestEntity){
         return em.createQuery("select count(u) from User u where u.department = :department " +
                "and u.updated >= :updated", Long.class)
                .setParameter("department", getClient(requestEntity).getDepartment())
                .setParameter("updated", requestEntity.getUpdated())
                .getSingleResult().toString();
    }
}
