/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class ResourceUtil {

    private ResourceUtil() {
    }

    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    public static String getString(Component component, String key) {
        try {
            return Application.get().getResourceSettings().getLocalizer().getString(key, component);
        } catch (MissingResourceException e) {
//            log.warn("Couldn't to find resource message. Component : '{}', Key : '{}'", component, key);
            throw e;
        }
    }

    public static String getString(String bundle, String key, Locale locale) {
        try {
            return getResourceBundle(bundle, locale).getString(key);
        } catch (MissingResourceException e) {
//            log.warn("Couldn't to find resource message. Bundle : '{}', Key : '{}', Locale : '{}'", new Object[]{bundle, key, locale});
            throw e;
        }
    }

    private static ResourceBundle getResourceBundle(String bundle, Locale locale) {
        try {
            return ResourceBundle.getBundle(bundle, locale);
        } catch (MissingResourceException e) {
            log.warn("Couldn't to find resource bundle. Bundle : '{}', Locale : '{}'", bundle, locale);
            throw e;
        }
    }
}
