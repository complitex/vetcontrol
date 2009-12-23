/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.PropertyResolver;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.util.web.Property;

/**
 *
 * @author Artem
 */
public class TitledPropertyColumn<T> extends PropertyColumn<T> {

    private static final int TEXT_LIMIT = 15;
    private static final int TITLE_LIMIT = 30;
    private static final String CONTINUE = "...";

    private Property property;

    public TitledPropertyColumn(IModel<String> displayModel, Property property) {
        super(displayModel, property.getName());
        this.property = property;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Object propertyValue = PropertyResolver.getValue(getPropertyExpression(), rowModel.getObject());

        String asString = "";
        if (propertyValue != null) {
            if (Date.class.isAssignableFrom(property.getType())) {
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Session.get().getLocale());
                asString = dateFormat.format((Date) propertyValue);
            } else if (List.class.isAssignableFrom(property.getType()) && property.isLocalizable()) {
                Locale currentLocale = Session.get().getLocale();
                List<StringCulture> list = (List<StringCulture>) propertyValue;
                boolean finded = false;
                for (StringCulture culture : list) {
                    if (new Locale(culture.getId().getLocale()).getLanguage().equalsIgnoreCase(currentLocale.getLanguage())) {
                        finded = true;
                        asString = culture.getValue();
                    }
                }
                if (!finded) {
                    if (!list.isEmpty()) {
                        asString = list.get(0).getValue();
                    }
                }
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
