package org.vetcontrol.web.login;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebRequestCycle;

import javax.security.auth.login.LoginContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 20:40:54
 */
public class LoginWebSession extends AuthenticatedWebSession {
    public LoginWebSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(String username, String password) {
       return username == null && password == null;
    }

    @Override
    public Roles getRoles() {
        HttpServletRequest request = ((WebRequestCycle) RequestCycle.get()).getWebRequest().getHttpServletRequest();        
        if (request.isUserInRole("admin")){
            return new Roles("ADMIN");
        }
        return null;
    }

    public void setSingIn(boolean signedIn){
        if (signedIn){
            signIn(null, null);
        }else{
            signOut();
        }
    }


}
