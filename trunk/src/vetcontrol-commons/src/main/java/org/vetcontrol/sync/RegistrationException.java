package org.vetcontrol.sync;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.02.2010 16:17:11
 */
public class RegistrationException extends Exception{
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
