/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change.comparator;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.entity.Change;
import org.vetcontrol.util.change.comparator.PropertyComparator;

/**
 *
 * @author Artem
 */
public final class LocalizableStringComparator extends PropertyComparator<List<StringCulture>> {

    private static final Logger log = LoggerFactory.getLogger(LocalizableStringComparator.class);

    public LocalizableStringComparator(String propertyName) {
        super(propertyName);
    }

    @Override
    public Set<Change> compare(List<StringCulture> oldValue, List<StringCulture> newValue) {
        checkArguments(oldValue, newValue);

        Set<Change> changes = new HashSet<Change>();

        for (StringCulture oldCulture : oldValue) {
            for (StringCulture newCulture : newValue) {
                if (new Locale(oldCulture.getId().getLocale()).getLanguage().
                        equalsIgnoreCase(new Locale(newCulture.getId().getLocale()).getLanguage())) {

                    if (!Strings.isEqual(oldCulture.getValue(), newCulture.getValue())) {
                        Change change = new Change();
                        change.setPropertyName(getPropertyName());
                        change.setOldValue(oldCulture.getValue());
                        change.setNewValue(newCulture.getValue());
                        change.setLocale(new Locale(oldCulture.getId().getLocale()));
                        changes.add(change);
                    }
                    break;
                }
            }
        }

        return changes;
    }

    private void checkArguments(List<StringCulture> oldValue, List<StringCulture> newValue) throws IllegalArgumentException {
        if (oldValue == null || newValue == null) {
            //Error!
            throw new IllegalArgumentException("List of localizable strings can't be null.");
        }
        if (oldValue.size() != newValue.size()) {
            //Error!
            throw new IllegalArgumentException("Lists of loclizable strings have different length.");
        }
    }
}
