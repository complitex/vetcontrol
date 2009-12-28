/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.web.model;

import java.util.Locale;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.vetcontrol.information.util.web.ResourcesUtil;

/**
 *
 * @author Artem
 */
public class DisplayBookClassModel extends AbstractReadOnlyModel<String> {
    
    private Class bookType;
    private Locale locale;

    public DisplayBookClassModel(Class bookType, Locale locale) {
        this.bookType = bookType;
        this.locale = locale;
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
        return ResourcesUtil.getString(ResourcesUtil.BOOK_NAMES_BUNDLE, key, locale);
     }
}
