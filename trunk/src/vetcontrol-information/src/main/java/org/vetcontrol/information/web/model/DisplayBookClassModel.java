/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import org.apache.wicket.Session;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.vetcontrol.information.util.resource.ResourceUtil;

/**
 *
 * @author Artem
 */
public class DisplayBookClassModel extends AbstractReadOnlyModel<String> {

    private Class bookType;

    public DisplayBookClassModel(Class bookType) {
        this.bookType = bookType;
    }

    @Override
    public String getObject() {
        String value = null;

        //attempt #1
        String key1 = bookType.getSimpleName();
        if ((value = attempt(key1)) != null) {
            return value;
        }

        //attempt #2
        String key2 = bookType.getName();
        if ((value = attempt(key2)) != null) {
            return value;
        }

        //no keys found, fall back to class name.
        return bookType.getSimpleName();
    }

    private String attempt(String key) {
        return ResourceUtil.getString(ResourceUtil.COMMON_RESOURCE_BUNDLE, key, Session.get().getLocale(), false);
    }
}
