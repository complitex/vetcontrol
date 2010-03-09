package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.ID;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process(){
        processUser();
        processUserGroups();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processTxRequied(){
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
        int index = 0;

        if (count > 0) {
            //Загрузка с сервера списка пользователей
            List<User> users = createJSONClient("/user/list").post(new GenericType<List<User>>(){},
                    new SyncRequestEntity(secureKey, getUpdated(User.class)));

            //Сохранение в базу данных списка пользователей
            index = 0;
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
        }

        complete(new SyncEvent(index, User.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User +++++++++++++++++++\n");
    }

    private void processUserGroups(){
        String secureKey = clientBean.getCurrentSecureKey();
        Date syncUpdated = DateUtil.getCurrentDate();

        log.debug("\n==================== Synchronizing: User Group ============================");

        //Количество удаленных групп пользователей для загрузки
        int count_deleted = createJSONClient("/usergroup/deleted/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(UserGroup.class))).getCount();

        //Количество групп пользователей для загрузки
        int count_updated = createJSONClient("/usergroup/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(UserGroup.class))).getCount();
        start(new SyncEvent(count_deleted + count_updated, UserGroup.class));
        int index = 0;

        if (count_deleted > 0) {
            //Загрузка с сервера списка идентификаторов удаленных групп пользователей
            List<ID> ids = createJSONClient("/usergroup/deleted/list").post(new GenericType<List<ID>>(){},
                    new SyncRequestEntity(secureKey, getUpdated(UserGroup.class)));

            //Удаление групп пользователей
            for (ID id : ids){
                //json protocol feature, skip empty entity
                if (id.getId() == null) continue;

                log.debug("Synchronizing Delete User Group: {}", id);
                sync(new SyncEvent(count_deleted + count_updated, index++, id));

                em.createQuery("delete from UserGroup where id = :id").setParameter("id", id.getId()).executeUpdate();
            }
        }

        if (count_updated > 0 ) {
            //Загрузка с сервера списка групп пользователей
            List<UserGroup> userGroups = createJSONClient("/usergroup/list").post(new GenericType<List<UserGroup>>(){},
                    new SyncRequestEntity(secureKey, getUpdated(UserGroup.class)));

            //Сохранение в базу данных списка групп пользователей
            index = 0;
            for (UserGroup userGroup : userGroups){
                //json protocol feature, skip empty entity
                if (userGroup.getId() == null) continue;

                log.debug("Synchronizing: {}", userGroup.toString());
                sync(new SyncEvent(count_deleted + count_updated, index++, userGroup));

                userGroup.setUpdated(syncUpdated);

                if (isPersisted(userGroup)){
                    userGroup.getUpdateQuery(em).executeUpdate();
                }else{
                    try {
                        userGroup.getInsertQuery(em).executeUpdate();
                    } catch (Exception e) {
                        //remove duplicates
                        em.createQuery("delete from UserGroup where login = :login and securityGroup = :securityGroup")
                                .setParameter("login", userGroup.getLogin())
                                .setParameter("securityGroup", userGroup.getSecurityGroup())
                                .executeUpdate();
                        userGroup.getUpdateQuery(em).executeUpdate();
                    }
                }
            }
        }

        if (count_deleted > 0 && count_updated == 0){
            em.createQuery("update UserGroup set updated = :updated where updated = :max")
                    .setParameter("updated", syncUpdated)
                    .setParameter("max", getUpdated(UserGroup.class))
                    .executeUpdate();
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
