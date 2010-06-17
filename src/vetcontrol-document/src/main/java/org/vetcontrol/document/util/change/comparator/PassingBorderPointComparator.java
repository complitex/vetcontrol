/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator;

import java.util.Collections;
import java.util.Set;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.util.change.comparator.PropertyComparator;

/**
 *
 * @author Artem
 */
public class PassingBorderPointComparator extends PropertyComparator<PassingBorderPoint> {

    public PassingBorderPointComparator(String propertyName) {
        super(propertyName);
    }

    @Override
    public Set<Change> compare(PassingBorderPoint oldValue, PassingBorderPoint newValue) {
        if ((oldValue == null) && (newValue == null)) {
            //property value wasn't changed.
            return Collections.emptySet();
        }

        String oldValueAsString = displayValue(oldValue);
        String newValueAsString = displayValue(newValue);

        if (oldValue == null || newValue == null) {
            //property value was changed
            return Collections.singleton(newChange(getPropertyName(), oldValueAsString, newValueAsString));
        }

        //the both old value and new value are not null.
        if (!oldValue.getId().equals(newValue.getId())) {
            return Collections.singleton(newChange(getPropertyName(), oldValueAsString, newValueAsString));
        }

        return Collections.emptySet();
    }

    private static String displayValue(PassingBorderPoint value) {
        if (value == null) {
            return null;
        } else {
            return value.getName();
        }
    }
}
