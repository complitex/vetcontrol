package org.vetcontrol.web.template;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;
import org.vetcontrol.web.security.ServeltAuthWebApplication;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 22.12.2009 21:18:32
 *
 * Wicket приложение, которое использует независимые модули и единый шаблон.
 */
public abstract class TemplateWebApplication extends ServeltAuthWebApplication {

    private final static String templatePath = "/WEB-INF/vetcontrol-template.xml";
    private TemplateLoader templateLoader;

    @Override
    protected void init() {
        super.init();

        addComponentInstantiationListener(new WiQueryInstantiationListener());
        addComponentInstantiationListener(new JavaEEComponentInjector(this));

        InputStream inputStream = getServletContext().getResourceAsStream(templatePath);
        if (inputStream != null) {
            templateLoader = new TemplateLoader(inputStream);
        }
    }

    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new VetControlSession(request);
    }
}
