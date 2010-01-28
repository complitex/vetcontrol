/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.vetcontrol.information.util.web.ResourceUtil;

/**
 *
 * @author Artem
 */
@Deprecated
public class BookPropertyResourceKeyModel extends AbstractReadOnlyModel<String> {

    private static final String SEPARATOR = ".";
    private static final String SUFFIX = "prop";
    private Class<?> clazz;
    private String property;
    private Component component;
    private String suffix;

    public BookPropertyResourceKeyModel(Class<?> clazz, String property, Component component, String suffix) {
        this.clazz = clazz;
        this.property = property;
        this.suffix = suffix;
        this.component = component;
    }

    /**
     * Gets resource key only.
     * @return
     */
    @Override
    public String getObject() {

        //attempt #1
        String key1 = clazz.getSimpleName() + SEPARATOR + property + SEPARATOR + SUFFIX + SEPARATOR + suffix;
        if (attempt(key1) != null) {
            return key1;
        }

        //attempt #2
        String key2 = clazz.getName() + SEPARATOR + property + SEPARATOR + SUFFIX + SEPARATOR + suffix;
        if (attempt(key2) != null) {
            return key2;
        }

        //no keys found, fall back to suffix.
        return suffix;
    }

    private String attempt(String key) {
        return ResourceUtil.getString(key, component);
    }
}
