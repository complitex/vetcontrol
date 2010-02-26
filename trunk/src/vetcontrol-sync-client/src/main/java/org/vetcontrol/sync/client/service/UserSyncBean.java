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
import org.vetcontrol.util.DateUtil;

import javax.annotation.Resource;
import javax.ejb.*;
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
public class UserSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(UserSyncBean.class);

    @Resource TimerService timerService;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    public void process() throws NotRegisteredException {
        if (timerService.getTimers().size() == 0){
            timerService.createSingleActionTimer(0, new TimerConfig(null, false));
        }
    }

    @Timeout
    private void monitor(Timer timer) throws NotRegisteredException {
        processUser();
        processUserGroups();
    }

    private void processUser() throws NotRegisteredException {
        String secureKey = clientBean.getCurrentSecureKey();
        Date syncUpdated = DateUtil.getCurrentDate();

        log.debug("\n==================== Synchronizing: User ============================");

        //Количество пользователей для загрузки
        int count = Integer.parseInt(createJSONClient("/user/count")
                .post(String.class, new SyncRequestEntity(secureKey, getUpdated(User.class))));
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

            if (em.find(User.class, user.getId()) == null){
                user.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(user);
            }
        }

        complete(new SyncEvent(index, User.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User +++++++++++++++++++\n");
    }

    private void processUserGroups() throws NotRegisteredException {
        String secureKey = clientBean.getCurrentSecureKey();
        Date syncUpdated = DateUtil.getCurrentDate();

        log.debug("\n==================== Synchronizing: User Group ============================");

        //Количество групп пользователей для загрузки
        int count = Integer.parseInt(createJSONClient("/usergroup/count")
                .post(String.class, new SyncRequestEntity(secureKey, getUpdated(User.class))));
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

            if (em.find(UserGroup.class, userGroup.getId()) == null){
                userGroup.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(userGroup);
            }
        }

        complete(new SyncEvent(index, UserGroup.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: User Group +++++++++++++++++++\n");
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
