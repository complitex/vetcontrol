/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.util.Date;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.web.component.DatePicker;

/**
 *
 * @author Artem
 */
public final class DatePanel extends Panel {

    public DatePanel(String id, IModel model, Property prop, boolean enabled) {
        super(id);

        DatePicker<Date> dateField = new DatePicker<Date>("dateField", model);
        dateField.setEnabled(enabled);
        dateField.setLabel(new DisplayPropertyLocalizableModel(prop, this));
        dateField.setRequired(!prop.isNullable());
        add(dateField);
    }
}
