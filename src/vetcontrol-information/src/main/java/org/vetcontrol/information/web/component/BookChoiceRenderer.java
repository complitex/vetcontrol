/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Property;

/**
 *
 * @author Artem
 */
public class BookChoiceRenderer extends ChoiceRenderer<Object> {
    
    private Property property;
    private Locale systemLocale;

    public BookChoiceRenderer(Property property, Locale systemLocale) {
        this.property = property;
        this.systemLocale = systemLocale;
    }

    @Override
    public Object getDisplayValue(Object object) {
        try {
            List<Property> props = BeanPropertyUtil.getProperties(object.getClass());
            for (Property prop : props) {
                if (prop.getName().equals(property.getReferencedField())) {
                    Object value = BeanPropertyUtil.getPropertyValue(object, prop.getName());
                    if (prop.isLocalizable()) {
                        return BeanPropertyUtil.getPropertyAsString(value, prop, systemLocale);
                    } else {
                        return value;
                    }
                }
            }
        } catch (Exception e) {
            //TODO: remove it after testing.
            throw new RuntimeException(e);
        }
        return "";
    }

    @Override
    public String getIdValue(Object object, int index) {
        if (object != null) {
            try {
                Object id = BeanPropertyUtil.getPropertyValue(object, "id");
                return id.toString();
            } catch (Exception e) {
                //TODO: remove it after testing.
//                    throw new RuntimeException(e);
            }
        }

        return String.valueOf(index);
    }
}
