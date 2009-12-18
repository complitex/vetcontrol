/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages;

import org.vetcontrol.service.fasade.pages.AddUpdateBookEntryPageFasade;
import java.beans.IntrospectionException;
import java.io.Serializable;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.vetcontrol.web.components.BookEntryFormControl;

/**
 *
 * @author Artem
 */
public class AddUpdateBookEntryPage extends BasePage {

    @EJB(name="AddUpdateBookEntryPageFasade")
    private AddUpdateBookEntryPageFasade fasade;

    public AddUpdateBookEntryPage() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IntrospectionException {
        init();
    }

    public void init() throws IntrospectionException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        final Serializable bookEntry = getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);

        if (bookEntry == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }

        //messages
        add(new FeedbackPanel("messages"));

        //form
        Form form = new Form("form");
        add(form);

        form.add(new BookEntryFormControl("book", new Model(bookEntry)) {

            @Override
            public void saveOrUpdate() {
                fasade.saveOrUpdate(bookEntry);
                setResponsePage(BookPage.class);
            }

            @Override
            public void cancel() {
                setResponsePage(BookPage.class);
            }
        });
    }
}
