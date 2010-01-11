/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.vetcontrol.information.service.fasade.pages.AddUpdateBookEntryPageFasade;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.web.component.BookEntryFormControl;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Artem
 */
public class AddUpdateBookEntryPage extends FormTemplatePage {

    @EJB(name = "AddUpdateBookEntryPageFasade")
    private AddUpdateBookEntryPageFasade fasade;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    public AddUpdateBookEntryPage() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        init();
    }

    public void init() throws IntrospectionException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final Serializable bookEntry = getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);

        if (bookEntry == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }

        //messages
        add(new FeedbackPanel("messages"));

        //form
        Form form = new Form("form");
        add(form);

        BeanPropertyUtil.addLocalization(bookEntry, localeDAO.all());

        form.add(new BookEntryFormControl("book", new Model(bookEntry), localeDAO.systemLocale(), fasade) {

            @Override
            public void saveOrUpdate() {
                fasade.saveOrUpdate(bookEntry);
                goToBooksPage();
            }

            private void goToBooksPage() {
                PageParameters params = new PageParameters();
                params.add(BookPage.BOOK_TYPE, bookEntry.getClass().getName());
                setResponsePage(BookPage.class, params);
            }

            @Override
            public void cancel() {
                goToBooksPage();
            }
        });
    }
}
