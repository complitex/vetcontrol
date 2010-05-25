/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component.book;

import org.vetcontrol.entity.IDisabled;

/**
 *
 * @author Artem
 */
public abstract class SimpleDisableAwareChoiceRenderer<T> implements IDisableAwareChoiceRenderer<T> {

    @Override
    public boolean isDisabled(T object) {
        if (object instanceof IDisabled) {
            return ((IDisabled) object).isDisabled();
        }
        return false;
    }
}
