/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.service.fasade.pages.BookPageFasade;
import org.vetcontrol.web.components.BookContentControl;
import org.vetcontrol.web.support.BookTypes;

/**
 *
 * @author Artem
 */
public class BookPage extends BasePage {

    @EJB(name="BookPageFasade")
    private BookPageFasade fasade;

    public static final MetaDataKey SELECTED_BOOK_ENTRY = new MetaDataKey() {
    };
    private static final MetaDataKey<Class<?>> SELECTED_BOOK_TYPE = new MetaDataKey<Class<?>>() {
    };
    private Class<?> bookType;
    private List<Serializable> list;

    public BookPage() throws IntrospectionException {

        List<Class<?>> bookTypies = BookTypes.getBookTypes();
        if (getSession().getMetaData(SELECTED_BOOK_TYPE) == null) {
            bookType = bookTypies.get(0);
        } else {
            bookType = getSession().getMetaData(SELECTED_BOOK_TYPE);
        }

        final Form form = new Form("form");
        add(form);



        initBookContentList();
        addBookContent(form);

        final DropDownChoice types = new DropDownChoice<Class<?>>("type", new PropertyModel<Class<?>>(this, "bookType"), bookTypies,
                new ChoiceRenderer<Class<?>>("simpleName", "name")) {

            @Override
            protected void onSelectionChanged(Class<?> newSelection) {
                form.remove("bookContent");
                initBookContentList();
                try {
                    addBookContent(form);
                    getSession().setMetaData(SELECTED_BOOK_TYPE, bookType);
                } catch (IntrospectionException e) {
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
        if (list != null && !list.isEmpty()) {
            final IModel<List<Serializable>> model = new PropertyModel<List<Serializable>>(this, "list");
            final BookContentControl bookContent = new BookContentControl("bookContent", model, bookType.getSimpleName()) {

                @Override
                public void selected(Serializable obj) {
                    goToEditPage(obj);
                }
            };
            form.add(bookContent);
        }else{
            form.add(new Label("bookContent", "This book is empty."));
        }
    }

    private void initBookContentList() {
        list = fasade.getBookContent(bookType);
    }

    private void goToEditPage(Serializable entry) {
        getSession().setMetaData(SELECTED_BOOK_ENTRY, entry);
        setResponsePage(AddUpdateBookEntryPage.class);
    }
}
