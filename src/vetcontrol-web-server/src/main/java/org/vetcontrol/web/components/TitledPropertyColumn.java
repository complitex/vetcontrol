/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.components;

import java.text.DateFormat;
import java.util.Date;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.PropertyResolver;

/**
 *
 * @author Artem
 */
public class TitledPropertyColumn<T> extends PropertyColumn<T> {

    private static final int TEXT_LIMIT = 15;
    private static final int TITLE_LIMIT = 30;
    private static final String CONTINUE = "...";

    public TitledPropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Object propertyValue = PropertyResolver.getValue(getPropertyExpression(), rowModel.getObject());

        String asString = "";
        if (propertyValue != null) {
            if (propertyValue instanceof Date) {
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Session.get().getLocale());
                asString = dateFormat.format((Date)propertyValue);
            } else {
                asString = propertyValue.toString();
            }
        }

        String value = asString;
        String title = null;
        if (value.length() > TEXT_LIMIT) {
            title = value;
            if (title.length() > TITLE_LIMIT) {
                title = title.substring(0, TITLE_LIMIT);
                title += CONTINUE;
            }
            value = value.substring(0, TEXT_LIMIT);
            value += CONTINUE;
        }

        Label label = new Label(componentId, value);
        if (title != null) {
            label.add(new SimpleAttributeModifier("title", title));
        }

        item.add(label);
    }
}
