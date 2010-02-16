package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.JSONResolver;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

import static com.sun.jersey.api.client.Client.create;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 20:49:06
 */
@Stateless(name = "UserSyncBean")
public class UserSyncBean {
    private static final Logger log = LoggerFactory.getLogger(UserSyncBean.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    public void processUser() throws NotRegisteredException {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JSONResolver.JAXBContextResolver.class);
        clientConfig.getClasses().add(JSONResolver.UnmarshallerContextResolver.class);
                
        String syncServerUrl = ":)";

        try {
            //FIX inject by @Resource(name = "syncServerUrl") return null
            syncServerUrl = (String) new InitialContext().lookup("java:module/env/syncServerUrl");
        } catch (NamingException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        String secureKey = clientBean.getCurrentSecureKey();

        //Загрузка с сервера списка пользователей
        List<User> users =  create(clientConfig)
                .resource(syncServerUrl+"/user/list")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(new GenericType<List<User>>(){}, new SyncRequestEntity(secureKey, getUpdated()));

        //Сохранение в базу данных списка пользователей
        for (User user : users){
            int count = em.createQuery("select count(u) from User u where u.id = :id", Long.class)
                    .setParameter("id", user.getId())
                    .getSingleResult()
                    .intValue();

            if (count != 1){
                user.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(user);

            }
        }

        //Загрузка с сервера списка групп пользователей
        List<UserGroup> usergroups =  create(clientConfig)
                .resource(syncServerUrl+"/user/usergroup/list")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(new GenericType<List<UserGroup>>(){}, new SyncRequestEntity(secureKey, getUpdated()));

        log.debug(usergroups.toString());

        //Сохранение в базу данных списка групп пользователей
        for (UserGroup userGroup : usergroups){
            int count = em.createQuery("select count(u) from UserGroup u where u.id = :id", Long.class)
                    .setParameter("id", userGroup.getId())
                    .getSingleResult()
                    .intValue();

            if (count != 1){
                log.debug(userGroup.toString());
                userGroup.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(userGroup);
            }
        }
    }

    private Date getUpdated(){
        Date updated = em.createQuery("select max(u.updated) from User u", Date.class).getSingleResult();
        if (updated == null){
            return new Date(0);
        }

        return updated;
    }
}
