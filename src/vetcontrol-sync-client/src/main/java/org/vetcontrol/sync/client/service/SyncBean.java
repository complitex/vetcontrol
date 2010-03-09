package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityWebListener;

import javax.ejb.*;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.02.2010 18:32:08
 */
@Singleton(name = "SyncBean")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SyncBean{
    private static final Logger log = LoggerFactory.getLogger(SyncBean.class);

    @EJB(beanName = "BookSyncBean")
    BookSyncBean bookSyncBean;

    @EJB(beanName = "UserSyncBean")
    UserSyncBean userSyncBean;
    @EJB(beanName = "DocumentCargoSyncBean")
    private DocumentCargoSyncBean documentCargoSyncBean;

    @EJB(beanName = "LogBean")
    LogBean logBean;

    private ResourceBundle rb;

    private boolean processing = false;

    private List<SyncMessage> syncMessages = new ArrayList<SyncMessage>();

    private boolean logout = false;

    private ISyncListener syncListener = new ISyncListener(){

        @Override
        public void start(SyncEvent syncEvent) {
            SyncMessage message = new SyncMessage();
            String key = ((Class)syncEvent.getObject()).getCanonicalName();
            message.setName(rb.containsKey(key) ? rb.getString(key) : key);
            message.setMessage(rb.getString("sync.client.sync.start"));

            syncMessages.add(message);
        }

        @Override
        public void sync(SyncEvent syncEvent) {
            SyncMessage message = syncMessages.get(syncMessages.size()-1);
            message.setDate(DateUtil.getCurrentDate());
            message.setMessage(rb.getString("sync.client.sync.process") + " " +
                    ((syncEvent.getIndex()*100) / syncEvent.getCount()) + "%");

        }

        @Override
        public void complete(SyncEvent syncEvent) {
            SyncMessage message = syncMessages.get(syncMessages.size()-1);
            message.setDate(DateUtil.getCurrentDate());

            String m = syncEvent.getCount() > 0
                    ? rb.getString("sync.client.sync.complete") + " " + syncEvent.getCount()
                    : rb.getString("sync.client.sync.complete.skip");
            message.setMessage(m);

            if (User.class.equals(syncEvent.getObject()) || UserGroup.class.equals(syncEvent.getObject())){
                //Деактивация сессии пользователя
                if (syncEvent.getCount() > 0){
                    message = new SyncMessage();
                    message.setName(rb.getString("sync.client.sync.client"));
                    message.setMessage(rb.getString("sync.client.sync.logoff"));
                    syncMessages.add(message);
                    logout = true;
                }

                //fix insert log lock by user
                logBean.infoTxRequired(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, (Class)syncEvent.getObject(), m);
            }else{
                logBean.info(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, (Class)syncEvent.getObject(), m);
            }

            log.info("Синхронизация объекта: {}. " + m, syncEvent.getObject());            
        }
    };

    public List<SyncMessage> getSyncMessages() {
        return syncMessages;
    }

    public boolean isProcessing(){
        return processing;
    }

    @Asynchronous
    public Future<String> asynchronousProcess(Locale locale){
        rb = ResourceBundle.getBundle("org.vetcontrol.sync.client.service.SyncBean", locale);
        syncMessages.clear();

        bookSyncBean.setSyncListener(syncListener);
        userSyncBean.setSyncListener(syncListener);
        documentCargoSyncBean.setSyncListener(syncListener);

        processing = true;

        try {
            SyncMessage message = new SyncMessage();
            message.setName(rb.getString("sync.client.sync.client"));
            message.setMessage(rb.getString("sync.client.sync.before_start"));
            syncMessages.add(message);

            //Синхронизация справочников
            for (Class book : BookSyncBean.syncBooks){
                //noinspection unchecked
                bookSyncBean.processBook(book);
            }

            //Синхронизация пользователей
            userSyncBean.process();

            //Синхронизация документов
            documentCargoSyncBean.process();

            message = new SyncMessage();
            message.setName(rb.getString("sync.client.sync.client"));
            message.setMessage(rb.getString("sync.client.sync.after_complete"));
            syncMessages.add(message);

            if (logout){
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage());
                }
                for(HttpSession session : SecurityWebListener.getSessions()){
                    session.invalidate();
                }
                logout = false;
            }
        } catch (EJBException exception) {
            SyncMessage message = new SyncMessage();

            try {
                if (exception.getCausedByException() instanceof EJBException){
                    throw ((EJBException)exception.getCausedByException()).getCausedByException();
                }
                throw exception.getCausedByException();
            } catch (NotRegisteredException e) {
                String m = rb.getString("sync.client.error.not_registered");

                message.setName(rb.getString("sync.client.sync.error"));
                message.setMessage(m);

                log.error(m);
                logBean.error(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, Client.class, m);
            } catch (UniformInterfaceException e){
                String m = e.getResponse().getEntity(String.class);

                message.setName(rb.getString("sync.client.sync.error"));
                message.setMessage(m);

                log.error(m);
                logBean.error(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, null, m);
            } catch (Exception e) {
                message.setName(rb.getString("sync.client.sync.error"));
                message.setMessage(e.getLocalizedMessage());

                log.error(e.getLocalizedMessage(), e);
                logBean.error(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, null, e.getLocalizedMessage());                
            }

            syncMessages.add(message);
        }

        processing = false;

        return new AsyncResult<String>("COMPLETE");
    }

    public Date getLastSync(){
        return logBean.getLastDate(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, Log.STATUS.OK);
    }
}
