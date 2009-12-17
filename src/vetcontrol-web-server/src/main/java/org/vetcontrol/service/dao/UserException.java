/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao;

import javax.ejb.ApplicationException;

/**
 *
 * @author Artem
 */
@ApplicationException(rollback=true)
public class UserException extends Exception {

    public UserException(String message) {
	super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

}
