/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.change.comparator;

import java.util.Set;
import org.vetcontrol.entity.Change;

/**
 *
 * @author Artem
 */
public interface Comparator<T> {

    Set<Change> compare(T oldValue, T newValue);
}
