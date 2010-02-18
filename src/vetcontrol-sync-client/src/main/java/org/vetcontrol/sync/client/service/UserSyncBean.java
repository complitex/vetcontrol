package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.IUpdated;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

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
        String secureKey = clientBean.getCurrentSecureKey();

        //Загрузка с сервера списка пользователей
        List<User> users = createJSONClient("/user/list").post(new GenericType<List<User>>(){},
                new SyncRequestEntity(secureKey, getUpdated(User.class)));

        //Сохранение в базу данных списка пользователей
        for (User user : users){
            int count = em.createQuery("select count(u) from User u where u.id = :id", Long.class)
                    .setParameter("id", user.getId())
                    .getSingleResult()
                    .intValue();

            //json protocol feature, skip empty entity
            if (user.getId() == null) continue;

            if (count != 1){
                user.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(user);

            }
        }

        //Загрузка с сервера списка групп пользователей
        List<UserGroup> usergroups = createJSONClient("/user/usergroup/list").post(new GenericType<List<UserGroup>>(){}, 
                new SyncRequestEntity(secureKey, getUpdated(UserGroup.class)));

        //Сохранение в базу данных списка групп пользователей
        for (UserGroup userGroup : usergroups){
            int count = em.createQuery("select count(u) from UserGroup u where u.id = :id", Long.class)
                    .setParameter("id", userGroup.getId())
                    .getSingleResult()
                    .intValue();

            //json protocol feature, skip empty entity
            if (userGroup.getId() == null) continue;

            if (count != 1){
                log.debug(userGroup.toString());
                userGroup.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(userGroup);
            }
        }
    }

    private Date getUpdated(Class<? extends IUpdated> entity){
        Date updated = em.createQuery("select max(e.updated) from " + entity.getSimpleName() +" e", Date.class)
                .getSingleResult();
        if (updated == null){
            return new Date(0);
        }

        return updated;
    }
}
