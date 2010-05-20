/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.io.Serializable;
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
import org.apache.wicket.model.StringResourceModel;
import org.vetcontrol.book.BeanPropertyUtil;
import org.vetcontrol.entity.Log;
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.information.web.util.BookWebInfoContainer;
import org.vetcontrol.information.web.util.CanEditUtil;
import org.vetcontrol.information.web.component.edit.AutoCompleteSelectPanel;
import org.vetcontrol.information.web.component.edit.BooleanPanel;
import org.vetcontrol.information.web.component.edit.DatePanel;
import org.vetcontrol.information.web.component.edit.LocalizableTextPanel;
import org.vetcontrol.information.web.component.edit.SaveUpdateConfirmationDialog;
import org.vetcontrol.information.web.component.edit.SelectPanel;
import org.vetcontrol.information.web.component.edit.TextPanel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.book.BookHash;
import org.vetcontrol.book.Property;
import org.vetcontrol.book.ShowBooksMode;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.component.toolbar.DisableItemButton;
import org.vetcontrol.web.component.toolbar.EnableItemButton;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public class AddUpdateBookEntryPage extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(AddUpdateBookEntryPage.class);

    @EJB(name = "BookDAO")
    private IBookDAO bookDAO;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "LogBean")
    private LogBean logBean;

    private Serializable bookEntry;

    public AddUpdateBookEntryPage() {
        init();
    }

    public void init() {
        bookEntry = getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);
        if (bookEntry == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }

        final Locale systemLocale = getSystemLocale();
        final List<Locale> allLocales = localeDAO.all();

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

        BeanPropertyUtil.addLocalization(bookEntry, allLocales);
        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = BeanPropertyUtil.hash(bookEntry);

        List<Property> filtered = BeanPropertyUtil.getProperties(bookEntry.getClass());
        ListView<Property> fields = new ListView<Property>("bookFields", filtered) {

            @Override
            protected void populateItem(ListItem<Property> item) {
                Property prop = item.getModelObject();

                item.add(new Label("bookFieldDesc", new DisplayPropertyLocalizableModel(prop, this)));

                WebMarkupContainer requiredContainer = new WebMarkupContainer("bookFieldRequired");
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

                requiredContainer.setVisible(isLocalizableText ? false : !prop.isNullable());

                IModel m = new PropertyModel(bookEntry, prop.getName());

                Panel datePanel = new EmptyPanel("datePanel");
                Panel textPanel = new EmptyPanel("textPanel");
                Panel localizableTextPanel = new EmptyPanel("localizableTextPanel");
                Panel selectablePanel = new EmptyPanel("selectablePanel");
                Panel autoCompleteSelectPanel = new EmptyPanel("autoCompleteSelectPanel");
                Panel booleanPanel = new EmptyPanel("booleanPanel");


                //choose what panel is editable:
                if (isSimpleText) {
                    textPanel = new TextPanel("textPanel", m, prop, CanEditUtil.canEdit(bookEntry));
                } else if (isLocalizableText) {
                    localizableTextPanel = new LocalizableTextPanel("localizableTextPanel", m, prop, systemLocale, CanEditUtil.canEdit(bookEntry));
                } else if (isDate) {
                    datePanel = new DatePanel("datePanel", m, prop, CanEditUtil.canEdit(bookEntry));
                } else if (isSelectable) {
                    selectablePanel = new SelectPanel("selectablePanel", m, prop, bookDAO.getContent(prop.getType(), ShowBooksMode.ALL),
                            systemLocale, CanEditUtil.canEdit(bookEntry));
                } else if (isAutoComplete) {
                    autoCompleteSelectPanel = new AutoCompleteSelectPanel("autoCompleteSelectPanel", m, prop, CanEditUtil.canEdit(bookEntry),
                            systemLocale);
                } else if (isBoolean) {
                    booleanPanel = new BooleanPanel("booleanPanel", m, prop, CanEditUtil.canEdit(bookEntry));
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
                saveOrUpdate(initial);
                goToBooksPage();
            }

            @Override
            public void createNew() {
                saveAsNew();
                goToBooksPage();
            }
        };
        add(confirmationDialog);

        AjaxSubmitLink saveOrUpdateBook = new AjaxSubmitLink("saveOrUpdateBook") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (BeanPropertyUtil.isNewBook(bookEntry)) {
                    saveOrUpdate(initial);
                    goToBooksPage();
                } else {
                    confirmationDialog.open(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        };
        saveOrUpdateBook.setVisible(CanEditUtil.canEdit(bookEntry));
        form.add(saveOrUpdateBook);

        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                goToBooksPage();
            }
        };
        cancel.setVisible(CanEditUtil.canEdit(bookEntry));
        form.add(cancel);

        Link back = new Link("back") {

            @Override
            public void onClick() {
                goToBooksPage();
            }
        };
        back.setVisible(!CanEditUtil.canEdit(bookEntry));
        form.add(back);

        form.add(new Spacer("spacer"));
    }

    private void saveOrUpdate(BookHash initial) {
        Log.EVENT event = BeanPropertyUtil.isNewBook(bookEntry) ? Log.EVENT.CREATE : Log.EVENT.EDIT;
        Long oldId = BeanPropertyUtil.getId(bookEntry);
        String resourceKey = "log.save_update";

        //update version of book and its localizable strings if necessary.
        BeanPropertyUtil.updateVersionIfNecessary(bookEntry, initial);

        try {
            bookDAO.saveOrUpdate(bookEntry);
            Long newId = BeanPropertyUtil.getId(bookEntry);
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, bookEntry.getClass(), message);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{oldId}).getObject();
            logBean.error(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, bookEntry.getClass(), message);
        }
    }

    private void saveAsNew() {
        Long oldId = BeanPropertyUtil.getId(bookEntry);
        String resourceKey = "log.save_as_new";
        try {
            bookDAO.saveAsNew(bookEntry);
            Long newId = BeanPropertyUtil.getId(bookEntry);
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{oldId, newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, AddUpdateBookEntryPage.class, bookEntry.getClass(), message);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{oldId, null}).getObject();
            logBean.error(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, AddUpdateBookEntryPage.class, bookEntry.getClass(), message);
        }
    }

    private void disableBook(Long id) {
        bookDAO.disable(id, bookEntry.getClass());
        String message = new StringResourceModel("log.enable_disable", this, null, new Object[]{id}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, AddUpdateBookEntryPage.class, bookEntry.getClass(), message);
    }

    private void enableBook(Long id) {
        bookDAO.enable(id, bookEntry.getClass());
        String message = new StringResourceModel("log.enable_disable", this, null, new Object[]{id}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.ENABLE, AddUpdateBookEntryPage.class, bookEntry.getClass(), message);
    }

    private void goToBooksPage() {
        setResponsePage(BookWebInfoContainer.getListPage(bookEntry.getClass()), BookWebInfoContainer.getListPageParameters(bookEntry.getClass()));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableBook(BeanPropertyUtil.getId(bookEntry));
                goToBooksPage();
            }

            @Override
            protected void onBeforeRender() {
                if (BeanPropertyUtil.isNewBook(bookEntry) || !CanEditUtil.canEdit(bookEntry)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        toolbarButtons.add(new EnableItemButton(id) {

            @Override
            protected void onClick() {
                enableBook(BeanPropertyUtil.getId(bookEntry));
                goToBooksPage();
            }

            @Override
            protected void onBeforeRender() {
                if (BeanPropertyUtil.isNewBook(bookEntry) || !CanEditUtil.canEditDisabled(bookEntry)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }
}
