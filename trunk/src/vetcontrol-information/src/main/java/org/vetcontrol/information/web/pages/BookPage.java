/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.information.service.fasade.pages.BookPageFasade;
import org.vetcontrol.information.web.component.BookContentControl;
import org.vetcontrol.information.web.component.LocalePicker;
import org.vetcontrol.web.pages.BasePage;
import org.vetcontrol.information.web.support.BookTypes;
import org.vetcontrol.service.dao.ILocaleDAO;

/**
 *
 * @author Artem
 */
public class BookPage extends BasePage {

    public class DataProvider implements IDataProvider<Serializable>, IFilterStateLocator {

        private Serializable filterBean;
        private int size;

        public DataProvider() {
        }

        @Override
        public Iterator<Serializable> iterator(int first, int count) {
            return fasade.getContent(filterBean, first, count).iterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public IModel model(Serializable object) {
            return new Model(object);
        }

        @Override
        public void detach() {
        }

        @Override
        public Object getFilterState() {
            return filterBean;
        }

        @Override
        public void setFilterState(Object state) {
            this.filterBean = (Serializable) state;
        }

        public void init() {
            Long localSize = fasade.size(filterBean);
            size = localSize == null ? 0 : localSize.intValue();
        }

        public void init(Class bookType) throws InstantiationException, IllegalAccessException {
            filterBean = (Serializable) bookType.newInstance();
        }
    }
    @EJB(name = "BookPageFasade")
    private BookPageFasade fasade;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    public static final MetaDataKey SELECTED_BOOK_ENTRY = new MetaDataKey() {
    };
    private static final MetaDataKey<Class> SELECTED_BOOK_TYPE = new MetaDataKey<Class>() {
    };
    private Class<Serializable> bookType;
    private DataProvider dataProvider;

    public BookPage() throws IntrospectionException, InstantiationException, IllegalAccessException {

        List<Class> bookTypies = BookTypes.BOOK_TYPES;
        if (getSession().getMetaData(SELECTED_BOOK_TYPE) == null) {
            bookType = bookTypies.get(0);
        } else {
            bookType = getSession().getMetaData(SELECTED_BOOK_TYPE);
        }

        add(new LocalePicker("localePicker", localeDAO.all()));

        final Form form = new Form("form");
        add(form);

        dataProvider = new DataProvider();

        initBookContentList();
        addBookContent(form);

        final DropDownChoice types = new DropDownChoice<Class>("type", new PropertyModel<Class>(this, "bookType"), bookTypies,
                new ChoiceRenderer<Class>("simpleName", "name")) {

            @Override
            protected void onSelectionChanged(Class newSelection) {
                form.remove("bookContent");
                try {
                    initBookContentList();
                    addBookContent(form);
                    getSession().setMetaData(SELECTED_BOOK_TYPE, bookType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        };
        form.add(types);

        form.add(new SubmitLink("new") {

            @Override
            public void onSubmit() {
                try {
                    final Serializable entry = (Serializable) bookType.newInstance();
                    goToEditPage(entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void addBookContent(final Form form) throws IntrospectionException {
        if (dataProvider.size() != 0) {
            final BookContentControl bookContent = new BookContentControl("bookContent", dataProvider, bookType.getSimpleName(), localeDAO.systemLocale()) {

                @Override
                public void selected(Serializable obj) {
                    goToEditPage(obj);
                }
            };
            form.add(bookContent);
        } else {
            form.add(new Label("bookContent", "This book is empty."));
        }
    }

    private void initBookContentList() throws InstantiationException, IllegalAccessException {
        dataProvider.init(bookType);
        dataProvider.init();
    }

    private void goToEditPage(Serializable entry) {
        getSession().setMetaData(SELECTED_BOOK_ENTRY, entry);
        setResponsePage(AddUpdateBookEntryPage.class);
    }
}
