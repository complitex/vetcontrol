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
import org.apache.wicket.PageParameters;
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
import org.apache.wicket.model.ResourceModel;
import org.vetcontrol.information.service.fasade.pages.AddUpdateBookEntryPageFasade;
import org.vetcontrol.information.service.fasade.pages.BookPageFasade;
import org.vetcontrol.information.web.component.BookContentControl;
import org.vetcontrol.information.web.component.LocalePicker;
import org.vetcontrol.information.web.support.BookTypes;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
public class BookPage extends TemplatePage {

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

        public void initSize(){
            Long localSize = fasade.size(filterBean);
            size = localSize == null ? 0 : localSize.intValue();
        }

        public void init(Class type) throws InstantiationException, IllegalAccessException {
            filterBean = (Serializable) type.newInstance();
        }
    }
    @EJB(name = "BookPageFasade")
    private BookPageFasade fasade;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "AddUpdateBookEntryPageFasade")
    private AddUpdateBookEntryPageFasade addUpdateBookEntryPageFasade;
    public static final MetaDataKey SELECTED_BOOK_ENTRY = new MetaDataKey() {
    };
    private static final MetaDataKey<Class> SELECTED_BOOK_TYPE = new MetaDataKey<Class>() {
    };
    static final String BOOK_TYPE = "bookType";
    private DataProvider dataProvider;

    public BookPage(PageParameters params) throws IntrospectionException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        init(params.getString(BOOK_TYPE));
    }

    public void init(String bookType) throws IntrospectionException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        final Class bookClass = Thread.currentThread().getContextClassLoader().loadClass(bookType);

        add(new LocalePicker("localePicker", localeDAO.all()));

        final Form form = new Form("form");
        add(form);

        dataProvider = new DataProvider();
        dataProvider.init(bookClass);
        dataProvider.initSize();

        if (dataProvider.size() != 0) {
            final BookContentControl bookContent = new BookContentControl("bookContent", dataProvider,
                    bookClass,
                    addUpdateBookEntryPageFasade,
                    localeDAO.systemLocale()) {

                @Override
                public void selected(Serializable obj) {
                    goToEditPage(obj);
                }
            };
            form.add(bookContent);
        } else {
            form.add(new Label("bookContent", new ResourceModel("book.content.empty")));
        }

        form.add(new SubmitLink("new") {

            @Override
            public void onSubmit() {
                try {
                    final Serializable entry = (Serializable) bookClass.newInstance();
                    goToEditPage(entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void goToEditPage(Serializable entry) {
        getSession().setMetaData(SELECTED_BOOK_ENTRY, entry);
        setResponsePage(AddUpdateBookEntryPage.class);
    }
}
