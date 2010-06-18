/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.book.BookTypes;
import org.vetcontrol.information.web.util.BookWebInfoContainer;

/**
 *
 * @author Artem
 */
public class InformationModuleResourceLoader implements IStringResourceLoader {

    private static final List<Class> INFORMATION_MODULE_PAGES;

    static {
        INFORMATION_MODULE_PAGES = new ArrayList<Class>();
        for (Class bookType : BookTypes.all()) {
            INFORMATION_MODULE_PAGES.add(BookWebInfoContainer.getListPage(bookType));
            INFORMATION_MODULE_PAGES.add(BookWebInfoContainer.getEditPage(bookType));
        }
    }

    private static final Logger log = LoggerFactory.getLogger(InformationModuleResourceLoader.class);

    @Override
    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style) {
        return null;
    }

    @Override
    public String loadStringResource(Component component, String key) {
        if (component == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Component class : {}, key : {}", new Object[]{component.getClass(), key});
        }

        Page pageOfComponent = component instanceof Page ? (Page) component : component.findParent(Page.class);
        if (pageOfComponent != null) {
            Class pageClass = pageOfComponent.getClass();
            if (INFORMATION_MODULE_PAGES.contains(pageClass)) {
                String value = ResourceUtil.getString(ResourceUtil.COMMON_RESOURCE_BUNDLE, key, Session.get().getLocale(), false);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }
}
