/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.list;

import java.util.Date;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.book.Property;

/**
 *
 * @author Artem
 */
public class DateFilter extends AbstractFilter {

    private DatePicker<Date> filter;

    public DateFilter(String id, IModel model, FilterForm form, Property property) {
        super(id, form);
        filter = new DatePicker<Date>("filter", model);
        filter.setLabel(new DisplayPropertyLocalizableModel(property, this));
        filter.setShowOn(DatePicker.ShowOnEnum.FOCUS);
        enableFocusTracking(filter);
        add(filter);
    }

    public DatePicker<Date> getFilter() {
        return filter;
    }
}
