/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.sync.client.service.exception;

import org.vetcontrol.entity.Log.EVENT;

/**
 *
 * @author Artem
 */
public class SyncException extends RuntimeException {

    private Class modelClass;
    private EVENT event;

    public SyncException(String message, Throwable cause, Class modelClass, EVENT event) {
        super(message, cause);
        this.modelClass = modelClass;
        this.event = event;
    }

    public Class getModelClass() {
        return modelClass;
    }

    public EVENT getEvent() {
        return event;
    }
}
