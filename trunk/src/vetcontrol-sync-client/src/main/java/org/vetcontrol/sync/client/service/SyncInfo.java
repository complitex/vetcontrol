package org.vetcontrol.sync.client.service;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.02.2010 22:40:40
 */
public abstract class SyncInfo {
    private ISyncListener syncListener;

    public ISyncListener getSyncListener() {
        return syncListener;
    }

    public  void setSyncListener(ISyncListener syncListener) {
        this.syncListener = syncListener;
    }

    protected void start(SyncEvent syncEvent){
        if (syncListener != null) {
            syncListener.start(syncEvent);
        }
    }

    protected void sync(SyncEvent syncEvent){
        if (syncListener != null) {
            syncListener.sync(syncEvent);
        }
    }

    protected void complete(SyncEvent syncEvent){
        if (syncListener != null) {
            syncListener.complete(syncEvent);
        }
    }
}
