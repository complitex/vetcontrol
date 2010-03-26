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
public class NetworkConnectionException extends SyncException {

    public NetworkConnectionException(String message, Throwable cause, Class modelClass, EVENT event) {
        super(message, cause, modelClass, event);
    }
}
