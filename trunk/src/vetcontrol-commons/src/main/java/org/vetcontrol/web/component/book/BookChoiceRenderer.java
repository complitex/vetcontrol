/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component.book;

import org.apache.wicket.Session;
import org.vetcontrol.entity.Localizable;

import java.util.Locale;

/**
 *
 * @author Artem
 */
public class BookChoiceRenderer<T extends Localizable> extends SimpleDisableAwareChoiceRenderer<T> {

    private Locale systemLocale;

    public BookChoiceRenderer(Locale systemLocale) {
        this.systemLocale = systemLocale;
    }

    @Override
    public Object getDisplayValue(T object) {
        return object.getDisplayName(Session.get().getLocale(), systemLocale);
    }

    @Override
    public String getIdValue(T object, int index) {
        return String.valueOf(object.getId());
    }
}
