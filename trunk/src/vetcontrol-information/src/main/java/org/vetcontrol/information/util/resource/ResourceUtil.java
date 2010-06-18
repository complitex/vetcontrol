/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.wicket.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class ResourceUtil {

    public static final String COMMON_RESOURCE_BUNDLE = "org.vetcontrol.information.resource.CommonResources";

    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    public static String getString(Component component, String key) {
        return org.vetcontrol.util.resource.ResourceUtil.getString(component, key);
    }

    public static String getString(String bundle, String key, Locale locale, boolean printErrorOnMissingResource) {
        try {
            return org.vetcontrol.util.resource.ResourceUtil.getString(bundle, key, locale);
        } catch (MissingResourceException e) {
            if (printErrorOnMissingResource) {
                log.error("Couldn't to find resource message. Bundle : '{}', Key : '{}', Locale : '{}'", new Object[]{bundle, key, locale});
                log.error("", e);
            }
        }
        return null;
    }
}
