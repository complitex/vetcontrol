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
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.vetcontrol.information.util.web.BeanPropertiesUtil;
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

    

    public BookContentControl(String id, final DataProvider dataProvider, String bookName) throws IntrospectionException {
        super(id);

        add(new Label("bookName", bookName));
        
        Iterator it = dataProvider.iterator(0, 1);
        List<IColumn<Serializable>> columns = new ArrayList<IColumn<Serializable>>();
        if (it.hasNext()) {
            Object first = it.next();
            for (Property prop : BeanPropertiesUtil.filter(first.getClass())) {
                columns.add(new TitledPropertyColumn<Serializable>(new DisplayPropertyLocalizableModel(first.getClass(), prop.getName()), prop));
            }
            columns.add(new AbstractColumn(new Model("Edit")) {

                @Override
                public void populateItem(Item cellItem, String componentId, IModel rowModel) {
                    cellItem.add(new EditPanel(componentId, rowModel));
                }
            });
            columns.add(new TextFilteredPropertyColumn(new Model("ID"), "id"));
        }
        DataTable table = new DataTable("table", columns.toArray(new IColumn[columns.size()]), dataProvider, Constants.ROWS_PER_PAGE);
        table.addTopToolbar(new HeadersToolbar(table, null));
        final FilterForm filterForm = new FilterForm("filterForm", dataProvider);
        table.addTopToolbar(new FilterToolbar(table, filterForm, dataProvider));
        filterForm.add(table);
        table.addBottomToolbar(new NavigationToolbar(table));
//        add(table);
        add(filterForm);
    }

    public abstract void selected(Serializable obj);
}
