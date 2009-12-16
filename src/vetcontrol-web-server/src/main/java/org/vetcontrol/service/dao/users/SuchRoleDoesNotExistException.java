/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao.users;

import org.vetcontrol.service.dao.UserException;

/**
 *
 * @author Artem
 */
public class SuchRoleDoesNotExistException extends UserException {

    private String role;

    public SuchRoleDoesNotExistException(String message, String role) {
        super(message);
        this.role = role;
    }

    public String getRole() {
        return role;
    }
    
    
}
