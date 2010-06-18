/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;
import org.vetcontrol.util.resource.ResourceUtil;

/**
 *
 * @author Artem
 */
public abstract class DocumentComparator<T> extends AbstractEntityComparator<T> {

    private static final Logger log = LoggerFactory.getLogger(DocumentComparator.class);

    public DocumentComparator(Class entityClass, Locale systemLocale) {
        super(entityClass, systemLocale);
    }

    protected String getString(String key) {
        return ResourceUtil.getString(getResourceBundle(), key, getSystemLocale());
    }

    protected abstract String getResourceBundle();
}
