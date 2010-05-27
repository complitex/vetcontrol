package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.sync.client.service.exception.DBOperationException;
import org.vetcontrol.sync.client.service.exception.NetworkConnectionException;
import org.vetcontrol.sync.client.service.exception.SyncException;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityWebListener;

import javax.ejb.*;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.Locale;
import java.util.concurrent.Future;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.02.2010 18:32:08
 */
@Singleton(name = "SyncBean")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SyncBean {
    private static final Logger log = LoggerFactory.getLogger(SyncBean.class);

    @EJB(beanName = "BookSyncBean")
    BookSyncBean bookSyncBean;

    @EJB(beanName = "UserSyncBean")
    UserSyncBean userSyncBean;

    @EJB(beanName = "DocumentCargoSyncBean")
    private DocumentCargoSyncBean documentCargoSyncBean;

    @EJB(beanName = "ArrestDocumentSyncBean")
    private ArrestDocumentSyncBean arrestDocumentSyncBean;

    @EJB(beanName = "LogBean")
    LogBean logBean;

    @EJB(beanName = "LogSyncBean")
    private LogSyncBean logSyncBean;

    @EJB(beanName = "UpdateSyncBean")
    private UpdateSyncBean updateSyncBean;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    private ResourceBundle rb;
    private boolean processing = false;
    private List<SyncMessage> syncMessages = new ArrayList<SyncMessage>();
    private boolean logout = false;
    private ISyncListener syncListener = new ISyncListener() {

        @Override
        public void start(SyncEvent syncEvent) {
            SyncMessage message = new SyncMessage();

            String key = syncEvent.getObject() instanceof DeletedEmbeddedId
                    ? ((DeletedEmbeddedId) syncEvent.getObject()).getId().getEntity()
                    : ((Class) syncEvent.getObject()).getCanonicalName();

            message.setName(rb.containsKey(key) ? rb.getString(key) : key);

            if (syncEvent.getObject() instanceof DeletedEmbeddedId) {
                message.setName(message.getName() + " " + rb.getString("sync.client.sync.delete"));
            }

            message.setMessage(rb.getString("sync.client.sync.start"));

            syncMessages.add(message);
        }

        @Override
        public void sync(SyncEvent syncEvent) {
            SyncMessage message = syncMessages.get(syncMessages.size() - 1);
            message.setDate(DateUtil.getCurrentDate());
            message.setMessage(rb.getString("sync.client.sync.process") + " "
                    + ((syncEvent.getIndex() * 100) / syncEvent.getCount()) + "%");

        }

        @Override
        public void complete(SyncEvent syncEvent) {
            SyncMessage message = syncMessages.get(syncMessages.size() - 1);
            message.setDate(DateUtil.getCurrentDate());

            String m = syncEvent.getCount() > 0
                    ? rb.getString("sync.client.sync.complete") + " " + syncEvent.getCount()
                    : rb.getString("sync.client.sync.complete.skip");
            message.setMessage(m);

            if (User.class.equals(syncEvent.getObject()) || UserGroup.class.equals(syncEvent.getObject())) {
                //Деактивация сессии пользователя
                if (syncEvent.getCount() > 0) {
                    message = new SyncMessage();
                    message.setName(rb.getString("sync.client.sync.client"));
                    message.setMessage(rb.getString("sync.client.sync.logoff"));
                    syncMessages.add(message);
                    logout = true;
                }

                //fix insert log lock by user
                logBean.infoTxRequired(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, (Class) syncEvent.getObject(), m);
            } else {
                logBean.info(Log.MODULE.SYNC_CLIENT, syncEvent.getEvent() == null ? Log.EVENT.SYNC : syncEvent.getEvent(),
                        SyncBean.class, (Class) syncEvent.getObject(), m);
            }

            log.info("Синхронизация объекта: {}. " + m, syncEvent.getObject());
        }
    };

    public List<SyncMessage> getSyncMessages() {
        List<SyncMessage> list = new ArrayList<SyncMessage>(syncMessages);
        Collections.reverse(list);
        return list;
    }

    public boolean isProcessing() {
        return processing;
    }

    @Asynchronous
    public Future<String> asynchronousProcess(Locale locale) {
        rb = ResourceBundle.getBundle("org.vetcontrol.sync.client.service.SyncBean", locale);
        syncMessages.clear();

        bookSyncBean.setSyncListener(syncListener);
        userSyncBean.setSyncListener(syncListener);
        documentCargoSyncBean.setSyncListener(syncListener);
        arrestDocumentSyncBean.setSyncListener(syncListener);
        logSyncBean.setSyncListener(syncListener);
        updateSyncBean.setSyncListener(syncListener);

        bookSyncBean.setInitial(getLastSync() == null);

        processing = true;

        try {
            SyncMessage message = new SyncMessage();
            message.setName(rb.getString("sync.client.sync.client"));
            message.setMessage(rb.getString("sync.client.sync.before_start"));
            syncMessages.add(message);

            //Синхронизация справочников
            bookSyncBean.process();

            //Синхронизация пользователей
            userSyncBean.process();

            //Синхронизация документов
            documentCargoSyncBean.process();

            //Синхронизация актов задержания грузов
            arrestDocumentSyncBean.process();

            //Синхронизация лога документов
            logSyncBean.process();

            //Синхронизация доступных обновлений
            updateSyncBean.process();

            //Установка даты последней синхронизации с сервером
            clientBean.saveCurrentLastSync(DateUtil.getCurrentDate());

            message = new SyncMessage();
            message.setName(rb.getString("sync.client.sync.client"));
            message.setMessage(rb.getString("sync.client.sync.after_complete"));
            syncMessages.add(message);

            if (logout) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage());
                }
                for (HttpSession session : SecurityWebListener.getSessions()) {
                    session.invalidate();
                }
                logout = false;
            }
        } catch (EJBException exception) {
            SyncMessage message = new SyncMessage();

            try {
                if (exception.getCausedByException() instanceof EJBException) {
                    throw ((EJBException) exception.getCausedByException()).getCausedByException();
                }
                throw exception.getCausedByException();
            } catch (NotRegisteredException e) {
                String m = rb.getString("sync.client.error.not_registered");

                message.setName(rb.getString("sync.client.sync.error"));
                message.setMessage(m);

                log.error(m);
                logBean.error(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, Client.class, m);
            } catch (UniformInterfaceException e) {
                String m = e.getResponse().getEntity(String.class);

                message.setName(rb.getString("sync.client.sync.error"));

                if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                    message.setMessage(rb.getString("NOT_FOUND"));
                } else {
                    message.setMessage(m);
                }

                log.error(m);
                logBean.error(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, null, m);
            } catch (SyncException e) {
                message.setName(rb.getString("sync.client.sync.error"));
                if (e instanceof NetworkConnectionException) {
                    message.setMessage(rb.getString("sync.client.sync.error.common"));
                } else if (e instanceof DBOperationException) {
                    message.setMessage(rb.getString("sync.client.sync.error.db"));
                }

                log.error(message.getMessage());
                logBean.error(Log.MODULE.SYNC_CLIENT, e.getEvent(), SyncBean.class, e.getModelClass(), message.getMessage());
            } catch (Exception e) {
                message.setName(rb.getString("sync.client.sync.error"));
                message.setMessage(rb.getString("sync.client.sync.error.common"));

                log.error(e.getLocalizedMessage(), e);
                logBean.error(Log.MODULE.SYNC_CLIENT, Log.EVENT.SYNC, SyncBean.class, null, e.getLocalizedMessage());
            }
            syncMessages.add(message);
        }

        processing = false;

        return new AsyncResult<String>("COMPLETE");
    }

    public Date getLastSync() {
        return clientBean.getCurrentClient().getLastSync();
    }
}
