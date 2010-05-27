package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncProcess;
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
public class UserSyncBean extends SyncInfo {
    private static final Logger log = LoggerFactory.getLogger(UserSyncBean.class);

    private static final int DB_BATCH_SIZE = 50;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process() {
        Date syncStart = processSyncStart();

        processUser(syncStart);
        processUserGroups(syncStart);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processTxRequied() {
        Date syncStart = processSyncStart();

        processUser(syncStart);
        processUserGroups(syncStart);
    }

    private Date processSyncStart(){
        return createJSONClient("/book/start")
                .post(SyncProcess.class, new SyncRequestEntity(clientBean.getCurrentSecureKey(), DateUtil.getCurrentDate()))
                .getSyncStart();
    }


    private void processUser(Date syncStart) {
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: User ============================");

        //Количество пользователей для загрузки
        int count = createJSONClient("/user/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(User.class), syncStart, null)).getCount();
        start(new SyncEvent(count, User.class));
        int index = 0;

        if (count > 0) {
            //Загрузка с сервера списка пользователей
            List<User> users = createJSONClient("/user/list").post(new GenericType<List<User>>() {},
                    new SyncRequestEntity(secureKey, getUpdated(User.class), syncStart, null));

            //Сохранение в базу данных списка пользователей
            index = 0;
            for (User user : users) {
                //json protocol feature, skip empty entity
                if (user.getId() == null) {
                    continue;
                }

                log.debug("Synchronizing: {}", user.toString());
                sync(new SyncEvent(count, index++, user));

                if (isPersisted(user)) {
                    EntityPersisterUtil.update(em, user);
                } else {
                    EntityPersisterUtil.insert(em, user);
                }

                if (index % DB_BATCH_SIZE == 0){
                    EntityPersisterUtil.executeBatch(em);
                }
            }

            EntityPersisterUtil.executeBatch(em);
        }

        complete(new SyncEvent(index, User.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User +++++++++++++++++++\n");
    }

    private void processUserGroups(Date syncStart) {
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: User Group ============================");

        //Количество удаленных групп пользователей для загрузки
        int count_deleted = createJSONClient("/user/usergroup/deleted/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getDeleted(UserGroup.class), syncStart, null))
                .getCount();

        //Количество групп пользователей для загрузки
        int count_updated = createJSONClient("/user/usergroup/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(UserGroup.class), syncStart, null))
                .getCount();
        start(new SyncEvent(count_deleted + count_updated, UserGroup.class));
        int index = 0;

        if (count_deleted > 0) {
            //Загрузка с сервера списка идентификаторов удаленных групп пользователей
            List<DeletedLongId> ids = createJSONClient("/user/usergroup/deleted/list")
                    .post(new GenericType<List<DeletedLongId>>() {},
                            new SyncRequestEntity(secureKey, getDeleted(UserGroup.class), syncStart, null));

            Date serverMaxDeletedDate = null;
            //Удаление групп пользователей
            for (DeletedLongId id : ids) {
                //json protocol feature, skip empty entity
                if (id.getId() == null || id.getId().getId() == null) {
                    continue;
                }

                if (serverMaxDeletedDate == null || serverMaxDeletedDate.before(id.getDeleted())) {
                    serverMaxDeletedDate = id.getDeleted();
                }

                log.debug("Synchronizing Delete User Group: {}", id.getId().getId());
                sync(new SyncEvent(count_deleted + count_updated, index++, id));

                em.createQuery("delete from UserGroup where id = :id").setParameter("id", id.getId().getId()).executeUpdate();
            }
            if (serverMaxDeletedDate != null) {
                DeletedLongId deleted = new DeletedLongId(1L, UserGroup.class.getCanonicalName(), serverMaxDeletedDate);
                em.merge(deleted);
            }
        }

        if (count_updated > 0) {
            //Загрузка с сервера списка групп пользователей
            List<UserGroup> userGroups = createJSONClient("/user/usergroup/list").post(new GenericType<List<UserGroup>>() {},
                    new SyncRequestEntity(secureKey, getUpdated(UserGroup.class), syncStart, null));

            //Сохранение в базу данных списка групп пользователей
            index = 0;
            for (UserGroup userGroup : userGroups) {
                //json protocol feature, skip empty entity
                if (userGroup.getId() == null) {
                    continue;
                }

                log.debug("Synchronizing: {}", userGroup.toString());
                sync(new SyncEvent(count_deleted + count_updated, index++, userGroup));

                if (isPersisted(userGroup)) {
                    EntityPersisterUtil.update(em, userGroup);
                } else {
                    try {
                        EntityPersisterUtil.insert(em, userGroup);
                    } catch (Exception e) {
                        //remove duplicates
                        em.createQuery("delete from UserGroup where login = :login and securityGroup = :securityGroup").setParameter("login", userGroup.getLogin()).setParameter("securityGroup", userGroup.getSecurityGroup()).executeUpdate();
                        EntityPersisterUtil.update(em, userGroup);
                    }
                }

                if (index % DB_BATCH_SIZE == 0){
                    EntityPersisterUtil.executeBatch(em);
                }
            }

            EntityPersisterUtil.executeBatch(em);
        }

        complete(new SyncEvent(index, UserGroup.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User Group +++++++++++++++++++\n");
    }

    private boolean isPersisted(Object obj) {
        Object id = null;

        if (obj instanceof ILongId) {
            id = ((ILongId) obj).getId();
        } else if (obj instanceof IEmbeddedId) {
            id = ((IEmbeddedId) obj).getId();
        }

        return em.createQuery("select count(b) from " + obj.getClass().getSimpleName() + " b where b.id = :id", Long.class).setParameter("id", id).getSingleResult() == 1;
    }

    private Date getUpdated(Class<? extends IUpdated> entity) {
        Date updated = em.createQuery("select max(e.updated) from " + entity.getSimpleName() + " e", Date.class).getSingleResult();
        if (updated == null) {
            return new Date(0);
        }

        return updated;
    }

    private Date getDeleted(Class entity) {
        Date updated = em.createQuery("select max(d.deleted) from DeletedLongId d where d.id.entity = :entity", Date.class).
                setParameter("entity", entity.getCanonicalName()).
                getSingleResult();
        if (updated == null) {
            return new Date(0);
        }

        return updated;
    }
}
