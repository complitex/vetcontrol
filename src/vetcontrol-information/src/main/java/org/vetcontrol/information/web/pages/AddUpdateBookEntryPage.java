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
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.entity.ILongId;
import org.vetcontrol.entity.Log;
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.information.web.component.edit.AutoCompleteSelectPanel;
import org.vetcontrol.information.web.component.edit.BooleanPanel;
import org.vetcontrol.information.web.component.edit.DatePanel;
import org.vetcontrol.information.web.component.edit.LocalizableTextPanel;
import org.vetcontrol.information.web.component.edit.SaveUpdateConfirmationDialog;
import org.vetcontrol.information.web.component.edit.SelectPanel;
import org.vetcontrol.information.web.component.edit.TextPanel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.util.book.BookHash;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.util.book.entity.ShowBooksMode;
import org.vetcontrol.web.component.toolbar.DisableItemButton;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_EDIT)
public class AddUpdateBookEntryPage extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(AddUpdateBookEntryPage.class);
    @EJB(name = "BookDAO")
    private IBookDAO bookDAO;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "LogBean")
    private LogBean logBean;
    private Serializable bookEntry;

    public AddUpdateBookEntryPage() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        init();
    }

    public void init() throws IntrospectionException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        bookEntry = getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);
        if (bookEntry == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }
        final String bookTypeName = getBookTypeName(bookEntry);

        final Locale systemLocale = localeDAO.systemLocale();

        //title
        add(new Label("title", new ResourceModel("page.title")));

        //messages
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        //form
        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        BeanPropertyUtil.addLocalization(bookEntry, localeDAO.all());
        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = BeanPropertyUtil.hash(bookEntry);

        List<Property> filtered = BeanPropertyUtil.getProperties(bookEntry.getClass());
        ListView<Property> fields = new ListView<Property>("bookFields", filtered) {

            @Override
            protected void populateItem(ListItem<Property> item) {
                Property prop = item.getModelObject();

                item.add(new Label("bookFieldDesc", new DisplayPropertyLocalizableModel(prop, this)));

                WebMarkupContainer requiredContainer = new WebMarkupContainer("bookFieldRequired");
                requiredContainer.setVisible(!prop.isNullable());
                item.add(requiredContainer);

                boolean isSimpleText = false;
                boolean isDate = false;
                boolean isLocalizableText = false;
                boolean isSelectable = false;
                boolean isAutoComplete = false;
                boolean isBoolean = false;

                if (prop.getType().equals(String.class) || prop.getType().equals(int.class) || prop.getType().equals(Integer.class)
                        || prop.getType().equals(long.class) || prop.getType().equals(Long.class)) {
                    isSimpleText = true;
                } else if (Date.class.isAssignableFrom(prop.getType())) {
                    isDate = true;
                } else if (prop.isLocalizable()) {
                    isLocalizableText = true;
                } else if (prop.isBookReference()) {
                    switch (prop.getUiType()) {
                        case SELECT:
                            isSelectable = true;
                            break;
                        case AUTO_COMPLETE:
                            isAutoComplete = true;
                            break;
                    }
                } else if (prop.getType().equals(boolean.class) || prop.getType().equals(Boolean.class)) {
                    isBoolean = true;
                }

                IModel m = new PropertyModel(bookEntry, prop.getName());

                Panel datePanel = new EmptyPanel("datePanel");
                Panel textPanel = new EmptyPanel("textPanel");
                Panel localizableTextPanel = new EmptyPanel("localizableTextPanel");
                Panel selectablePanel = new EmptyPanel("selectablePanel");
                Panel autoCompleteSelectPanel = new EmptyPanel("autoCompleteSelectPanel");
                Panel booleanPanel = new EmptyPanel("booleanPanel");

                //choose what panel is editable:
                if (isSimpleText) {
                    textPanel = new TextPanel("textPanel", m, prop);
                } else if (isLocalizableText) {
                    localizableTextPanel = new LocalizableTextPanel("localizableTextPanel", m, prop, systemLocale);
                } else if (isDate) {
                    datePanel = new DatePanel("datePanel", m, prop);
                } else if (isSelectable) {
                    selectablePanel = new SelectPanel("selectablePanel", m, prop, bookDAO.getContent(prop.getType(), ShowBooksMode.ENABLED),
                            systemLocale);
                } else if (isAutoComplete) {
                    autoCompleteSelectPanel = new AutoCompleteSelectPanel("autoCompleteSelectPanel", m, prop);
                } else if (isBoolean) {
                    booleanPanel = new BooleanPanel("booleanPanel", m, prop);
                }

                item.add(datePanel);
                item.add(textPanel);
                item.add(localizableTextPanel);
                item.add(selectablePanel);
                item.add(autoCompleteSelectPanel);
                item.add(booleanPanel);
            }
        };
        fields.setReuseItems(true);
        form.add(fields);

        final SaveUpdateConfirmationDialog confirmationDialog = new SaveUpdateConfirmationDialog("confirmationDialogPanel") {

            @Override
            public void update() {
                saveOrUpdate(bookEntry, initial);
                goToBooksPage(bookTypeName);
            }

            @Override
            public void createNew() {
                //disable old book entry.
                disableBookEntry();

                //save new book entry.
                BeanPropertyUtil.clearBook(bookEntry);
                saveOrUpdate(bookEntry, initial);
                goToBooksPage(bookTypeName);
            }
        };
        add(confirmationDialog);

        form.add(new AjaxSubmitLink("saveOrUpdateBook") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (BeanPropertyUtil.isNewBook(bookEntry)) {
                    saveOrUpdate(bookEntry, initial);
                    goToBooksPage(bookTypeName);
                } else {
                    confirmationDialog.open(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                goToBooksPage(bookTypeName);
            }
        });

    }

    private void saveOrUpdate(Serializable bookEntry, BookHash initial) {
        Long id = -1L;
        Log.EVENT event = BeanPropertyUtil.isNewBook(bookEntry) ? Log.EVENT.CREATE : Log.EVENT.EDIT;

        //update version of book and its localizable strings if necessary.
        BeanPropertyUtil.updateVersionIfNecessary(bookEntry, initial);
        try {
            bookDAO.saveOrUpdate(bookEntry);

            if (bookEntry instanceof ILongId) {
                id = ((ILongId) bookEntry).getId();
            }

            logBean.info(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, bookEntry.getClass(), "ID: " + id);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            logBean.error(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, bookEntry.getClass(), "ID: " + id);
        }
    }

    private void disableBookEntry() {
        Long id = -1L;
        if (bookEntry instanceof ILongId) {
            id = ((ILongId) bookEntry).getId();
        }
        bookDAO.disable(bookEntry);
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, AddUpdateBookEntryPage.class, bookEntry.getClass(), "ID: " + id);
    }

    private void goToBooksPage(String typeName) {
        PageParameters params = new PageParameters();
        params.add(BookPage.BOOK_TYPE, typeName);
        setResponsePage(BookPage.class, params);
    }

    private String getBookTypeName(Serializable bookEntry) {
        return bookEntry.getClass().getName();
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableBookEntry();
                goToBooksPage(getBookTypeName(bookEntry));
            }

            @Override
            protected void onBeforeRender() {
                if (BeanPropertyUtil.isNewBook(bookEntry)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }
}
