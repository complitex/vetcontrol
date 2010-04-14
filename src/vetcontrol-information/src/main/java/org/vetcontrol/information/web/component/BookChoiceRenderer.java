/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.util.Locale;
import org.vetcontrol.information.util.web.ITruncate;
import static org.vetcontrol.util.book.BeanPropertyUtil.*;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.web.component.book.SimpleDisableAwareChoiceRenderer;

/**
 *
 * @author Artem
 */
public class BookChoiceRenderer extends SimpleDisableAwareChoiceRenderer<Object>{

    private Property property;
    private Locale systemLocale;
    private ITruncate truncateLogic;

    public BookChoiceRenderer(Property property, Locale systemLocale, ITruncate truncateLogic) {
        this.property = property;
        this.systemLocale = systemLocale;
        this.truncateLogic = truncateLogic;
    }

    @Override
    public Object getDisplayValue(Object object) {
        Property referencedProperty = getPropertyByName(object.getClass(), property.getReferencedField());
        Object value = getPropertyValue(object, referencedProperty.getName());
        String stringValue = getPropertyAsString(value, referencedProperty, systemLocale);
        return truncateLogic.truncate(stringValue, property);
    }

    @Override
    public String getIdValue(Object object, int index) {
        return getPropertyValue(object, "id").toString();
    }
}
