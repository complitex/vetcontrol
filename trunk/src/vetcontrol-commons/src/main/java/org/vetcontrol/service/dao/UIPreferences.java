/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Class represents user preferences.
 * @author Artem
 */
public class UIPreferences implements Serializable {

    private Map<String, Object> init(PreferenceType type) {
        Map<String, Object> prefs = preferences.get(type);
        if (prefs == null) {
            prefs = new HashMap<String, Object>();
            preferences.put(type, prefs);
        }
        return prefs;
    }

    public enum PreferenceType {

        SORT_PROPERTY(true), SORT_ORDER(true), PAGING(true), FILTER(true);
        private boolean sessionOnly;

        private PreferenceType(boolean sessionOnly) {
            this.sessionOnly = sessionOnly;
        }

        public boolean isSessionOnly() {
            return sessionOnly;
        }
    }
    private Map<PreferenceType, Map<String, Object>> preferences = new EnumMap<PreferenceType, Map<String, Object>>(PreferenceType.class);

    public <T> void putPreference(PreferenceType type, String key, T value) {
        Map<String, Object> prefs = init(type);
        if (prefs == null) {
            prefs = new HashMap<String, Object>();
            preferences.put(type, prefs);
        }
        prefs.put(key, value);
    }

    public <T> T getPreference(PreferenceType type, String key, Class<T> clazz) {
        Map<String, Object> prefs = init(type);
        Object o = prefs.get(key);
        T value = null;
        try {
            value = clazz.cast(o);
        } catch (ClassCastException e) {
            throw new RuntimeException("can't to cast object " + o + " to type " + clazz, e);
        }
        return value;
    }
}
