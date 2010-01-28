/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.information.web.pages.BookPage;
import org.vetcontrol.information.web.pages.BookPage.DataProvider;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.web.component.datatable.ArrowHeadersToolbar;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
public abstract class BookContentControl extends Panel {

    private class ModifyColumnFilter extends Panel {

        public ModifyColumnFilter(String id) {
            super(id);
            Button goSearch = new Button("filter");
            add(goSearch);
        }
    }

    private class ModifyColumnHeader extends Panel {

        public ModifyColumnHeader(String id, final Class bookClass) {
            super(id);
            Button clear = new Button("header") {

                @Override
                public void onSubmit() {
                    try {
                        Object filterBean = bookClass.newInstance();
                        getForm().setDefaultModelObject(filterBean);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            add(clear);
        }
    }

    private class ModifyColumn implements IFilteredColumn, IColumn {

        private Class bookClass;

        public ModifyColumn(Class bookClass) {
            this.bookClass = bookClass;
        }

        @Override
        public void populateItem(Item cellItem, String componentId, IModel rowModel) {
            cellItem.add(new EditPanel(componentId, rowModel));
        }

        @Override
        public Component getFilter(String componentId, FilterForm form) {
            Panel filter = new ModifyColumnFilter(componentId);
            return filter;
        }

        @Override
        public Component getHeader(String componentId) {
            Panel header = new ModifyColumnHeader(componentId, bookClass);
            return header;
        }

        @Override
        public String getSortProperty() {
            return null;
        }

        @Override
        public boolean isSortable() {
            return false;
        }

        @Override
        public void detach() {
        }
    }

    private class EditPanel extends Panel {

        public EditPanel(String id, final IModel<Serializable> model) {
            super(id, model);

            Link editLink = new Link("edit") {

                @Override
                public void onClick() {
                    selected(model.getObject());
                }
            };
            MetaDataRoleAuthorizationStrategy.authorize(editLink, ENABLE, SecurityRoles.INFORMATION_EDIT);
            add(editLink);
        }
    }

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    public BookContentControl(String id, final DataProvider dataProvider, final Class bookClass, Locale systemLocale,
            final UIPreferences preferences) throws IntrospectionException {
        super(id);

        add(new Label("bookName", new DisplayBookClassModel(bookClass)));

        List<IColumn<Serializable>> columns = new ArrayList<IColumn<Serializable>>();

        for (Property prop : BeanPropertyUtil.getProperties(bookClass)) {
            columns.add(new BookPropertyColumn<Serializable>(this, new DisplayPropertyLocalizableModel(prop, this), prop, bookViewDAO, systemLocale));
        }
        columns.add(new ModifyColumn(bookClass));

        final DataTable table = new DataTable("table", columns.toArray(new IColumn[columns.size()]), dataProvider, 1) ;

        table.addTopToolbar(new ArrowHeadersToolbar(table, dataProvider));

        final FilterForm filterForm = new FilterForm("filterForm", dataProvider) {

            @Override
            protected void onSubmit() {
                dataProvider.initSize();
            }
        };

        table.addTopToolbar(new FilterToolbar(table, filterForm, dataProvider));
        filterForm.add(table);
        add(filterForm);
        add(new PagingNavigator("navigator", table, "rowsPerPage", preferences, bookClass.getSimpleName() + BookPage.PAGE_NUMBER_KEY_SUFFIX));
    }

    public abstract void selected(Serializable obj);
}
