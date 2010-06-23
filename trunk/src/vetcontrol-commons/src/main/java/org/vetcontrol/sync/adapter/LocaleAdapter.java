/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.sync.adapter;

import java.util.Locale;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Artem
 */
public class LocaleAdapter extends XmlAdapter<String, Locale> {

    @Override
    public Locale unmarshal(String language) throws Exception {
        if (language == null || language.isEmpty()) {
            return null;
        } else {
            return new Locale(language);
        }
    }

    @Override
    public String marshal(Locale locale) throws Exception {
        return locale != null ? locale.getLanguage() : null;
    }
}
