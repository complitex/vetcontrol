/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.change.comparator;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.entity.Change;

/**
 * Currently supports only enum types and BeanPropertyUtil.PRIMITIVES types excepting all date types.
 *
 * @author Artem
 */
public class SimpleComparator<T> extends PropertyComparator<T> {

    private static final Logger log = LoggerFactory.getLogger(SimpleComparator.class);

    public SimpleComparator(String propertyName) {
        super(propertyName);
    }

    protected String displayPropertyValue(T value) {
        if (value == null) {
            return "null";
        }
        return value.toString();
    }

    @Override
    public Set<Change> compare(T oldValue, T newValue) {
        if ((oldValue == null) && (newValue == null)) {
            //property value wasn't changed.
            return Collections.emptySet();
        }

        Class type = (oldValue != null) ? oldValue.getClass() : newValue.getClass();
        boolean supported = support(type);
        if (!supported) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " does not support comparison of objects of type " + type);
        }

        String oldValueAsString = displayPropertyValue(oldValue);
        String newValueAsString = displayPropertyValue(newValue);
        if (oldValue == null || newValue == null) {
            //property value was changed
            return Collections.singleton(newChange(getPropertyName(), oldValueAsString, newValueAsString));
        }

        //the both old value and new value are not null.
        if (!isEqual(oldValue, newValue)) {
            return Collections.singleton(newChange(getPropertyName(), oldValueAsString, newValueAsString));
        }

        return Collections.emptySet();
    }

    protected boolean support(Class type) {
        if (type.isEnum() || (isPrimitive(type) && !Date.class.isAssignableFrom(type))) {
            return true;
        }
        return false;
    }

    /**
     * The both oldValue and newValue params must be not null.
     * @param oldValue
     * @param newValue
     * @return
     */
    protected boolean isEqual(T oldValue, T newValue) {
        return oldValue.equals(newValue);
    }
}
