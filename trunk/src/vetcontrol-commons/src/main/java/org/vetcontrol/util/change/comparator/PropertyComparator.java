/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.change.comparator;

import org.vetcontrol.entity.Change;

/**
 *
 * @author Artem
 */
public abstract class PropertyComparator<T> implements Comparator<T> {

    private String propertyName;

    public PropertyComparator(String propertyName) {
        this.propertyName = propertyName;
    }

    protected String getPropertyName() {
        return propertyName;
    }

    protected static Change newChange(String propertyName, String oldValue, String newValue) {
        Change change = new Change();
        change.setPropertyName(propertyName);
        change.setOldValue(oldValue);
        change.setNewValue(newValue);
        return change;
    }
}
