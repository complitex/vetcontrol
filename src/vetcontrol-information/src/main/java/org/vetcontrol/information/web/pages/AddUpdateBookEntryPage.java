/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Localizable;
import org.vetcontrol.entity.Log;
import org.vetcontrol.information.service.fasade.pages.AddUpdateBookEntryPageFasade;
import org.vetcontrol.information.web.component.edit.BookEntryFormControl;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import org.vetcontrol.util.book.BookHash;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_EDIT)
public class AddUpdateBookEntryPage extends FormTemplatePage {
    private static final Logger log = LoggerFactory.getLogger(AddUpdateBookEntryPage.class);

    @EJB(name = "AddUpdateBookEntryPageFasade")
    private AddUpdateBookEntryPageFasade fasade;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "LogBean")
    private LogBean logBean;

    public AddUpdateBookEntryPage() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        init();
    }

    public void init() throws IntrospectionException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Serializable bookEntry = getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);

        if (bookEntry == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }

        //title
        add(new Label("title", new ResourceModel("page.title")));

        //messages
        add(new FeedbackPanel("messages"));

        //form
        Form form = new Form("form");
        add(form);

        BeanPropertyUtil.addLocalization(bookEntry, localeDAO.all());
        
        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = BeanPropertyUtil.hash(bookEntry);

        form.add(new BookEntryFormControl("book", new Model(bookEntry), localeDAO.systemLocale()) {

            @Override
            public void saveOrUpdate() {
                //TODO get description to log
                Long id = -1L;
                Log.EVENT event = null;
                if (bookEntry instanceof Localizable){
                    id = ((Localizable)bookEntry).getId();
                    if (id == null){
                        event = Log.EVENT.CREATE;
                    }else if (id > 0){
                        event = Log.EVENT.EDIT;
                    }
                }

                //update version of book and its localizable strings if necessary.
                BeanPropertyUtil.updateVersionIfNecessary(bookEntry, initial);
                try {
                    fasade.saveOrUpdate(bookEntry);

                    if (bookEntry instanceof Localizable){
                        id = ((Localizable)bookEntry).getId();
                    }

                    logBean.info(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, bookEntry.getClass(), "ID: " + id);
                } catch (Exception e) {
                    log.error("Ошибка сохранения справочника", e);
                    logBean.error(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, bookEntry.getClass(), "ID: " + id);
                }

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
