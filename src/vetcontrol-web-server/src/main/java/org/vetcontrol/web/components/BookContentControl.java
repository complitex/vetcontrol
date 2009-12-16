/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.components;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.vetcontrol.util.web.BeanPropertiesFilter;
import org.vetcontrol.util.web.Property;
import org.vetcontrol.web.models.DisplayPropertyLocalizableModel;

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

    public BookContentControl(String id, final IModel<List<Serializable>> content, String bookName) throws IntrospectionException {
        super(id);

        add(new Label("bookName", bookName));

        if (content == null || content.getObject() == null) {
            throw new IllegalArgumentException("model must be not null.");
        }

        List<Serializable> list = content.getObject();

        List<IColumn<Serializable>> columns = new ArrayList<IColumn<Serializable>>();
        if (!list.isEmpty()) {
            Object first = list.get(0);
            for (Property prop : BeanPropertiesFilter.filter(first.getClass())) {
                columns.add(new TitledPropertyColumn<Serializable>(new DisplayPropertyLocalizableModel(first.getClass(), prop.getName()), prop.getName()));
            }
            columns.add(new AbstractColumn(new Model("Edit")) {

                @Override
                public void populateItem(Item cellItem, String componentId, IModel rowModel) {
                    cellItem.add(new EditPanel(componentId, rowModel));
                }
            });
        }
        DataTable table = new DataTable("table", columns.toArray(new IColumn[columns.size()]), new IDataProvider<Serializable>() {

            public Iterator<Serializable> iterator(int first, int count) {
                return content.getObject().listIterator(first);
            }

            public int size() {
                return content.getObject().size();
            }

            public IModel model(Serializable object) {
                return new Model(object);
            }

            public void detach() {
            }
        }, 10);
        table.addTopToolbar(new HeadersToolbar(table, null));
        add(table);
    }

    public abstract void selected(Serializable obj);
}
