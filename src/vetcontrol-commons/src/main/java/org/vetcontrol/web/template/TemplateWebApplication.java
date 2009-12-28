package org.vetcontrol.web.template;

import org.vetcontrol.web.security.ServeltAuthWebApplication;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 22.12.2009 21:18:32
 *
 * Wicket приложение, которое использует независимые модули и единый шаблон.
 */
public abstract class TemplateWebApplication extends ServeltAuthWebApplication{
    private final static String templatePath = "/WEB-INF/vetcontrol-template.xml";
    private TemplateLoader templateLoader;

    @Override
    protected void init() {
        super.init();

        InputStream inputStream = getServletContext().getResourceAsStream(templatePath);
        if (inputStream != null){
            templateLoader = new TemplateLoader(inputStream);
        }
    }

    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }
}
