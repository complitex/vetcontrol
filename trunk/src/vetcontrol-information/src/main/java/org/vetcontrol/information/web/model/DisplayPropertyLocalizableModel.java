/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.web.util.ResourceUtil;

/**
 *
 * @author Artem
 */
public class DisplayPropertyLocalizableModel extends AbstractReadOnlyModel<String> {

    private static final String SEPARATOR = ".";

    private static final String SUFFIX = "prop";

    private Class<?> clazz;

    private String property;

    private Component resourceComponent;

    public DisplayPropertyLocalizableModel(Property property, Component resourceComponent) {
        this.clazz = property.getSurroundingClass();
        this.property = property.getName();
        this.resourceComponent = resourceComponent;
    }

    @Override
    public String getObject() {
        String value = null;

        //attempt #1
        String key1 = clazz.getSimpleName() + SEPARATOR + property + SEPARATOR + SUFFIX;
        if ((value = attempt(key1)) != null) {
            return value;
        }

        //attempt #2
        String key2 = clazz.getName() + SEPARATOR + property + SEPARATOR + SUFFIX;
        if ((value = attempt(key2)) != null) {
            return value;
        }

        //no keys found, fall back to property name.
        return property;
    }

    private String attempt(String key) {
        return ResourceUtil.getString(resourceComponent, key);
    }
}
