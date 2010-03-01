/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages.custom.cargomode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
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
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.information.web.component.edit.AbstractAutoCompleteTextField;
import org.vetcontrol.information.web.component.edit.LocalizableTextPanel;
import org.vetcontrol.information.web.component.edit.SaveUpdateConfirmationDialog;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.BookHash;
import org.vetcontrol.web.component.toolbar.DisableItemButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
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
        private DropDownChoice<UnitType> unitTypeSelect;

        public UnitTypesChoiceContainer(String id, final CargoMode cargoMode,
                final CargoModeUnitType cargoModeUnitType, final IModel<UnitType> model, final Locale systemLocale) {
            super(id);

            IChoiceRenderer<UnitType> renderer = new IChoiceRenderer<UnitType>() {

                @Override
                public Object getDisplayValue(UnitType object) {
                    return object.getDisplayName(getLocale(), systemLocale);
                }

                @Override
                public String getIdValue(UnitType object, int index) {
                    return String.valueOf(object.getId());
                }
            };
            unitTypeSelect = new DropDownChoice<UnitType>("unitTypeSelect",
                    new IModel<UnitType>() {

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
                    }, new UnitTypesModel(cargoMode, cargoModeUnitType), renderer);
            unitTypeSelect.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.addComponent(unitTypeSelect);
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
                        return cargoModeDAO.getAvailableCargoTypes(searchTextInput, Constants.AUTO_COMPLETE_TEXT_FIELD_MAX_ITEMS,
                                cargoMode.getId(),
                                exclude);
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

            autoCompleteTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.addComponent(autoCompleteTextField);
                }
            });
