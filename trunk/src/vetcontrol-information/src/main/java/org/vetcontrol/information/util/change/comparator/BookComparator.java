/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change.comparator;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.util.change.comparator.DateComparator;
import org.vetcontrol.util.change.comparator.SimpleComparator;

/**
 *
 * @author Artem
 */
public class BookComparator<T> extends AbstractEntityComparator<T> {

    private static final Logger log = LoggerFactory.getLogger(BookComparator.class);

    public BookComparator(Class<T> bookType, Locale systemLocale) {
        super(bookType, systemLocale);
    }

    @Override
    public Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = new HashMap<String, Comparator>();

        for (Property prop : getProperties(getEntityClass())) {
            String propName = prop.getName();
            String localizedPropName = new DisplayPropertyLocalizableModel(prop).getObject();
            if (prop.isLocalizable()) {
                propertyComparators.put(propName, new LocalizableStringComparator(localizedPropName));
            } else if (prop.isBookReference()) {
                propertyComparators.put(propName, new BookReferenceComparator(propName, getEntityClass(), getSystemLocale()));
            } else {
                Class propType = prop.getType();
                if (isPrimitive(propType) && !Date.class.isAssignableFrom(propType)) {
                    propertyComparators.put(propName, new SimpleComparator(localizedPropName));
                } else if (Date.class.isAssignableFrom(propType)) {
                    propertyComparators.put(propName, new DateComparator(localizedPropName, getSystemLocale()));
                }
            }
        }
        return propertyComparators;
    }
}
