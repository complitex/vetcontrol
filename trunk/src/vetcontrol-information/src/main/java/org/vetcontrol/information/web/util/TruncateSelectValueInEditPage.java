/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util;

import org.apache.wicket.util.string.Strings;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.book.Property;

/**
 *
 * @author Artem
 */
public class TruncateSelectValueInEditPage implements ITruncate {

    @Override
    public String truncate(String fullValue, Property property) {
        if (!Strings.isEmpty(fullValue)) {
            if (property.isBookReference()) {
                Property referencedProperty = getPropertyByName(property.getType(), property.getReferencedField());
                if (referencedProperty.getLength() > Constants.SELECT_ENTRY_MAX_LENGTH) {
                    if (fullValue.length() > Constants.SELECT_ENTRY_MAX_LENGTH) {
                        return fullValue.substring(0, Constants.SELECT_ENTRY_MAX_LENGTH) + Constants.CONTINUE;
                    }
                }
            }
        }
        return fullValue;
    }
}
