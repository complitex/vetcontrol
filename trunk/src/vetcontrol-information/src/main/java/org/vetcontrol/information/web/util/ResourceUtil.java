/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class ResourceUtil {

    public static final String BOOK_NAMES_BUNDLE = "org.vetcontrol.information.web.pages.Books";
    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    public static String getString(String key, Component component) {
        try {
            return Application.get().getResourceSettings().getLocalizer().getString(key, component);
        } catch (MissingResourceException e) {
            log.error("error with finding resource", e);
            return null;
        }
    }

    public static String getString(String key, Component component, IModel<?> model) {
        try {
            return new StringResourceModel(key, component, model).getString();
        } catch (MissingResourceException e) {
            log.error("error with finding resource", e);
            return null;
        }
    }

    public static String getString(String bundle, String key, Locale locale) {
        String result = key;
        try {
            result = getResourceBundle(bundle, locale).getString(key);
        } catch (MissingResourceException e) {
            log.error("error with finding resource", e);
        }
        return result;
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
