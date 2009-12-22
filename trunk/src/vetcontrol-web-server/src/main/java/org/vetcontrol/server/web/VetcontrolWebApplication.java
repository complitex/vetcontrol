package org.vetcontrol.server.web;

import java.util.ArrayList;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.vetcontrol.server.web.pages.HomePage;
import org.vetcontrol.server.web.template.Template;
import org.vetcontrol.web.template.TemplateWebApplication;

import java.util.List;
import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;
import org.vetcontrol.server.web.component.AvailableBooksPanel;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class VetcontrolWebApplication extends TemplateWebApplication{

    private List<Component> templateComponents;

    @Override
    protected void init() {
        addComponentInstantiationListener(new WiQueryInstantiationListener());
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
        super.init();

        // TODO: add locale support, menu visible by role, header info
    }

    @Override
    public List<Component> getTemplateComponents() {        
        templateComponents = new ArrayList<Component>();
        templateComponents.add(new AvailableBooksPanel("BooksPanel"));

        return templateComponents;
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
