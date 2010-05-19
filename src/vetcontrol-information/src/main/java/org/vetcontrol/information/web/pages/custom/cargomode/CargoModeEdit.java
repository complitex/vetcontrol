/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages.custom.cargomode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoModeCargoType;
import org.vetcontrol.entity.CargoModeUnitType;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.UnitType;
import org.vetcontrol.information.service.dao.CargoModeDAO;
import org.vetcontrol.information.util.web.BookTypeWebInfoUtil;
import org.vetcontrol.information.util.web.BookWebInfo;
import org.vetcontrol.information.util.web.CanEditUtil;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.information.util.web.TruncateUtil;
import org.vetcontrol.information.web.component.BookChoiceRenderer;
import org.vetcontrol.web.component.book.DisableAwareDropDownChoice;
import org.vetcontrol.information.web.component.edit.AbstractAutoCompleteTextField;
import org.vetcontrol.information.web.component.edit.LocalizableTextPanel;
import org.vetcontrol.information.web.component.edit.SaveUpdateConfirmationDialog;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.book.BookHash;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.component.book.IDisableAwareChoiceRenderer;
import org.vetcontrol.web.component.book.SimpleDisableAwareChoiceRenderer;
import org.vetcontrol.web.component.list.AjaxRemovableListView;
import org.vetcontrol.web.component.toolbar.DisableItemButton;
import org.vetcontrol.web.component.toolbar.EnableItemButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public final class CargoModeEdit extends FormTemplatePage {

    private class UnitTypesChoiceContainer extends WebMarkupContainer {

        private class UnitTypesModel extends LoadableDetachableModel<List<UnitType>> {

            private CargoMode cargoMode;

            private CargoModeUnitType cargoModeUnitType;

            public UnitTypesModel(CargoMode cargoMode, CargoModeUnitType cargoModeUnitType) {
                this.cargoMode = cargoMode;
                this.cargoModeUnitType = cargoModeUnitType;
            }

            @Override
            protected List<UnitType> load() {
                List<UnitType> exclude = new ArrayList<UnitType>();
                for (CargoModeUnitType cmut : cargoMode.getCargoModeUnitTypes()) {
                    UnitType unitType = cmut.getUnitType();
                    if (unitType != null) {
                        boolean toExclude = true;
                        UnitType currentUnitType = cargoModeUnitType.getUnitType();
                        if (currentUnitType != null) {
                            if (currentUnitType.getId().equals(unitType.getId())) {
                                toExclude = false;
                            }
                        }

                        if (toExclude) {
                            exclude.add(unitType);
                        }
                    }
                }
                return cargoModeDAO.getAvailableUnitTypes(exclude);
            }
        }

        public UnitTypesChoiceContainer(String id, final CargoMode cargoMode, final CargoModeUnitType cargoModeUnitType,
                final IModel<UnitType> model, final Locale systemLocale) {
            super(id);

            IModel<UnitType> choiceModel = new IModel<UnitType>() {

                @Override
                public UnitType getObject() {
                    return model.getObject();
                }

                @Override
                public void setObject(UnitType object) {
                    UnitType old = model.getObject();
                    if (old == null || !old.equals(object)) {
                        cargoModeUnitType.setNeedToUpdateVersion(true);
                    }
                    model.setObject(object);
                }

                @Override
                public void detach() {
                }
            };
            IDisableAwareChoiceRenderer<UnitType> renderer = new SimpleDisableAwareChoiceRenderer<UnitType>() {

                @Override
                public Object getDisplayValue(UnitType object) {
                    return object.getDisplayName(getLocale(), systemLocale);
                }

                @Override
                public String getIdValue(UnitType object, int index) {
                    return String.valueOf(object.getId());
                }
            };
            DropDownChoice<UnitType> unitTypeSelect = new DisableAwareDropDownChoice<UnitType>("unitTypeSelect", choiceModel,
                    new UnitTypesModel(cargoMode, cargoModeUnitType), renderer);
            unitTypeSelect.setEnabled(CanEditUtil.canEdit(cargoModeModel.getObject()));
            unitTypeSelect.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });

            add(unitTypeSelect);
        }
    }

    private class UKTZEDPanel extends Panel {

        private AbstractAutoCompleteTextField<CargoType> autoCompleteTextField;

        private CargoModeCargoType cargoModeCargoType;

        public UKTZEDPanel(String id, final CargoMode cargoMode, CargoModeCargoType cmct,
                final Locale systemLocale) {
            super(id);

            this.cargoModeCargoType = cmct;

            AutoCompleteSettings settings = new AutoCompleteSettings();
            settings.setAdjustInputWidth(false);

            final AbstractAutoCompleteTextRenderer<CargoType> renderer = new AbstractAutoCompleteTextRenderer<CargoType>() {

                @Override
                protected String getTextValue(CargoType cargoType) {
                    return cargoType.getCode();
                }

                @Override
                protected void renderChoice(CargoType cargoType, Response response, String criteria) {
                    String text = getText(cargoType, systemLocale);
                    response.write(text.length() <= Constants.AUTO_COMPLETE_SELECT_MAX_LENGTH ? text
                            : text.substring(0, Constants.AUTO_COMPLETE_SELECT_MAX_LENGTH) + Constants.CONTINUE);
                }
            };

            class AutoCompleteTextFieldModel extends Model<String> {

                private String localObject;

                @Override
                public String getObject() {
                    CargoType objectModel = cargoModeCargoType.getCargoType();
                    if (objectModel == null) {
                        return localObject;
                    } else {
                        return objectModel.getCode();
                    }
                }

                @Override
                public void setObject(String object) {
                    CargoType cargoType = autoCompleteTextField.findChoice();
                    CargoType old = cargoModeCargoType.getCargoType();
                    if (old == null || !old.equals(cargoType)) {
                        cargoModeCargoType.setNeedToUpdateVersion(true);
                    }
                    cargoModeCargoType.setCargoType(cargoType);
                    localObject = object;
                }
            }

            final AutoCompleteTextFieldModel autoCompleteTextFieldModel = new AutoCompleteTextFieldModel();

            autoCompleteTextField = new AbstractAutoCompleteTextField<CargoType>("autoCompleteTextField", autoCompleteTextFieldModel, String.class,
                    renderer, settings) {

                @Override
                protected List<CargoType> getChoiceList(String searchTextInput) {
                    if (!Strings.isEmpty(searchTextInput)) {
                        List<CargoType> exclude = new ArrayList<CargoType>();
                        for (CargoModeCargoType cmct : cargoMode.getCargoModeCargoTypes()) {
                            CargoType cargoType = cmct.getCargoType();
                            if (cargoType != null) {
                                boolean toExclude = true;
                                CargoType currentCargoType = cargoModeCargoType.getCargoType();
                                if (currentCargoType != null) {
                                    if (currentCargoType.getId().equals(cargoType.getId())) {
                                        toExclude = false;
                                    }
                                }

                                if (toExclude) {
                                    exclude.add(cargoType);
                                }
                            }
                        }
                        return cargoModeDAO.getAvailableCargoTypes(searchTextInput, Constants.AUTO_COMPLETE_TEXT_FIELD_MAX_ITEMS, exclude);
                    } else {
                        return Collections.emptyList();
                    }
                }

                @Override
                protected String getChoiceValue(CargoType choice) throws Throwable {
                    return choice.getCode();
                }

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    CargoType objectModel = cargoModeCargoType.getCargoType();
                    if (objectModel != null) {
                        String title = getText(objectModel, systemLocale);
                        tag.put("title", title);
                    }
                }
            };
            autoCompleteTextField.setEnabled(CanEditUtil.canEdit(cargoModeModel.getObject()));
            autoCompleteTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.addComponent(autoCompleteTextField);
                }
            });
            autoCompleteTextField.setOutputMarkupId(true);
            add(autoCompleteTextField);
        }

        private String getText(CargoType cargoType, Locale systemLocale) {
            return cargoType.getCode() + "   " + cargoType.getDisplayName(getLocale(), systemLocale);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(CargoModeEdit.class);

    private static final String UNIT_TYPE_INCORRECT = "unit_type_incorrect";

    private static final String CARGO_TYPE_INCORRECT = "cargo_type_incorrect";

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    @EJB(name = "CargoModeDAO")
    private CargoModeDAO cargoModeDAO;

    @EJB(name = "LogBean")
    private LogBean logBean;

    private Model<CargoMode> cargoModeModel;

    public CargoModeEdit() {
        init();
    }

    private void init() {
        final Locale systemLocale = getSystemLocale();
        final List<Locale> allLocales = localeDAO.all();

        final CargoMode cargoMode = getSession().getMetaData(CargoModeList.SELECTED_BOOK_ENTRY);
        if (cargoMode == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }
        bookViewDAO.addLocalizationSupport(cargoMode);
        addLocalization(cargoMode, allLocales);

        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = hash(cargoMode);

        cargoModeModel = new Model<CargoMode>(cargoMode);

        //title
        add(new Label("title", new DisplayBookClassModel(CargoMode.class)));
        add(new Label("caption", new DisplayBookClassModel(CargoMode.class)));

        //messages
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        //form
        final Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        //cargo mode name
        form.add(new LocalizableTextPanel("name",
                new PropertyModel(cargoModeModel, "names"),
                getPropertyByName(CargoMode.class, "names"),
                systemLocale, CanEditUtil.canEdit(cargoModeModel.getObject())));


        WebMarkupContainer parentZone = new WebMarkupContainer("parentZone");
        parentZone.setVisible(isNewBook(cargoModeModel.getObject()) || !isRootCargoMode(cargoModeModel.getObject()));
        form.add(parentZone);

        IModel<List<CargoMode>> rootCargoModeModel = new LoadableDetachableModel<List<CargoMode>>() {

            @Override
            protected List<CargoMode> load() {
                return cargoModeDAO.getRootCargoModes();
            }
        };
        BookChoiceRenderer parentChoiceRenderer = new BookChoiceRenderer(getPropertyByName(CargoMode.class, "parent"), systemLocale,
                TruncateUtil.TRUNCATE_SELECT_VALUE_IN_EDIT_PAGE);
        final DropDownChoice<CargoMode> parentChoice = new DisableAwareDropDownChoice<CargoMode>("parent",
                new PropertyModel(cargoModeModel, "parent"),
                rootCargoModeModel,
                parentChoiceRenderer);
        parentChoice.setEnabled(CanEditUtil.canEdit(cargoModeModel.getObject()));
        parentZone.add(parentChoice);

        //cargo and unit types container
        final WebMarkupContainer cargoAndUnitTypes = new WebMarkupContainer("cargoAndUnitTypes");
        cargoAndUnitTypes.setOutputMarkupPlaceholderTag(true);
        cargoAndUnitTypes.setVisible(!isRootCargoMode(cargoModeModel.getObject()));
        form.add(cargoAndUnitTypes);

        parentChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (!isRootCargoMode(cargoModeModel.getObject())) {
                    parentChoice.setRequired(true);
                    cargoAndUnitTypes.setVisible(true);
                }
                target.addComponent(parentChoice);
                target.addComponent(cargoAndUnitTypes);
            }
        });

        //list of associated cargo types.
        final WebMarkupContainer cargoTypesContainer = new WebMarkupContainer("cargoTypesContainer");
        cargoTypesContainer.setOutputMarkupId(true);
        cargoAndUnitTypes.add(cargoTypesContainer);

        final AjaxLink addCargoType = new AjaxLink("addCargoType") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                CargoModeCargoType cargoModeCargoType = new CargoModeCargoType();
                cargoModeModel.getObject().addCargoModeCargoType(cargoModeCargoType);
                target.addComponent(cargoTypesContainer);

                String setFocusOnNewElement = "newElementFirstInputId = $('.cargo_types tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newElementFirstInputId);";
                target.appendJavascript(setFocusOnNewElement);
            }
        };
        addCargoType.setVisible(CanEditUtil.canEdit(cargoModeModel.getObject()));
        cargoTypesContainer.add(addCargoType);

        final ListView<CargoModeCargoType> cargoTypes = new AjaxRemovableListView<CargoModeCargoType>("cargoTypes",
                cargoModeModel.getObject().getCargoModeCargoTypes()) {

            @Override
            protected void populateItem(ListItem<CargoModeCargoType> item) {
                UKTZEDPanel uktzed = new UKTZEDPanel("cargoType", cargoMode, item.getModelObject(), systemLocale);
                item.add(uktzed);

                addRemoveLink("deleteCargoType", item, addCargoType, cargoTypesContainer).
                        setVisible(CanEditUtil.canEdit(cargoModeModel.getObject()));
            }
        };
        cargoTypesContainer.add(cargoTypes);

        //list of associated unit types.
        final WebMarkupContainer unitTypesContainer = new WebMarkupContainer("unitTypesContainer");
        unitTypesContainer.setOutputMarkupId(true);
        cargoAndUnitTypes.add(unitTypesContainer);

        final AjaxLink addUnitType = new AjaxLink("addUnitType") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                CargoModeUnitType cargoModeUnitType = new CargoModeUnitType();
                cargoModeModel.getObject().addCargoModeUnitType(cargoModeUnitType);
                target.addComponent(unitTypesContainer);

                String setFocusOnNewElement = "newElementFirstInputId = $('.unit_types tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newElementFirstInputId);";
                target.appendJavascript(setFocusOnNewElement);
            }
        };
        addUnitType.setVisible(CanEditUtil.canEdit(cargoModeModel.getObject()));
        unitTypesContainer.add(addUnitType);

        final ListView<CargoModeUnitType> unitTypes = new AjaxRemovableListView<CargoModeUnitType>("unitTypes",
                cargoModeModel.getObject().getCargoModeUnitTypes()) {

            @Override
            protected void populateItem(ListItem<CargoModeUnitType> item) {
                UnitTypesChoiceContainer unitTypesChoiceContainer = new UnitTypesChoiceContainer("unitType", cargoModeModel.getObject(),
                        item.getModelObject(), new PropertyModel<UnitType>(item.getModelObject(), "unitType"), systemLocale);
                item.add(unitTypesChoiceContainer);

                addRemoveLink("deleteUnitType", item, addUnitType, unitTypesContainer).
                        setVisible(CanEditUtil.canEdit(cargoModeModel.getObject()));
            }
        };
        unitTypesContainer.add(unitTypes);

        final SaveUpdateConfirmationDialog confirmationDialog = new SaveUpdateConfirmationDialog("confirmationDialogPanel") {

            @Override
            public void update() {
                saveOrUpdate(cargoModeModel, initial);
                goToListPage();
            }

            @Override
            public void createNew() {
                saveAsNew();
                goToListPage();
            }
        };
        add(confirmationDialog);
        //save and cancel links.
        AjaxSubmitLink save = new AjaxSubmitLink("save") {

            private boolean validated;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (validate()) {
                    if (isNewBook(cargoModeModel.getObject())) {
                        //new entry
                        saveOrUpdate(cargoModeModel, initial);
                        goToListPage();
                    } else {
                        confirmationDialog.open(target);
                    }
                } else {
                    target.addComponent(messages);
                    target.addComponent(form);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                validate();
                target.addComponent(messages);
                validated = false;
            }

            private boolean validate() {
                if (!validated) {
                    validated = true;
                    boolean isValid = true;
                    for (CargoModeCargoType cargoModeCargoType : cargoModeModel.getObject().getCargoModeCargoTypes()) {
                        CargoType cargoType = cargoModeCargoType.getCargoType();
                        if (cargoType == null) {
                            isValid = false;
                            error(getString(CARGO_TYPE_INCORRECT));
                            break;
                        }
                    }
                    for (CargoModeUnitType cargoModeUnitType : cargoModeModel.getObject().getCargoModeUnitTypes()) {
                        UnitType unitType = cargoModeUnitType.getUnitType();
                        if (unitType == null) {
                            isValid = false;
                            error(getString(UNIT_TYPE_INCORRECT));
                            break;
                        }
                    }
                    return isValid;
                }
                return false;
            }
        };
        save.setVisible(CanEditUtil.canEdit(cargoModeModel.getObject()));
        form.add(save);

        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                goToListPage();
            }
        };
        cancel.setVisible(CanEditUtil.canEdit(cargoModeModel.getObject()));
        form.add(cancel);

        Link back = new Link("back") {

            @Override
            public void onClick() {
                goToListPage();
            }
        };
        back.setVisible(!CanEditUtil.canEdit(cargoModeModel.getObject()));
        form.add(back);

        form.add(new Spacer("spacer"));
    }

    private void saveOrUpdate(IModel<CargoMode> cargoModeModel, BookHash initial) {
        Log.EVENT event = isNewBook(cargoModeModel.getObject()) ? Log.EVENT.CREATE : Log.EVENT.EDIT;
        Long oldId = cargoModeModel.getObject().getId();
        String resourceKey = "log.save_update";

        //update version of book and its localizable strings if necessary.
        updateVersionIfNecessary(cargoModeModel.getObject(), initial);
        cargoModeDAO.updateReferences(cargoModeModel.getObject());

        try {
            cargoModeDAO.saveOrUpdate(cargoModeModel.getObject(), null);
            Long newId = cargoModeModel.getObject().getId();
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, event, CargoModeEdit.class, CargoMode.class, message);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{oldId}).getObject();
            logBean.error(Log.MODULE.INFORMATION, event, CargoModeEdit.class, CargoMode.class, message);
        }
    }

    private void saveAsNew() {
        Long oldId = cargoModeModel.getObject().getId();
        String resourceKey = "log.save_as_new";
        try {
            cargoModeDAO.saveAsNew(cargoModeModel.getObject());
            Long newId = cargoModeModel.getObject().getId();
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{oldId, newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, CargoModeEdit.class, CargoMode.class, message);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            String message = new StringResourceModel(resourceKey, this, null, new Object[]{oldId, null}).getObject();
            logBean.error(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, CargoModeEdit.class, CargoMode.class, message);
        }
    }

    private void disableCargoMode(Long cargoModeId) {
        cargoModeDAO.disable(cargoModeId);
        String message = new StringResourceModel("log.enable_disable", this, null, new Object[]{cargoModeId}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, CargoModeEdit.class, CargoMode.class, message);
    }

    private void enableCargoMode(Long cargoModeId) {
        cargoModeDAO.enable(cargoModeId);
        String message = new StringResourceModel("log.enable_disable", this, null, new Object[]{cargoModeId}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.ENABLE, CargoModeEdit.class, CargoMode.class, message);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableCargoMode(cargoModeModel.getObject().getId());
                goToListPage();
            }

            @Override
            protected void onBeforeRender() {
                if (isNewBook(cargoModeModel.getObject()) || !CanEditUtil.canEdit(cargoModeModel.getObject())) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        toolbarButtons.add(new EnableItemButton(id) {

            @Override
            protected void onClick() {
                enableCargoMode(cargoModeModel.getObject().getId());
                goToListPage();
            }

            @Override
            protected void onBeforeRender() {
                if (isNewBook(cargoModeModel.getObject()) || !CanEditUtil.canEditDisabled(cargoModeModel.getObject())) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });

        return toolbarButtons;
    }

    private static boolean isRootCargoMode(CargoMode cargoMode) {
        return cargoMode.getParent() == null;
    }

    private void goToListPage() {
        BookWebInfo cargoModeWebInfo = BookTypeWebInfoUtil.getInfo(CargoMode.class);
        setResponsePage(cargoModeWebInfo.getListPage(), cargoModeWebInfo.getListPageParameters());
    }
}

