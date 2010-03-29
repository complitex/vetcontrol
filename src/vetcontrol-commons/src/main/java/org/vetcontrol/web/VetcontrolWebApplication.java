package org.vetcontrol.web;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import org.apache.wicket.protocol.http.WebRequest;
import org.vetcontrol.web.pages.expired.SessionExpiredPage;
import org.vetcontrol.web.pages.welcome.WelcomePage;
import org.vetcontrol.web.template.TemplateWebApplication;

import javax.servlet.http.HttpServletRequest;

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
        return WelcomePage.class;
    }

    @Override
    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
        return new UploadWebRequest(servletRequest);
    }
}
