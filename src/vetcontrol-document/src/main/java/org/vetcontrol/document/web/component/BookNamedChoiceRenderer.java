/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.web.component;

import java.util.Locale;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.vetcontrol.entity.Localizable;

/**
 *
 * @author Artem
 */
public class BookNamedChoiceRenderer<T extends Localizable> implements IChoiceRenderer<T> {

    private Locale systemLocale;

    public BookNamedChoiceRenderer(Locale systemLocale) {
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
