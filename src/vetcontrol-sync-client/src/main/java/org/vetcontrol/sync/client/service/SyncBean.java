package org.vetcontrol.sync.client.service;

import org.vetcontrol.util.DateUtil;

import javax.ejb.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.02.2010 18:32:08
 */
@Singleton(name = "SyncBean")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SyncBean{
    @EJB(beanName = "BookSyncBean")
    BookSyncBean bookSyncBean;

    @EJB(beanName = "UserSyncBean")
    UserSyncBean userSyncBean;

    private ResourceBundle rb;

    private boolean processing = false;

    private List<SyncMessage> syncMessages = new ArrayList<SyncMessage>();

    private ISyncListener syncListener = new ISyncListener(){

        @Override
        public void start(SyncEvent syncEvent) {
            SyncMessage message = new SyncMessage();
            String key = ((Class)syncEvent.getObject()).getCanonicalName();
            message.setDate(DateUtil.getCurrentDate());
            message.setName(rb.containsKey(key) ? rb.getString(key) : key);
            message.setMessage("Подключение к серверу... " + syncEvent.getCount());

            syncMessages.add(message);
        }

        @Override
        public void sync(SyncEvent syncEvent) {
            SyncMessage message = syncMessages.get(syncMessages.size()-1);
            message.setDate(DateUtil.getCurrentDate());
            message.setMessage("Синхронизация... " + syncEvent.getIndex() + " из " + syncEvent.getCount());
        }

        @Override
        public void complete(SyncEvent syncEvent) {
            SyncMessage message = syncMessages.get(syncMessages.size()-1);
            message.setDate(DateUtil.getCurrentDate());
            message.setMessage("Успешно синхронизировано: " + syncEvent.getCount());
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

        bookSyncBean.setInitial(true);
        bookSyncBean.setSyncListener(syncListener);
        userSyncBean.setSyncListener(syncListener);

        processing = true;

        try {
            bookSyncBean.process();
            userSyncBean.process();
        } catch (Exception e) {
            SyncMessage message = new SyncMessage();
            message.setDate(DateUtil.getCurrentDate());
            message.setMessage("Ошибка: " + e.getLocalizedMessage());
            syncMessages.add(message);
        }

        processing = false;

        return new AsyncResult<String>("COMPLETE");
    }
}
