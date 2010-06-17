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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.vetcontrol.entity.Change;
import org.vetcontrol.information.web.component.edit.GoToListPagePanel;
import org.vetcontrol.information.util.change.BookChangeManager;
import org.vetcontrol.util.CloneUtil;
import org.vetcontrol.information.util.resource.CommonResourceKeys;
import org.vetcontrol.information.web.util.PageManager;
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

    private Serializable newBookEntry;

    private Serializable oldBookEntry;

    public AddUpdateBookEntryPage() {
        init();
    }

    public void init() {
        newBookEntry = getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);
        if (newBookEntry == null) {
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

        BeanPropertyUtil.addLocalization(newBookEntry, allLocales);

        if (!BeanPropertyUtil.isNewBook(newBookEntry)) {
            oldBookEntry = CloneUtil.cloneObject(newBookEntry);
        }

        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = BeanPropertyUtil.hash(newBookEntry);

        List<Property> filtered = new ArrayList(BeanPropertyUtil.getProperties(newBookEntry.getClass()));
        ListView<Property> fields = new ListView<Property>("bookFields", filtered) {

            @Override
            protected void populateItem(ListItem<Property> item) {
                Property prop = item.getModelObject();

                item.add(new Label("bookFieldDesc", new DisplayPropertyLocalizableModel(prop)));

                WebMarkupContainer requiredContainer = new WebMarkupContainer("bookFieldRequired");
                item.add(requiredContainer);

                boolean isSimpleText = false;
                boolean isDate = false;
                boolean isLocalizableText = false;
                boolean isSelectable = false;
                boolean isAutoComplete = false;
                boolean isBoolean = false;

                if (prop.getType().equals(String.class) || BeanPropertyUtil.isNumericType(prop.getType())) {
                    isSimpleText = true;
                } else if (BeanPropertyUtil.isDateType(prop.getType())) {
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
                } else if (BeanPropertyUtil.isBoolType(prop.getType())) {
                    isBoolean = true;
                }

                requiredContainer.setVisible(isLocalizableText ? false : !prop.isNullable());

                IModel m = new PropertyModel(newBookEntry, prop.getName());

                Panel datePanel = new EmptyPanel("datePanel");
                Panel textPanel = new EmptyPanel("textPanel");
                Panel localizableTextPanel = new EmptyPanel("localizableTextPanel");
                Panel selectablePanel = new EmptyPanel("selectablePanel");
                Panel autoCompleteSelectPanel = new EmptyPanel("autoCompleteSelectPanel");
                Panel booleanPanel = new EmptyPanel("booleanPanel");


                //choose what panel is editable:
                if (isSimpleText) {
                    textPanel = new TextPanel("textPanel", m, prop, CanEditUtil.canEdit(newBookEntry));
                } else if (isLocalizableText) {
                    localizableTextPanel = new LocalizableTextPanel("localizableTextPanel", m, prop, systemLocale, CanEditUtil.canEdit(newBookEntry));
                } else if (isDate) {
                    datePanel = new DatePanel("datePanel", m, prop, CanEditUtil.canEdit(newBookEntry));
                } else if (isSelectable) {
                    selectablePanel = new SelectPanel("selectablePanel", m, prop, bookDAO.getContent(prop.getType(), ShowBooksMode.ALL),
                            systemLocale, CanEditUtil.canEdit(newBookEntry));
                } else if (isAutoComplete) {
                    autoCompleteSelectPanel = new AutoCompleteSelectPanel("autoCompleteSelectPanel", m, prop, CanEditUtil.canEdit(newBookEntry),
                            systemLocale);
                } else if (isBoolean) {
                    booleanPanel = new BooleanPanel("booleanPanel", m, prop, CanEditUtil.canEdit(newBookEntry));
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
                if (BeanPropertyUtil.isNewBook(newBookEntry)) {
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
        saveOrUpdateBook.setVisible(CanEditUtil.canEdit(newBookEntry));
        form.add(saveOrUpdateBook);
        form.add(new GoToListPagePanel("goToListPagePanel", newBookEntry));
        form.add(new Spacer("spacer"));
    }

    private void saveOrUpdate(BookHash initial) {
        Log.EVENT event = BeanPropertyUtil.isNewBook(newBookEntry) ? Log.EVENT.CREATE : Log.EVENT.EDIT;

        Set<Change> changes = getChanges();

        Long oldId = BeanPropertyUtil.getId(newBookEntry);

        //update version of book and its localizable strings if necessary.
        BeanPropertyUtil.updateVersionIfNecessary(newBookEntry, initial);

        try {
            bookDAO.saveOrUpdate(newBookEntry);
            Long newId = BeanPropertyUtil.getId(newBookEntry);
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_UPDATE_KEY, this, null, new Object[]{newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, newBookEntry.getClass(), message, changes);
        } catch (Exception e) {
            log.error("Error with saving the book.", e);
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_UPDATE_KEY, this, null, new Object[]{oldId}).getObject();
            logBean.error(Log.MODULE.INFORMATION, event, AddUpdateBookEntryPage.class, newBookEntry.getClass(), message);
        }
    }

    private void saveAsNew() {
        Set<Change> changes = getChanges();

        Long oldId = BeanPropertyUtil.getId(newBookEntry);

        try {
            bookDAO.saveAsNew(newBookEntry);
            Long newId = BeanPropertyUtil.getId(newBookEntry);
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_AS_NEW_KEY, this, null, new Object[]{oldId, newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, AddUpdateBookEntryPage.class, newBookEntry.getClass(), message, changes);
        } catch (Exception e) {
            log.error("Error with saving the book.", e);
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_AS_NEW_KEY, this, null, new Object[]{oldId, null}).getObject();
            logBean.error(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, AddUpdateBookEntryPage.class, newBookEntry.getClass(), message);
        }
    }

    private void disableBook(Long id) {
        bookDAO.disable(id, newBookEntry.getClass());
        String message = new StringResourceModel(CommonResourceKeys.LOG_ENABLE_DISABLE_KEY, this, null, new Object[]{id}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, AddUpdateBookEntryPage.class, newBookEntry.getClass(), message);
    }

    private void enableBook(Long id) {
        bookDAO.enable(id, newBookEntry.getClass());
        String message = new StringResourceModel(CommonResourceKeys.LOG_ENABLE_DISABLE_KEY, this, null, new Object[]{id}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.ENABLE, AddUpdateBookEntryPage.class, newBookEntry.getClass(), message);
    }

    private void goToBooksPage() {
        PageManager.goToListPage(this, newBookEntry.getClass());
    }

    private Set<Change> getChanges() {
        if (oldBookEntry != null) {
            try {
                Set<Change> changes = BookChangeManager.getChanges(oldBookEntry, newBookEntry, getSystemLocale());

                if (log.isDebugEnabled()) {
                    for (Change change : changes) {
                        log.debug(change.toString());
                    }
                }
                return changes;
            } catch (Exception e) {
                log.error("Error with getting changes for " + oldBookEntry.getClass().getName() + " book entry.", e);
            }
        }
        return Collections.emptySet();
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableBook(BeanPropertyUtil.getId(newBookEntry));
                goToBooksPage();
            }

            @Override
            protected void onBeforeRender() {
                if (BeanPropertyUtil.isNewBook(newBookEntry) || !CanEditUtil.canEdit(newBookEntry)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        toolbarButtons.add(new EnableItemButton(id) {

            @Override
            protected void onClick() {
                enableBook(BeanPropertyUtil.getId(newBookEntry));
                goToBooksPage();
            }

            @Override
            protected void onBeforeRender() {
                if (BeanPropertyUtil.isNewBook(newBookEntry) || !CanEditUtil.canEditDisabled(newBookEntry)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }
}
