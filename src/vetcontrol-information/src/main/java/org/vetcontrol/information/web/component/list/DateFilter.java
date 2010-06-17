/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.list;

import java.util.Date;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.util.resource.CommonResourceKeys;
import org.vetcontrol.web.component.DatePicker;

/**
 *
 * @author Artem
 */
public class DateFilter extends AbstractFilter {

    private DatePicker<Date> filter;

    public DateFilter(String id, IModel model, FilterForm form, Property property) {
        super(id, form);
        filter = new DatePicker<Date>("filter", model) {

            @Override
            public String getValidatorKeyPrefix() {
                return CommonResourceKeys.FILTER_COMPONENT_PREFIX;
            }
        };
        filter.setLabel(new DisplayPropertyLocalizableModel(property));
        filter.setShowOn(DatePicker.ShowOnEnum.FOCUS);
        enableFocusTracking(filter);
        add(filter);
    }

    public DatePicker<Date> getFilter() {
        return filter;
    }
}
