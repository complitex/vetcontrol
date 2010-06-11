/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.change.comparator;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class DateComparator extends SimpleComparator<Date> {

    private static final Logger log = LoggerFactory.getLogger(DateComparator.class);

    private DateFormat dateFormat;

    public DateComparator(String propertyName, Locale systemLocale) {
        super(propertyName);
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, systemLocale);
    }

    @Override
    protected boolean support(Class type) {
        return Date.class.isAssignableFrom(type);
    }

    @Override
    protected boolean isEqual(Date oldValue, Date newValue) {
        return oldValue.getTime() == newValue.getTime();
    }

    @Override
    protected String displayPropertyValue(Date value) {
        return dateFormat.format(value);
    }
}
