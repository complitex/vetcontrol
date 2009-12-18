package org.vetcontrol.user;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.vetcontrol.web.template.TemplateWebApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 21:30:40
 */
public class UserApplication extends TemplateWebApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return UserHomePage.class; 
    }

    @Override
    public List<Component> getTemplateComponents() {
        List<Component> list = new ArrayList<Component>();
        list.add(new Label("hello_template", "Hello Template"));
        return list;
    }

    @Override
    public IResourceStream getTemplateMarkup() {        
        return null;        
    }
}
