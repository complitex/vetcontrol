/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.app;

import javax.naming.InitialContext;
import org.vetcontrol.model.beans.Role;
import org.vetcontrol.model.beans.User;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.vetcontrol.service.fasade.app.AuthenticateUserFasade;

/**
 *
 * @author Artem
 */
public class ApplicationWebSession extends AuthenticatedWebSession {

    private AuthenticateUserFasade authenticateUserFasade;
    private User user;

    public ApplicationWebSession(Request request) {
        super(request);

        try {
            InitialContext ic = new InitialContext();
            authenticateUserFasade = (AuthenticateUserFasade) ic.lookup("java:module/AuthenticateUserFasade");
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public boolean authenticate(String username, String password) {

        user = authenticateUserFasade.getUserByName(username);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }

        return false;
    }

    @Override
    public Roles getRoles() {
        if (isSignedIn()) {
            Roles roles = new Roles();
            for (Role role : user.getRoles()) {
                roles.add(role.getId().getRole());
            }
            return roles;
        }
        return null;
    }
}
