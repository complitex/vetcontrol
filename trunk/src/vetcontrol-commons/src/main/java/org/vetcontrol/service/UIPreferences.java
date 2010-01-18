/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import org.vetcontrol.entity.User;

/**
 * Class represents user preferences.
 * @author Artem
 */
public class UIPreferences implements Serializable {

    public enum PreferenceType {

        SORT_PROPERTY(true), SORT_ORDER(true), PAGE_NUMBER(true), FILTER(true), LOCALE(false), PAGE_SIZE(false);
        private boolean sessionOnly;

        private PreferenceType(boolean sessionOnly) {
            this.sessionOnly = sessionOnly;
        }

        public boolean isSessionOnly() {
            return sessionOnly;
        }
    }

    private User currentUser;

    private UserProfileBean userProfileBean;

    private Map<PreferenceType, Map<String, Object>> sessionPreferences = new EnumMap<PreferenceType, Map<String, Object>>(PreferenceType.class);

    public UIPreferences() {
        try {
            InitialContext context = new InitialContext();
            userProfileBean = (UserProfileBean)context.lookup("java:module/UserProfileBean");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> init(PreferenceType type) {
        Map<String, Object> prefs = sessionPreferences.get(type);
        if (prefs == null) {
            prefs = new HashMap<String, Object>();
            sessionPreferences.put(type, prefs);
        }
        return prefs;
    }

    public <T> void putPreference(PreferenceType type, String key, T value) {
        Map<String, Object> prefs = init(type);
        if (prefs == null) {
            prefs = new HashMap<String, Object>();
            sessionPreferences.put(type, prefs);
        }
        saveToDBIfNecessary(type, value);
        prefs.put(key, value);
    }

    public <T> T getPreference(PreferenceType type, String key, Class<T> clazz) {
        Map<String, Object> prefs = init(type);
        Object o = prefs.get(key);
        if (o == null) {
            o = getValueFromDBIfNecessary(type, o);
        }

        T value = null;
        try {
            value = clazz.cast(o);
        } catch (ClassCastException e) {
            throw new RuntimeException("can't to cast object " + o + " to type " + clazz, e);
        }
        return value;
    }

    private void initCurrentUser() {
        if (currentUser == null) {
            currentUser = userProfileBean.getCurrentUser();
        }
    }

    private Object getValueFromDBIfNecessary(PreferenceType type, Object defaultValue){
        initCurrentUser();
        switch (type) {
            case LOCALE:
                return currentUser.getLocale();
            case PAGE_SIZE:
                return currentUser.getPageSize();
        }
        return defaultValue;
    }

    private void saveToDBIfNecessary(PreferenceType type, Object value) {
        initCurrentUser();
        switch (type) {
            case LOCALE:
                currentUser.setLocale((String) value);
                break;
            case PAGE_SIZE:
                currentUser.setPageSize((Integer) value);
                break;
        }
        currentUser = userProfileBean.saveModifications(currentUser);
    }
}
