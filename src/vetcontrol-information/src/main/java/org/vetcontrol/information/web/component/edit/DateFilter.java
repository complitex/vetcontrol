/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.util.Date;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.odlabs.wiquery.ui.datepicker.DatePicker;

/**
 *
 * @author Artem
 */
public class DateFilter extends AbstractFilter {

    private DatePicker<Date> filter;

    public DateFilter(String id, IModel model, FilterForm form) {
        super(id, form);
        filter = new DatePicker<Date>("filter", model);
        filter.setShowOn(DatePicker.ShowOnEnum.FOCUS);
        enableFocusTracking(filter);
        add(filter);
    }

    public DatePicker<Date> getFilter() {
        return filter;
    }
}
