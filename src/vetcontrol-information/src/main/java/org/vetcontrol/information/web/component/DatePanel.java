/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.util.Date;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.vetcontrol.information.util.web.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public final class DatePanel extends Panel {

    public DatePanel(String id, IModel model, Property prop) {
        super(id);

        DatePicker<Date> dateField = new DatePicker<Date>("dateField", model);
        dateField.setLabel(new DisplayPropertyLocalizableModel(prop, this));
        dateField.setButtonImage("images/calendar.gif");
        dateField.setButtonImageOnly(true);
        dateField.setShowOn(DatePicker.ShowOnEnum.BOTH);
        dateField.setRequired(!prop.isNullable());
        add(dateField);
    }
}
