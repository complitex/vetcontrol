package org.vetcontrol.web.login;

import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequest;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 20:18:49
 */
public abstract class BaseLoginApplication extends AuthenticatedWebApplication {

    @Override
    protected void init() {
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
        super.init();
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return LoginWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    @Override
    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
        final WebRequest webRequest = super.newWebRequest(servletRequest);
        final LoginWebSession session = (LoginWebSession)getSessionStore().lookup(webRequest);
        if (session != null){
            session.setSingIn(servletRequest.getUserPrincipal() != null);
        }

        return webRequest;
    }
}