//            autoCompleteTextField.setRequired(true);
            autoCompleteTextField.setOutputMarkupId(true);
            add(autoCompleteTextField);
        }

        private String getText(CargoType cargoType, Locale systemLocale) {
            return cargoType.getCode() + "   " + cargoType.getDisplayName(getLocale(), systemLocale);
        }
    }
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "BookDAO")
    IBookDAO bookDAO;
    @EJB(name = "CargoModeDAO")
    private CargoModeDAO cargoModeDAO;
    @EJB(name = "LogBean")
    private LogBean logBean;
    private static final String UNIT_TYPE_INCORRECT = "unit_type_incorrect";
    private static final String CARGO_TYPE_INCORRECT = "cargo_type_incorrect";
    private static final Logger log = LoggerFactory.getLogger(CargoModeEdit.class);
    private Model<CargoMode> cargoModeModel;

    public CargoModeEdit() {
        init();
    }

    private void init() {
        final Locale systemLocale = localeDAO.systemLocale();

        final CargoMode cargoMode = getSession().getMetaData(CargoModeList.SELECTED_BOOK_ENTRY);
        if (cargoMode == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }
        bookDAO.addLocalizationSupport(cargoMode);
        BeanPropertyUtil.addLocalization(cargoMode, localeDAO.all());

        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = BeanPropertyUtil.hash(cargoMode);

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
                BeanPropertyUtil.getPropertyByName(CargoMode.class, "names"),
                systemLocale));

        //list of associated cargo types.
        final WebMarkupContainer cargoTypesContainer = new WebMarkupContainer("cargoTypesContainer");
        cargoTypesContainer.setOutputMarkupId(true);
        form.add(cargoTypesContainer);

        final AjaxSubmitLink addCargoType = new AjaxSubmitLink("addCargoType", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                CargoModeCargoType cargoModeCargoType = new CargoModeCargoType();
                cargoModeModel.getObject().addCargoModeCargoType(cargoModeCargoType);
                target.addComponent(cargoTypesContainer);

                String setFocusOnNewElement = "newElementFirstInputId = $('.cargo_types tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newElementFirstInputId);";
                target.appendJavascript(setFocusOnNewElement);
            }
        };
        addCargoType.setDefaultFormProcessing(false);
        cargoTypesContainer.add(addCargoType);

        final ListView<CargoModeCargoType> cargoTypes = new ListView<CargoModeCargoType>("cargoTypes",
                cargoModeModel.getObject().getCargoModeCargoTypes()) {

            @Override
            protected IModel<CargoModeCargoType> getListItemModel(IModel<? extends List<CargoModeCargoType>> listViewModel, int index) {
                return new Model<CargoModeCargoType>(listViewModel.getObject().get(index));
            }

            @Override
            protected void populateItem(ListItem<CargoModeCargoType> item) {
                UKTZEDPanel uktzed = new UKTZEDPanel("cargoType", cargoMode, item.getModelObject(), systemLocale);
                item.add(uktzed);

                AjaxSubmitLink deleteCargoType = new AjaxSubmitLink("deleteCargoType", form) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form form) {
                        ListItem<CargoModeCargoType> item = (ListItem) getParent();
                        ListView<CargoModeCargoType> list = (ListView<CargoModeCargoType>) item.getParent();

                        int removeIndex = item.getIndex();
                        int last_index = list.getModelObject().size() - 1;

                        //Copy childs from next list item and remove last
                        for (int index = item.getIndex(); index < last_index; index++) {
                            ListItem<CargoModeCargoType> li = (ListItem) item.getParent().get(index);
                            ListItem<CargoModeCargoType> li_next = (ListItem) item.getParent().get(index + 1);

                            li.removeAll();
                            if (li.getIndex() == removeIndex) {
                                for (int i = 0; i < li.size(); i++) {
                                    li.get(i).remove();
                                }
                            }
                            li.setModelObject(li_next.getModelObject());

                            int size = li_next.size();
                            Component[] childs = new Component[size];
                            for (int i = 0; i < size; i++) {
                                childs[i] = li_next.get(i);
                            }
                            li.add(childs);
                        }
                        item.getParent().get(last_index).remove();

                        list.getModelObject().remove(removeIndex);

                        target.addComponent(cargoTypesContainer);
                        target.focusComponent(addCargoType);
                    }
                };
                deleteCargoType.setDefaultFormProcessing(false);
                item.add(deleteCargoType);
            }
        };
        cargoTypes.setReuseItems(true);
        cargoTypesContainer.add(cargoTypes);

        //list of associated unit types.
        final WebMarkupContainer unitTypesContainer = new WebMarkupContainer("unitTypesContainer");
        unitTypesContainer.setOutputMarkupId(true);
        form.add(unitTypesContainer);

        final AjaxSubmitLink addUnitType = new AjaxSubmitLink("addUnitType", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                CargoModeUnitType cargoModeUnitType = new CargoModeUnitType();
                cargoModeModel.getObject().addCargoModeUnitType(cargoModeUnitType);
                target.addComponent(unitTypesContainer);

                String setFocusOnNewElement = "newElementFirstInputId = $('.unit_types tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newElementFirstInputId);";
                target.appendJavascript(setFocusOnNewElement);
            }
        };
        addUnitType.setDefaultFormProcessing(false);
        unitTypesContainer.add(addUnitType);

        final ListView<CargoModeUnitType> unitTypes = new ListView<CargoModeUnitType>("unitTypes",
                cargoModeModel.getObject().getCargoModeUnitTypes()) {

            @Override
            protected IModel<CargoModeUnitType> getListItemModel(IModel<? extends List<CargoModeUnitType>> listViewModel, int index) {
                return new Model<CargoModeUnitType>(listViewModel.getObject().get(index));
            }

            @Override
            protected void populateItem(ListItem<CargoModeUnitType> item) {
                UnitTypesChoiceContainer unitTypesChoiceContainer = new UnitTypesChoiceContainer("unitType", cargoModeModel.getObject(),
                        item.getModelObject(), new PropertyModel<UnitType>(item.getModelObject(), "unitType"), systemLocale);
                item.add(unitTypesChoiceContainer);

                AjaxSubmitLink deleteUnitType = new AjaxSubmitLink("deleteUnitType", form) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form form) {
                        ListItem<CargoModeUnitType> item = (ListItem) getParent();
                        ListView<CargoModeUnitType> list = (ListView<CargoModeUnitType>) item.getParent();

                        int removeIndex = item.getIndex();
                        int last_index = list.getModelObject().size() - 1;

                        //Copy childs from next list item and remove last
                        for (int index = item.getIndex(); index < last_index; index++) {
                            ListItem<CargoModeUnitType> li = (ListItem) item.getParent().get(index);
                            ListItem<CargoModeUnitType> li_next = (ListItem) item.getParent().get(index + 1);

                            li.removeAll();
                            if (li.getIndex() == removeIndex) {
                                for (int i = 0; i < li.size(); i++) {
                                    li.get(i).remove();
                                }
                            }
                            li.setModelObject(li_next.getModelObject());

                            int size = li_next.size();
                            Component[] childs = new Component[size];
                            for (int i = 0; i < size; i++) {
                                childs[i] = li_next.get(i);
                            }
                            li.add(childs);
                        }
                        item.getParent().get(last_index).remove();

                        list.getModelObject().remove(removeIndex);

                        target.addComponent(unitTypesContainer);
                        target.focusComponent(addUnitType);
                    }
                };
                deleteUnitType.setDefaultFormProcessing(false);
                item.add(deleteUnitType);
            }
        };
        unitTypes.setReuseItems(true);
        unitTypesContainer.add(unitTypes);

        final SaveUpdateConfirmationDialog confirmationDialog = new SaveUpdateConfirmationDialog("confirmationDialogPanel") {

            @Override
            public void update() {
                saveOrUpdate(cargoModeModel, initial);
                setResponsePage(CargoModeList.class);
            }

            @Override
            public void createNew() {
                //disable old entry.
                disableCargoMode(cargoModeModel.getObject());

                //save new entry.
                BeanPropertyUtil.clearBook(cargoModeModel.getObject());
                for (CargoModeCargoType cmct : cargoModeModel.getObject().getCargoModeCargoTypes()) {
                    cmct.setNeedToUpdateVersion(true);
                }
                for (CargoModeUnitType cmut : cargoModeModel.getObject().getCargoModeUnitTypes()) {
                    cmut.setNeedToUpdateVersion(true);
                }
                saveOrUpdate(cargoModeModel, initial);
                setResponsePage(CargoModeList.class);
            }
        };
        add(confirmationDialog);
        //save and cancel links.
        form.add(
                new AjaxSubmitLink("save") {

                    @Override
                    public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        if (validate()) {
                            if (cargoModeModel.getObject().getId() == null) {
                                //new entry
                                saveOrUpdate(cargoModeModel, initial);
                                setResponsePage(CargoModeList.class);
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
                        target.addComponent(messages);
                    }

                    private boolean validate() {
                        boolean validated = true;
                        for (CargoModeCargoType cargoModeCargoType : cargoModeModel.getObject().getCargoModeCargoTypes()) {
                            CargoType cargoType = cargoModeCargoType.getCargoType();
                            if (cargoType == null) {
                                validated = false;
                                error(getString(CARGO_TYPE_INCORRECT));
                            }
                        }
                        for (CargoModeUnitType cargoModeUnitType : cargoModeModel.getObject().getCargoModeUnitTypes()) {
                            UnitType unitType = cargoModeUnitType.getUnitType();
                            if (unitType == null) {
                                validated = false;
                                error(getString(UNIT_TYPE_INCORRECT));
                            }
                        }
                        return validated;
                    }
                });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(CargoModeList.class);
            }
        });
    }

    private void saveOrUpdate(IModel<CargoMode> cargoModeModel, BookHash initial) {
        Long id = cargoModeModel.getObject().getId();
        Log.EVENT event = id == null ? Log.EVENT.CREATE : Log.EVENT.EDIT;

        //update version of book and its localizable strings if necessary.
        BeanPropertyUtil.updateVersionIfNecessary(cargoModeModel.getObject(), initial);
        updateCargoModeReferences(cargoModeModel);

        try {
            cargoModeDAO.saveOrUpdate(cargoModeModel.getObject());
            logBean.info(Log.MODULE.INFORMATION, event, CargoModeEdit.class, CargoMode.class, "ID: " + id);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            logBean.error(Log.MODULE.INFORMATION, event, CargoModeEdit.class, CargoMode.class, "ID: " + id);
        }
    }

    private void disableCargoMode(CargoMode cargoMode) {
        bookDAO.disable(cargoMode);
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, CargoModeEdit.class, CargoMode.class, "ID: " + cargoMode.getId());
    }

    private void updateCargoModeReferences(IModel<CargoMode> cargoModeModel) {
        Date newVersion = DateUtil.getCurrentDate();
        for (CargoModeCargoType cmct : cargoModeModel.getObject().getCargoModeCargoTypes()) {
            if (cmct.isNeedToUpdateVersion()) {
                cmct.setUpdated(newVersion);
            }
        }
        for (CargoModeUnitType cmut : cargoModeModel.getObject().getCargoModeUnitTypes()) {
            if (cmut.isNeedToUpdateVersion()) {
                cmut.setUpdated(newVersion);
            }
        }
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableCargoMode(cargoModeModel.getObject());
                setResponsePage(CargoModeList.class);
            }

            @Override
            protected void onBeforeRender() {
                if (cargoModeModel.getObject().getId() == null) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }
}

