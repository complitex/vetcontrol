package org.vetcontrol.sync.client.service;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.02.2010 18:32:08
 */
@Stateless(name = "SyncBean")
public class SyncBean extends SyncInfo{
    @EJB(beanName = "BookSyncBean")
    BookSyncBean bookSyncBean;

    @EJB(beanName = "UserSyncBean")
    UserSyncBean userSyncBean;

    @Asynchronous
    public Future<SyncStatus> asynchronousProcess() throws ExecutionException {
        bookSyncBean.setInitial(true);
        bookSyncBean.setSyncListener(this.getSyncListener());
        bookSyncBean.process();

        userSyncBean.setSyncListener(this.getSyncListener());
        userSyncBean.process();

        return new AsyncResult<SyncStatus>(SyncStatus.COMPLETE);
    }
}
