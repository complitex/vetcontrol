/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import java.util.MissingResourceException;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author Artem
 */
public class DisplayPropertyLocalizableModel extends AbstractReadOnlyModel<String> {

    private static final String SEPARATOR = ".";
    private static final String SUFFIX = "header";

    private Class<?> clazz;
    private String property;

    public DisplayPropertyLocalizableModel(Class<?> clazz, String property) {
        this.clazz = clazz;
        this.property = property;
    }

    @Override
    public String getObject() {
        String value = null;

        //attempt #1
        String key1 = clazz.getSimpleName() + SEPARATOR + property + SEPARATOR + SUFFIX;
        if((value = attempt(key1)) != null){
            return value;
        }

        //attempt #2
        String key2 = clazz.getName() + SEPARATOR + property + SEPARATOR + SUFFIX;
        if((value = attempt(key2)) != null){
            return value;
        }

        //no keys found, fall back to property name.
        return property;
    }

    private String attempt(String key){
        try {
            return Application.get().getResourceSettings().getLocalizer().getString(key, (Component) null);
        } catch (MissingResourceException e) {
            return null;
        }
    }
}
