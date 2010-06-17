/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;

/**
 *
 * @author Artem
 */
public abstract class DocumentComparator<T> extends AbstractEntityComparator<T> {

    private static final Logger log = LoggerFactory.getLogger(DocumentComparator.class);

    private final ResourceBundle resourceBundle;

    private final String bundle;

    public DocumentComparator(Class entityClass, Locale systemLocale, String resourceBundle) {
        super(entityClass, systemLocale);
        this.bundle = resourceBundle;
        this.resourceBundle = initResourceBundle(resourceBundle);
    }

    protected String getString(String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            log.error("Couldn't to find resource message. Bundle: " + bundle + ", key: " + key, e);
        }
        return "";
    }

    protected ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private ResourceBundle initResourceBundle(String bundle) {
        try {
            return ResourceBundle.getBundle(bundle, getSystemLocale());
        } catch (Exception e) {
            log.error("Couldn't to find resource bundle {}", bundle);
            throw new RuntimeException(e);
        }
    }
}
