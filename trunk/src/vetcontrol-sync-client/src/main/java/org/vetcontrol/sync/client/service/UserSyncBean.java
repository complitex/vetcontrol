package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 20:49:06
 */
@Singleton(name = "UserSyncBean")
public class UserSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(UserSyncBean.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    public void process(){
        processUser();
        processUserGroups();
    }

    private void processUser(){
        String secureKey = clientBean.getCurrentSecureKey();
        Date syncUpdated = DateUtil.getCurrentDate();

        log.debug("\n==================== Synchronizing: User ============================");

        //Количество пользователей для загрузки
        int count = createJSONClient("/user/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(User.class))).getCount();
        start(new SyncEvent(count, User.class));

        //Загрузка с сервера списка пользователей
        List<User> users = createJSONClient("/user/list").post(new GenericType<List<User>>(){},
                new SyncRequestEntity(secureKey, getUpdated(User.class)));

        //Сохранение в базу данных списка пользователей
        int index = 0;
        for (User user : users){
            //json protocol feature, skip empty entity
            if (user.getId() == null) continue;

            log.debug("Synchronizing: {}", user.toString());
            sync(new SyncEvent(count, index++, user));

            user.setUpdated(syncUpdated);

            if (isPersisted(user)){
                user.getUpdateQuery(em).executeUpdate();
            }else{
                user.getInsertQuery(em).executeUpdate();
            }
        }

        complete(new SyncEvent(index, User.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User +++++++++++++++++++\n");
    }

    private void processUserGroups(){
        String secureKey = clientBean.getCurrentSecureKey();
        Date syncUpdated = DateUtil.getCurrentDate();

        log.debug("\n==================== Synchronizing: User Group ============================");

        //Количество групп пользователей для загрузки
        int count = createJSONClient("/usergroup/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(UserGroup.class))).getCount();
        start(new SyncEvent(count, UserGroup.class));

        //Загрузка с сервера списка групп пользователей
        List<UserGroup> userGroups = createJSONClient("/usergroup/list").post(new GenericType<List<UserGroup>>(){},
                new SyncRequestEntity(secureKey, getUpdated(UserGroup.class)));

        //Сохранение в базу данных списка групп пользователей
        int index = 0;
        for (UserGroup userGroup : userGroups){
            //json protocol feature, skip empty entity
            if (userGroup.getId() == null) continue;

            log.debug("Synchronizing: {}", userGroup.toString());
            sync(new SyncEvent(count, index++, userGroup));

            userGroup.setUpdated(syncUpdated);

            if (isPersisted(userGroup)){
                userGroup.getUpdateQuery(em).executeUpdate();                
            }else{
                userGroup.getInsertQuery(em).executeUpdate();
            }
        }

        complete(new SyncEvent(index, UserGroup.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User Group +++++++++++++++++++\n");
    }

    private boolean isPersisted(Object obj){
        Object id = null;

        if (obj instanceof ILongId){
            id = ((ILongId) obj).getId();
        }else if (obj instanceof IEmbeddedId){
            id = ((IEmbeddedId) obj).getId();
        }

        return em.createQuery("select count(*) from " + obj.getClass().getSimpleName() + " b where b.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult() == 1;
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
