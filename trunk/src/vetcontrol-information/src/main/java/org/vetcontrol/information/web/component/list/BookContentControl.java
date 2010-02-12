/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.list;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
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

/**
 *
 * @author Artem
 */
public abstract class BookContentControl extends Panel implements ISelectable {

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
        columns.add(new ModifyColumn(bookClass, this));

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
}
