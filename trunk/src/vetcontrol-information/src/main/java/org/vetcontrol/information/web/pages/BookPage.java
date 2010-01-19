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
import org.vetcontrol.information.service.fasade.pages.BookPageFasade;
import org.vetcontrol.information.web.component.BookContentControl;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.wicket.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.LoadableDetachableModel;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UIPreferences.PreferenceType;
import org.vetcontrol.web.component.toolbar.AddItemButton;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public class BookPage extends FormTemplatePage {
    
    public class DataProvider extends SortableDataProvider<Serializable> implements IFilterStateLocator {

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

            return fasade.getContent(filterBean, first, count, getSort().getProperty(), getSort().isAscending(), BookPage.this.getLocale()).iterator();
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

        public void initSize() {
            sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return fasade.size(filterBean).intValue();
                }
            };
        }

        public void init(Class type, String sortProperty, boolean isAscending) throws InstantiationException, IllegalAccessException {
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
        }
    }
    @EJB(name = "BookPageFasade")
    private BookPageFasade fasade;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    public static final MetaDataKey SELECTED_BOOK_ENTRY = new MetaDataKey() {
    };
    public static final String BOOK_TYPE = "bookType";
    public static final String FILTER_KEY_SUFFIX = "_FILTER";
    public static final String PAGE_NUMBER_KEY_SUFFIX = "_PAGING";
    public static final String SORT_PROPERTY_KEY_SUFFIX = "_SORT_PROPERTY";
    public static final String SORT_ORDER_KEY_SUFFIX = "_SORT_ORDER";
    private DataProvider dataProvider;
    private UIPreferences preferences;
    private String bookType;

    public BookPage(PageParameters params) throws IntrospectionException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        init(params.getString(BOOK_TYPE));
    }

    public void init(String bookType) throws IntrospectionException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        this.bookType = bookType;
        Class bookClass = getBookClass();
        preferences = getPreferences();

        dataProvider = new DataProvider();
        dataProvider.init(bookClass, "id", true);
        dataProvider.initSize();

        //TODO: remove after review
        /*
        Panel bookContent = new EmptyPanel("bookContent");
        WebMarkupContainer emptyContent = new WebMarkupContainer("emptyContent");
         */

        /*if (dataProvider.size() != 0) {*/
            BookContentControl bookContent = new BookContentControl("bookContent", dataProvider,
                    bookClass,
                    fasade,
                    localeDAO.systemLocale(), preferences) {

                @Override
                public void selected(Serializable obj) {
                    goToEditPage(obj);
                }
            };
            /*emptyContent.setVisible(false);
        }
             */
        add(bookContent);
        /*add(emptyContent);*/


        //TODO: remove after review
        /*
        final Form form = new Form("form");
        add(form);

        form.add(new SubmitLink("new") {

        @Override
        public void onSubmit() {
        try {
        final Serializable entry = (Serializable) getBookClass().newInstance();
        goToEditPage(entry);
        } catch (Exception e) {
        e.printStackTrace();
        }
        }
        });
         */
    }

    private void goToEditPage(Serializable entry) {
        getSession().setMetaData(SELECTED_BOOK_ENTRY, entry);
        setResponsePage(AddUpdateBookEntryPage.class);
    }

    private Class getBookClass() throws ClassNotFoundException {
        final Class bookClass = Thread.currentThread().getContextClassLoader().loadClass(bookType);
        return bookClass;
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        IRoleCheckingStrategy application = (IRoleCheckingStrategy) getApplication();
        if (application.hasAnyRole(new Roles(SecurityRoles.INFORMATION_EDIT))) {
            List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
            toolbarButtons.add(new AddItemButton(id) {

                @Override
                protected void onClick() {
                    try {
                        final Serializable entry = (Serializable) getBookClass().newInstance();
                        goToEditPage(entry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return toolbarButtons;
        } else {
            return null;
        }
    }
}
