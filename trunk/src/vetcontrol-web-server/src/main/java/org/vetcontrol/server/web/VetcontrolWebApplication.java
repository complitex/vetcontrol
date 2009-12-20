package org.vetcontrol.server.web;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.vetcontrol.server.web.pages.HomePage;
import org.vetcontrol.server.web.template.Template;
import org.vetcontrol.web.template.TemplateWebApplication;

import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class VetcontrolWebApplication extends TemplateWebApplication{
    @Override
    protected void init() {
        super.init();
        // TODO: add locale support, menu visible by role, header info
    }

    @Override
    public List<Component> getTemplateComponents() {        
        return null;
    }

    @Override
    public IResourceStream getTemplateMarkup() {
        return new UrlResourceStream(Template.class.getResource("template.html"));
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}
