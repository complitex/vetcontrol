package org.vetcontrol.sync.client.service;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.02.2010 22:41:28
 */
public class SyncEvent {
    private int count;

    private int index;

    private Object object;

    private String message;

    public SyncEvent() {
    }

    public SyncEvent(int count, Object object) {
        this.count = count;
        this.object = object;
    }

    public SyncEvent(int count, int index, Object object) {
        this.count = count;
        this.index = index;
        this.object = object;
    }

    public SyncEvent(Object object, String message) {
        this.object = object;
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
