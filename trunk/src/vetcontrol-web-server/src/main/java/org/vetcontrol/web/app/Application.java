/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.web.app;

import javax.naming.InitialContext;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;
import org.vetcontrol.service.fasade.app.AuthenticateUserFasade;
import org.vetcontrol.web.pages.ApplicationSignInPage;
import org.vetcontrol.web.pages.StartPage;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 *
 * @author Artem
 */
public class Application extends AuthenticatedWebApplication {

    private WiQueryInstantiationListener wiQueryInstantiationListener;

    @Override
    protected void init() {
        wiQueryInstantiationListener = new WiQueryInstantiationListener();
        addComponentInstantiationListener(wiQueryInstantiationListener);
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
        super.init();      
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return StartPage.class;
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return ApplicationWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return ApplicationSignInPage.class;
    }

}
