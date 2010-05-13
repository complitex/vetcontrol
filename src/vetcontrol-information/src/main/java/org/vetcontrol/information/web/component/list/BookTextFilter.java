/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.list;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.book.Property;

/**
 *
 * @author Artem
 */
public class BookTextFilter extends TextFilter<Object> {

    public BookTextFilter(String id, IModel model, FilterForm filterForm, Property property) {
        super(id, model, filterForm);
        getFilter().setLabel(new DisplayPropertyLocalizableModel(property, this));
    }
}
