/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.util.change.comparator;

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

}
