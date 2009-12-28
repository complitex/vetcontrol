/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.information.util.web.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.information.web.pages.BookPage.DataProvider;

/**
 *
 * @author Artem
 */
public abstract class BookContentControl extends Panel {

    private class EditPanel extends Panel {

        public EditPanel(String id, final IModel<Serializable> model) {
            super(id, model);

            add(new Link("edit") {

                @Override
                public void onClick() {
                    selected(model.getObject());
                }
            });
        }
    }

    public BookContentControl(String id, final DataProvider dataProvider, String bookName, Locale systemLocale) throws IntrospectionException {
        super(id);

        add(new Label("bookName", bookName));

        Iterator it = dataProvider.iterator(0, 1);
        List<IColumn<Serializable>> columns = new ArrayList<IColumn<Serializable>>();
        if (it.hasNext()) {
            Object first = it.next();
            for (Property prop : BeanPropertyUtil.filter(first.getClass())) {
                columns.add(new TitledPropertyColumn<Serializable>(new DisplayPropertyLocalizableModel(prop, this), prop, systemLocale));
            }
            columns.add(new AbstractColumn(new ResourceModel("book.edit.header")) {

                @Override
                public void populateItem(Item cellItem, String componentId, IModel rowModel) {
                    cellItem.add(new EditPanel(componentId, rowModel));
                }
            });
        }
        DataTable table = new DataTable("table", columns.toArray(new IColumn[columns.size()]), dataProvider, Constants.ROWS_PER_PAGE);
        table.addTopToolbar(new HeadersToolbar(table, null));
        final FilterForm filterForm = new FilterForm("filterForm", dataProvider) {

            @Override
            protected void onSubmit() {
                dataProvider.init();
                super.onSubmit();
            }
        };
        table.addTopToolbar(new FilterToolbar(table, filterForm, dataProvider));
        table.setOutputMarkupId(true);
        PagingNavigator navigator = new AjaxPagingNavigator("navigator", table);
        filterForm.add(navigator);
        filterForm.add(table);
        add(filterForm);
    }

    public abstract void selected(Serializable obj);
}