package org.vetcontrol.web;

import org.apache.wicket.Page;
import org.vetcontrol.web.pages.HomePage;
import org.vetcontrol.web.pages.SessionExpiredPage;
import org.vetcontrol.web.template.TemplateWebApplication;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class VetcontrolWebApplication extends TemplateWebApplication {

    @Override
    protected void init() {
        getApplicationSettings().setPageExpiredErrorPage(SessionExpiredPage.class);
        super.init();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

}
