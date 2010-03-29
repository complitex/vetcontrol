package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Update;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.03.2010 5:21:44
 */
@Singleton(name = "UpdateSyncBean")
public class UpdateSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(UpdateSyncBean.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    public void process(){
        log.debug("\n==================== Synchronizing: Update ============================");
        start(new SyncEvent(0, Update.class));

        String secureKey = clientBean.getCurrentSecureKey();

        //Максимальная дата создания доступных обновлений в базе клиента
        Date created =  em.createQuery("select max(u.created) from Update u", Date.class).getSingleResult();
        if (created == null){
            created = new Date(0);
        }

        //Текущая версия клиента
        String version = clientBean.getCurrentClient().getVersion();

        //TODO set client version on registration
        if (version == null){
            version = "";
        }

        List<Update> list = ClientFactory.createJSONClient("/update/list")
                .post(new GenericType<List<Update>>(){}, new SyncRequestEntity(secureKey, created, null, version));

        int count = list.size();

        int index = 0;

        for (Update update : list){
            log.debug("Synchronizing: {}", update.toString());
            sync(new SyncEvent(count, index++, update));

            removeIfExist(update);
            em.merge(update);
        }

        complete(new SyncEvent(index, Update.class));
        log.debug("++++++++++++++++++++ Synchronizing Complete: Update +++++++++++++++++++\n");
    }

    private void removeIfExist(Update update){
        Long count = em.createQuery("select count(*) from Update u where u.version = :version", Long.class)
                .setParameter("version", update.getVersion())
                .getSingleResult();

        if (count > 0){
            em.createQuery("delete from UpdateItem ui where ui.update.version = :version")
                    .setParameter("version", update.getVersion())
                    .executeUpdate();
            em.createQuery("delete from Update u where u.version = :version")
                    .setParameter("version", update.getVersion())
                    .executeUpdate();
        }
    }

}
