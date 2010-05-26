/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages;

import java.util.List;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.vetcontrol.web.component.toolbar.ToolbarButton;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.information.web.util.BookWebInfoContainer;
import org.vetcontrol.book.ShowBooksMode;
import org.vetcontrol.information.web.component.BookPropertyColumn;
import org.vetcontrol.information.web.component.list.IBookDataProvider;
import org.vetcontrol.information.web.component.list.ModifyColumn;
import org.vetcontrol.information.web.component.list.ShowBooksModePanel;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UIPreferences.PreferenceType;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.book.BeanPropertyUtil;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.web.util.PageManager;
import org.vetcontrol.web.component.datatable.ArrowHeadersToolbar;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.AddItemButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.ListTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public class BookPage extends ListTemplatePage {

    public class DataProvider extends SortableDataProvider<Serializable> implements IFilterStateLocator, IBookDataProvider {

        private Serializable filterBean;

        private LoadableDetachableModel<Integer> sizeModel;

        public DataProvider() {
        }

        @Override
        public Iterator<Serializable> iterator(int first, int count) {
            preferences.putPreference(PreferenceType.SORT_PROPERTY, filterBean.getClass().getSimpleName() + SORT_PROPERTY_KEY_SUFFIX,
                    getSort().getProperty());
            preferences.putPreference(PreferenceType.SORT_ORDER, filterBean.getClass().getSimpleName() + SORT_ORDER_KEY_SUFFIX,
                    Boolean.valueOf(getSort().isAscending()));
            preferences.putPreference(PreferenceType.FILTER, filterBean.getClass().getSimpleName() + FILTER_KEY_SUFFIX, filterBean);
            preferences.putPreference(PreferenceType.SHOW_BOOKS_MODE, filterBean.getClass().getSimpleName() + SHOW_BOOKS_MODE_KEY_SUFFIX,
                    showBooksModeModel.getObject());

            return bookViewDAO.getContent(filterBean, first, count, getSort().getProperty(), getSort().isAscending(),
                    BookPage.this.getLocale(), showBooksModeModel.getObject()).iterator();
        }

        @Override
        public int size() {
            return sizeModel.getObject();
        }

        @Override
        public IModel model(Serializable object) {
            return new Model(object);
        }

        @Override
        public Object getFilterState() {
            return filterBean;
        }

        @Override
        public void setFilterState(Object state) {
            this.filterBean = (Serializable) state;
        }

        @Override
        public void initSize() {
            sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return bookViewDAO.size(filterBean, showBooksModeModel.getObject()).intValue();
                }
            };
        }

        @Override
        public void init(Class type, String sortProperty, boolean isAscending) {
            try {
                //retrieve filter bean from preferences.
                filterBean = preferences.getPreference(PreferenceType.FILTER, type.getSimpleName() + FILTER_KEY_SUFFIX, Serializable.class);
                if (filterBean == null) {
                    filterBean = (Serializable) type.newInstance();
                }

                String sortPropertyFromPreferences = preferences.getPreference(PreferenceType.SORT_PROPERTY,
                        type.getSimpleName() + SORT_PROPERTY_KEY_SUFFIX, String.class);
                Boolean sortOrderFromPreferences = preferences.getPreference(PreferenceType.SORT_ORDER,
                        type.getSimpleName() + SORT_ORDER_KEY_SUFFIX, Boolean.class);
                String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : sortProperty;
                boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences.booleanValue() : isAscending;
                setSort(sortProp, asc);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(BookPage.class);

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    public static final MetaDataKey SELECTED_BOOK_ENTRY = new MetaDataKey() {
    };

    public static final String BOOK_TYPE = "bookType";

    private static final String FILTER_KEY_SUFFIX = "_FILTER";

    private static final String PAGE_NUMBER_KEY_SUFFIX = "_PAGING";

    private static final String SORT_PROPERTY_KEY_SUFFIX = "_SORT_PROPERTY";

    private static final String SORT_ORDER_KEY_SUFFIX = "_SORT_ORDER";

    private static final String SHOW_BOOKS_MODE_KEY_SUFFIX = "_SHOW_BOOKS_MODE";

    private UIPreferences preferences;

    private Class bookClass;

    private IModel<ShowBooksMode> showBooksModeModel;

    public BookPage(PageParameters params) {
        init(params.getString(BOOK_TYPE));
    }

    public void init(String bookClassName) {
        bookClass = getBookClass(bookClassName);
        preferences = getPreferences();

        ShowBooksMode showBooksModeFromPreferences = preferences.getPreference(PreferenceType.SHOW_BOOKS_MODE,
                bookClass.getSimpleName() + SHOW_BOOKS_MODE_KEY_SUFFIX, ShowBooksMode.class);
        showBooksModeModel = new Model(showBooksModeFromPreferences != null ? showBooksModeFromPreferences : ShowBooksMode.ENABLED);

        final DataProvider dataProvider = new DataProvider();
        dataProvider.init(bookClass, "id", true);
        dataProvider.initSize();

        //title
        add(new Label("title", new DisplayBookClassModel(bookClass)));

        Label bookName = new Label("bookName", new DisplayBookClassModel(bookClass));

        Panel showBooksModePanel = new ShowBooksModePanel("showBooksModePanel", showBooksModeModel);

        List<IColumn<? extends Serializable>> columns = new ArrayList<IColumn<? extends Serializable>>();
        for (Property prop : BeanPropertyUtil.getProperties(bookClass)) {
            columns.add(newColumn(this, new DisplayPropertyLocalizableModel(prop, this), prop));
        }
        columns.add(new ModifyColumn(bookClass, dataProvider) {

            @Override
            protected void selected(Serializable bookEntry) {
                goToEditPage(bookEntry);
            }
        });

        final DataTable table = new DataTable("table", columns.toArray(new IColumn[columns.size()]), dataProvider, 1);

        table.addTopToolbar(new ArrowHeadersToolbar(table, dataProvider));

        final FilterForm filterForm = new FilterForm("filterForm", dataProvider);

        table.addTopToolbar(new FilterToolbar(table, filterForm, dataProvider));
        filterForm.add(bookName);
        filterForm.add(table);
        filterForm.add(showBooksModePanel);
        add(new FeedbackPanel("messages"));
        add(filterForm);
        add(new PagingNavigator("navigator", table, "rowsPerPage", preferences, bookClass.getSimpleName() + PAGE_NUMBER_KEY_SUFFIX));
    }

    protected IColumn<? extends Serializable> newColumn(Component resourceComponent, IModel<String> displayPropertyModel, Property property) {
        return new BookPropertyColumn<Serializable>(resourceComponent, displayPropertyModel, property, bookViewDAO, getSystemLocale());
    }

    private void goToEditPage(Serializable bookEntry) {
        getSession().setMetaData(SELECTED_BOOK_ENTRY, bookEntry);
        PageManager.goToEditPage(this, bookClass);
    }

    private static Class getBookClass(String bookClassName) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(bookClassName);
        } catch (ClassNotFoundException e) {
            log.error("Couldn't to load {} class.", bookClassName);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        if (hasAnyRole(SecurityRoles.INFORMATION_EDIT)) {
            List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
            toolbarButtons.add(new AddItemButton(id) {

                @Override
                protected void onClick() {
                    try {
                        Serializable newBookEntry = (Serializable) bookClass.newInstance();
                        goToEditPage(newBookEntry);
                    } catch (Exception e) {
                        log.error("Couldn't to instantiate new instance of {} class.", bookClass);
                        throw new RuntimeException(e);
                    }
                }
            });
            return toolbarButtons;
        } else {
            return null;
        }
    }
}
