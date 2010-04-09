/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.web;

import org.apache.wicket.util.string.Strings;
import org.vetcontrol.util.book.Property;

/**
 *
 * @author Artem
 */
public class TruncateSelectValueInListPage implements ITruncate {

    @Override
    public String truncate(String fullValue, Property property) {
        if (!Strings.isEmpty(fullValue)) {
            String value = fullValue;
            int valueLimit = Constants.TEXT_LIMIT;
            if (property != null && property.getViewLength() > 0) {
                valueLimit = property.getViewLength();
            }
            if (value.length() > valueLimit) {
                value = value.substring(0, valueLimit);
                value += Constants.CONTINUE;
            }
            return value;
        } else {
            return fullValue;
        }
    }
}
