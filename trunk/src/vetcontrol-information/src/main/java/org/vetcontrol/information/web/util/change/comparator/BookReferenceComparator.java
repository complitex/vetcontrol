/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util.change.comparator;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.entity.IBook;
import org.vetcontrol.entity.Change;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.util.change.comparator.PropertyComparator;

/**
 *
 * @author Artem
 */
public final class BookReferenceComparator extends PropertyComparator<IBook> {

    private static final Logger log = LoggerFactory.getLogger(BookReferenceComparator.class);

    private Locale systemLocale;

    private Class rootBookType;

    public BookReferenceComparator(String propertyName, Class rootBookType, Locale systemLocale) {
        super(propertyName);
        this.systemLocale = systemLocale;
        this.rootBookType = rootBookType;
    }

    private static String dysplayPropertyValue(IBook value, Class rootBookType, String propertyName, Locale systemLocale) {
        if (value == null) {
            return "null";
        }
        return getPropertyAsString(value, getPropertyByName(rootBookType, propertyName), systemLocale);
    }

    private String getLocalizedPropertyName() {
        return new DisplayPropertyLocalizableModel(getPropertyByName(rootBookType, getPropertyName())).getObject();
    }

    @Override
    public Set<Change> compare(IBook oldValue, IBook newValue) {
        if ((oldValue == null) && (newValue == null)) {
            //property value wasn't changed.
            return Collections.emptySet();
        }

        String oldValueAsString = dysplayPropertyValue(oldValue, rootBookType, getPropertyName(), systemLocale);
        String newValueAsString = dysplayPropertyValue(newValue, rootBookType, getPropertyName(), systemLocale);

        if (oldValue == null || newValue == null) {
            //property value was changed
            Change change = new Change();
            change.setPropertyName(getLocalizedPropertyName());
            change.setOldValue(oldValueAsString);
            change.setNewValue(newValueAsString);
            return Collections.singleton(change);
        }

        //the both old value and new value are not null.
        if (!getId(oldValue).equals(getId(newValue))) {
            Change change = new Change();
            change.setPropertyName(getLocalizedPropertyName());
            change.setOldValue(oldValueAsString);
            change.setNewValue(newValueAsString);
            return Collections.singleton(change);
        }

        return Collections.emptySet();
    }
}
