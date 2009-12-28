/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.PropertyResolver;
import org.vetcontrol.information.service.fasade.pages.AddUpdateBookEntryPageFasade;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.information.util.web.Property;
import org.vetcontrol.information.web.model.StringCultureModel;

/**
 *
 * @author Artem
 */
public class TitledPropertyColumn<T> extends FilteredPropertyColumn<T> {

    private Property property;
    private Locale systemLocale;
    private AddUpdateBookEntryPageFasade fasade;

    public TitledPropertyColumn(IModel<String> displayModel, Property property, AddUpdateBookEntryPageFasade fasade, Locale systemLocale) {
        super(displayModel, property.getName());
        this.property = property;
        this.systemLocale = systemLocale;
        this.fasade = fasade;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Object propertyValue = PropertyResolver.getValue(getPropertyExpression(), rowModel.getObject());

        String asString = "";
        try {
            asString = BeanPropertyUtil.getAsString(propertyValue, property, systemLocale);
        } catch (Exception e) {
            //TODO: remove it after testing.
            throw new RuntimeException(e);
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

//    @Override
//    protected IModel<Serializable> getFilterModel(FilterForm form) {
//        if (property.isLocalizable()) {
//            return new StringCultureModel(super.getFilterModel(form));
////        } else if (property.isBeanReference()) {
////            return new BookReferenceModel(property.getType(), super.getFilterModel(form), property.getReferencedField());
//        } else {
//            return super.getFilterModel(form);
//        }
//    }
    @Override
    public Component getFilter(String componentId, FilterForm form) {
        if (property.isLocalizable()) {
            return new TextFilter(componentId, new StringCultureModel(new PropertyModel(form.getDefaultModel(), getPropertyExpression())), form);
        } else if (property.isBeanReference()) {
            return new ChoiceFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form,
                    fasade.getAll(property.getType()),
                    new BookChoiceRenderer(property, systemLocale), false);
        } else {
            return new TextFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form);
        }
    }
}
