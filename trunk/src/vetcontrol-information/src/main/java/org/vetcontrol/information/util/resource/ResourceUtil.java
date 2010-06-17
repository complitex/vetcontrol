/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.resource;

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
public class ResourceUtil {

    public static final String COMMON_RESOURCES_BUNDLE = "org.vetcontrol.information.resource.CommonResources";

    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    public static String getString(Component component, String key) {
        try {
            return Application.get().getResourceSettings().getLocalizer().getString(key, component);
        } catch (MissingResourceException e) {
            log.error("error with finding resource", e);
        }
        return null;
    }

    public static String getString(String bundle, String key, Locale locale, boolean printErrorOnMissingResource) {
        try {
            return getResourceBundle(bundle, locale).getString(key);
        } catch (MissingResourceException e) {
            if (printErrorOnMissingResource) {
                log.error("error with finding resource", e);
            }
        }
        return null;
    }

    private static ResourceBundle getResourceBundle(String bundle, Locale locale) {
        try {
            return ResourceBundle.getBundle(bundle, locale);
        } catch (Exception e) {
            log.error("error with finding bundle", e);
        }
        return null;
    }
}
