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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.PropertyResolver;
import org.apache.wicket.util.string.Strings;
import static org.vetcontrol.util.book.BeanPropertyUtil.*;
import org.vetcontrol.information.util.web.ResourceUtil;
import org.vetcontrol.information.util.web.TruncateUtil;
import org.vetcontrol.information.web.component.list.DateFilter;
import org.vetcontrol.information.web.component.list.BookTextFilter;
import org.vetcontrol.information.web.model.AutoCompleteBookReferenceModel;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.information.web.model.StringCultureModel;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.util.book.entity.ShowBooksMode;
import org.vetcontrol.util.book.entity.annotation.UIType;

/**
 *
 * @author Artem
 */
public class BookPropertyColumn<T> extends FilteredPropertyColumn<T> {

    private Property property;
    private Locale systemLocale;
    private Component component;
    private final IBookViewDAO bookViewDAO;

    public BookPropertyColumn(Component component, IModel<String> displayModel, Property property, IBookViewDAO bookViewDAO, Locale systemLocale) {
        super(displayModel, property.getName(), property.getName());
        this.component = component;
        this.property = property;
        this.systemLocale = systemLocale;
        this.bookViewDAO = bookViewDAO;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Object propertyValue = PropertyResolver.getValue(getPropertyExpression(), rowModel.getObject());

        String asString = "";
        if (property.getType().equals(boolean.class) || property.getType().equals(Boolean.class)) {
            asString = ResourceUtil.getString(String.valueOf(propertyValue), component);
        } else if (property.isBookReference() && property.getUiType().equals(UIType.AUTO_COMPLETE)
                && !Strings.isEmpty(property.getBookReferencePattern())) {
            asString = applyPattern(property.getBookReferencePattern(), propertyValue, systemLocale);
        } else {
            asString = getPropertyAsString(propertyValue, property, systemLocale);
        }

        //Show overall text as title
        String title = asString;

        String value = TruncateUtil.TRUNCATE_SELECT_VALUE_IN_LIST_PAGE.truncate(asString, property);

        Label label = new Label(componentId, value);
        label.add(new SimpleAttributeModifier("title", title));

        item.add(label);
    }

    @Override
    public Component getFilter(String componentId, FilterForm form) {
        if (property.isLocalizable()) {
            BookTextFilter filter = new BookTextFilter(componentId,
                    new StringCultureModel(new PropertyModel(form.getDefaultModel(), getPropertyExpression())), form, property);
            return filter;
        } else if (property.isBookReference()) {
            if (property.getUiType().equals(UIType.SELECT)) {
                return new ChoiceFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form,
                        bookViewDAO.getContent(property.getType(), ShowBooksMode.ENABLED),
                        new BookChoiceRenderer(property, systemLocale, TruncateUtil.TRUNCATE_SELECT_VALUE_IN_LIST_PAGE), false);
            } else if (property.getUiType().equals(UIType.AUTO_COMPLETE)) {
                return new BookTextFilter(componentId,
                        new AutoCompleteBookReferenceModel(property, new PropertyModel(form.getDefaultModel(), getPropertyExpression())), form, property);
            } else {
                //TODO: remove after tests.
                throw new RuntimeException("Not real case.");
            }
        } else if (Date.class.isAssignableFrom(property.getType())) {
            DateFilter filter = new DateFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form, property);
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
            BookTextFilter filter = new BookTextFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form, property);
            return filter;
        }
    }
}
