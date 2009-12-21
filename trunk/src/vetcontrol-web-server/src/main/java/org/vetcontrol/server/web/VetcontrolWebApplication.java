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
import org.vetcontrol.server.web.component.AvailableBooksPanel;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class VetcontrolWebApplication extends TemplateWebApplication{

    private List<Component> templateComponents = new ArrayList<Component>();

    @Override
    protected void init() {
        super.init();

        addBooksPanelComponent();
        // TODO: add locale support, menu visible by role, header info
    }

    @Override
    public List<Component> getTemplateComponents() {        
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

    private void addBooksPanelComponent() {
        templateComponents.add(new AvailableBooksPanel("BooksPanel"));
    }
}
