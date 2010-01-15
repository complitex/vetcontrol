/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.PropertyResolver;
import org.vetcontrol.information.service.fasade.pages.BookPageFasade;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.information.util.web.ResourceUtil;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.information.web.model.StringCultureModel;

/**
 *
 * @author Artem
 */
public class BookPropertyColumn<T> extends FilteredPropertyColumn<T> {

    private Property property;
    private Locale systemLocale;
    private BookPageFasade fasade;
    private Component component;

    public BookPropertyColumn(Component component, IModel<String> displayModel, Property property, BookPageFasade fasade, Locale systemLocale) {
        super(displayModel, property.getName(), property.getName());
        this.component = component;
        this.property = property;
        this.systemLocale = systemLocale;
        this.fasade = fasade;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Object propertyValue = PropertyResolver.getValue(getPropertyExpression(), rowModel.getObject());

        String asString = "";
        if (property.getType().equals(boolean.class) || property.getType().equals(Boolean.class)) {
            asString = ResourceUtil.getString(String.valueOf(propertyValue), component);
        } else {
            try {
                asString = BeanPropertyUtil.getPropertyAsString(propertyValue, property, systemLocale);
            } catch (Exception e) {
                //TODO: remove it after testing.
                throw new RuntimeException(e);
            }
        }

        String value = asString;
        String title = null;
        if (value.length() > Constants.TEXT_LIMIT) {
            title = value;
            if (title.length() > Constants.TITLE_LIMIT) {
                title = title.substring(0, Constants.TITLE_LIMIT);
                title += Constants.CONTINUE;
            }
            value = value.substring(0, Constants.TEXT_LIMIT);
            value += Constants.CONTINUE;
        }

        Label label = new Label(componentId, value);
        if (title != null) {
            label.add(new SimpleAttributeModifier("title", title));
        }

        item.add(label);
    }

    @Override
    public Component getFilter(String componentId, FilterForm form) {
        if (property.isLocalizable()) {
            TextFilter filter = new TextFilter(componentId,
                    new StringCultureModel(new PropertyModel(form.getDefaultModel(), getPropertyExpression())),
                    form);
            return filter;
        } else if (property.isBookReference()) {
            return new ChoiceFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form,
                    fasade.getAll(property.getType()),
                    new BookChoiceRenderer(property, systemLocale), false);
        } else if (Date.class.isAssignableFrom(property.getType())) {
            DateFilter filter = new DateFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form);
            return filter;
        } else if (property.getType().equals(boolean.class) || property.getType().equals(Boolean.class)) {
            List choices = Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE});
            ChoiceRenderer<Boolean> booleanChoiceRenderer = new ChoiceRenderer<Boolean>() {

                @Override
                public Object getDisplayValue(Boolean object) {
                    return ResourceUtil.getString(String.valueOf(object), component);
                }

                @Override
                public String getIdValue(Boolean object, int index) {
                    return String.valueOf(object);
                }
            };

            ChoiceFilter filter = new ChoiceFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()),
                    form, choices, booleanChoiceRenderer, false);
            return filter;
        } else {
            TextFilter filter = new TextFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form);
            return filter;
        }
    }
}
