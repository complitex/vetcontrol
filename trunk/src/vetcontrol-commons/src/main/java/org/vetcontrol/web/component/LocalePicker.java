/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.service.UIPreferences;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class LocalePicker extends Panel {

    public LocalePicker(String id, final List<Locale> supportedLocales, Locale systemLocale, final UIPreferences preferences) {
        super(id);

        String userLocale = preferences.getPreference(UIPreferences.PreferenceType.LOCALE, "locale", String.class);
        if(userLocale == null){
            getSession().setLocale(systemLocale);
            preferences.putPreference(UIPreferences.PreferenceType.LOCALE, "locale", systemLocale.getLanguage());
        } else{
            Locale userLocalePreference = new Locale(userLocale);
            getSession().setLocale(userLocalePreference);
        }

        add(new DropDownChoice<Locale>("localeDropDown", new IModel<Locale>() {

            @Override
            public Locale getObject() {
                return getSession().getLocale();
            }

            @Override
            public void setObject(Locale locale) {
                getSession().setLocale(locale);
                preferences.putPreference(UIPreferences.PreferenceType.LOCALE, "locale", locale.getLanguage());
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
