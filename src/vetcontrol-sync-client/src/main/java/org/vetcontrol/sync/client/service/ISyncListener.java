package org.vetcontrol.sync.client.service;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.02.2010 22:40:57
 */
public interface ISyncListener {
    void start(SyncEvent syncEvent);

    void sync(SyncEvent syncEvent);

    void complete(SyncEvent syncEvent);
}
