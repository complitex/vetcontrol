/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.util.web.ResourceUtil;

/**
 *
 * @author Artem
 */
public class DisplayPropertyLocalizableModel extends AbstractReadOnlyModel<String> {

    private static final String SEPARATOR = ".";
    private static final String SUFFIX = "prop";
    private Class<?> clazz;
    private String property;
    private Component component;

    public DisplayPropertyLocalizableModel(Property property, Component component) {
        this.clazz = property.getSurroundingClass();
        this.property = property.getName();
        this.component = component;
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
        return ResourceUtil.getString(key, component);
    }
}
