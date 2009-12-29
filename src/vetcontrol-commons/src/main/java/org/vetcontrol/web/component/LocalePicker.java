/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public class LocalePicker extends Panel {

    public LocalePicker(String id, final List<Locale> supportedLocales, Locale systemLocale) {
        super(id);

        if (getSession().getLocale() == null || !supportedLocales.contains(getSession().getLocale())) {
            getSession().setLocale(systemLocale);
        }
        add(new DropDownChoice("localeDropDown", new IModel<Locale>() {

            @Override
            public Locale getObject() {
                return getSession().getLocale();
            }

            @Override
            public void setObject(Locale locale) {
                getSession().setLocale(locale);
            }

            @Override
            public void detach() {
            }
        }, supportedLocales, new ChoiceRenderer<Locale>() {

            @Override
            public Object getDisplayValue(Locale locale) {
                return locale.getDisplayName(getLocale());
            }
        }) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        });
    }
}
