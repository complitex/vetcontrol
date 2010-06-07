/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import org.vetcontrol.entity.Client;

/**
 *
 * @author Artem
 */
public class Statistica {

    private float requestTime;

    private Exception exception;

    private Client client;

    public Statistica(float requestTime, Client client) {
        this.requestTime = requestTime;
        this.client = client;
    }

    public Statistica(float requestTime, Client client, Exception exception) {
        this.requestTime = requestTime;
        this.client = client;
        this.exception = exception;
    }

    public float getRequestTime() {
        return requestTime;
    }

    public Exception getException() {
        return exception;
    }

    public Client getClient() {
        return client;
    }
}
