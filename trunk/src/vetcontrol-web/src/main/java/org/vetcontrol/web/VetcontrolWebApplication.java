package org.vetcontrol.web;

import org.apache.wicket.Page;
import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;
import org.vetcontrol.web.pages.HomePage;
import org.vetcontrol.web.template.TemplateWebApplication;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class VetcontrolWebApplication extends TemplateWebApplication {

    @Override
    protected void init() {
        super.init();
        addComponentInstantiationListener(new WiQueryInstantiationListener());
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}
