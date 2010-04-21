package org.vetcontrol.document.web.pages;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.document.service.DocumentCargoBean;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.UKTZEDField;
import org.vetcontrol.web.component.list.AjaxRemovableListView;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.vetcontrol.entity.Log.EVENT.CREATE;
import static org.vetcontrol.entity.Log.EVENT.EDIT;
import static org.vetcontrol.entity.Log.MODULE.DOCUMENT;
import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 15:44:20
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_EDIT, DOCUMENT_DEP_CHILD_EDIT})
public class DocumentCargoEdit extends DocumentEditPage {

    private static final Logger log = LoggerFactory.getLogger(DocumentCargoEdit.class);
    @EJB(name = "DocumentCargoBean")
    private DocumentCargoBean documentCargoBean;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "CargoTypeBean")
    private CargoTypeBean cargoTypeBean;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "LogBean")
    private LogBean logBean;
    @EJB(name = "ClientBean")
    ClientBean clientBean;

    public DocumentCargoEdit() {
        super();
        init(null);
    }

    public DocumentCargoEdit(final PageParameters parameters) {
        super();

        init(documentCargoBean.getDocumentCargoId(
                parameters.getAsLong("document_cargo_id"),
                parameters.getAsLong("client_id"),
                parameters.getAsLong("department_id")));
    }

    private void init(final ClientEntityId id) {

        //Модель данных
        DocumentCargo dc;
        try {
            dc = (id != null) ? documentCargoBean.loadDocumentCargo(id) : new DocumentCargo();
        } catch (Exception e) {
            log.error("Карточка на груз по id = " + id + " не найдена", e);
            getSession().error("Карточка на груз № " + id + " не найдена");
            logBean.error(DOCUMENT, EDIT, DocumentCargoEdit.class, DocumentCargo.class,
                    "Карточка не найдена. ID: " + id);

            setResponsePage(DocumentCargoList.class);
            return;
        }

        //Проверка доступа к данным
        User currentUser = userProfileBean.getCurrentUser();

        //Установка подразделения по умолчанию
        if (dc.getId() == null) {
            dc.setDepartment(currentUser.getDepartment());
        }

        if (id == null && !hasAnyRole(DOCUMENT_CREATE)) {
            log.error("Пользователю запрещен доступ на создание карточки на груз: " + currentUser.toString());
            logBean.error(DOCUMENT, CREATE, DocumentCargoEdit.class, DocumentCargo.class, "Доступ запрещен");
            throw new UnauthorizedInstantiationException(DocumentCargoEdit.class);
        }

        if (id != null) {
            boolean authorized = hasAnyRole(DOCUMENT_EDIT)
                    && currentUser.getId().equals(dc.getCreator().getId());

            if (!authorized && hasAnyRole(DOCUMENT_DEP_EDIT)) {
                authorized = currentUser.getDepartment().getId().equals(dc.getCreator().getDepartment().getId());
            }

            if (!authorized && hasAnyRole(DOCUMENT_DEP_CHILD_EDIT)) {
                for (Department d = dc.getCreator().getDepartment(); d != null; d = d.getParent()) {
                    if (d.getId().equals(currentUser.getDepartment().getId())) {
                        authorized = true;
                        break;
                    }
                }
            }

            if (!authorized || (!clientBean.isServer() && dc.getSyncStatus().equals(Synchronized.SyncStatus.SYNCHRONIZED))) {
                log.error("Пользователю запрещен доступ редактирование карточки на груз id = " + id + ": "
                        + currentUser.toString());
                logBean.error(DOCUMENT, EDIT, DocumentCargoEdit.class, DocumentCargo.class,
                        "Доступ запрещен. ID: " + id);
                throw new UnauthorizedInstantiationException(DocumentCargoEdit.class);
            }
        }

        //Заголовок
        IModel title = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return (id != null)
                        ? getString("document.cargo.edit.title.edit") + " " + id
                        : getString("document.cargo.edit.title.create");
            }
        };
        add(new Label("title", title));
        add(new Label("header", title));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final Model<DocumentCargo> documentCargoModel = new Model<DocumentCargo>(dc);

        //Форма
        final Form form = new Form<DocumentCargo>("doc_cargo_edit_form", documentCargoModel) {

            @Override
            protected void onValidate() {
                DocumentCargo dc = getModelObject();

                if (dc.getVehicleType() != null && !dc.getVehicleType().isCompound() && dc.getVehicles().size() > 1) {
                    error(getString("document.cargo.vehicle.add.error"));
                }

                //Вид груза уникален для всех грузов
                CargoMode cargoMode = getCargoMode(dc);

                if (cargoMode == null){
                    error(getString("document.cargo.edit.message.cargo_type.null_cargo_mode.error"));
                }                     

                for (Cargo c : dc.getCargos()) {
                    if (c.getCargoType() == null) {
                        error(getString("document.cargo.edit.message.cargo_type.not_found.error"));
                    }

                    if (cargoMode != null && !cargoMode.equals(cargoTypeBean.getCargoMode(c.getCargoType()))){
                        error(getString("document.cargo.edit.message.cargo_type.unique_cargo_mode.error") + ": " +
                                c.getCargoType().getCode() + " - " +
                                cargoMode.getDisplayName(getLocale(), localeDAO.systemLocale()));
                    }
                }
            }

            @Override
            protected void onSubmit() {
                try {
                    documentCargoBean.save(getModelObject());
                    setResponsePage(DocumentCargoList.class);
                    if (id == null) {
                        getSession().info(new StringResourceModel("document.cargo.edit.message.added", this, null,
                                new Object[]{getModelObject().getDisplayId()}).getString());
                    } else {
                        getSession().info(new StringResourceModel("document.cargo.edit.message.saved", this, null,
                                new Object[]{id}).getString());
                    }

                    logBean.info(DOCUMENT, id == null ? CREATE : EDIT, DocumentCargoEdit.class, DocumentCargo.class,
                            "ID: " + getModelObject().getDisplayId());
                } catch (Exception e) {
                    getSession().error(new StringResourceModel("document.cargo.edit.message.save.error", this, null,
                            new Object[]{id}).getString());

                    log.error("Ошибка сохранения карточки на груз № " + getModelObject().getDisplayId(), e);

                    logBean.error(DOCUMENT, id == null ? CREATE : EDIT, DocumentCargoEdit.class,
                            DocumentCargo.class, "Ошибка сохранения в базу данных");
                }
            }
        };
        add(form);

        Button cancel = new Button("cancel") {

            @Override
            public void onSubmit() {
                setResponsePage(DocumentCargoList.class);
            }
        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        //Тип движения груза
        addBookDropDownChoice(form, "document.cargo.movement_type", MovementType.class, documentCargoModel, "movementType");

        //Тип транспортного средства
        final DropDownChoice<VehicleType> ddcVehicleType = new DropDownChoice<VehicleType>("document.cargo.vehicle_type",
                new PropertyModel<VehicleType>(documentCargoModel, "vehicleType"),
                Arrays.asList(VehicleType.values()),
                new EnumChoiceRenderer<VehicleType>(this));
        ddcVehicleType.setRequired(true);
        ddcVehicleType.setOutputMarkupId(true);
        form.add(ddcVehicleType);

        ddcVehicleType.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                for (Vehicle vehicle : documentCargoModel.getObject().getVehicles()) {
                    vehicle.setVehicleType(ddcVehicleType.getModelObject());
                }
            }
        });

        //Отправитель
        addSender(form, "document.cargo.cargo_sender_country", "senderCountry",
                "document.cargo.cargo_sender_name", "senderName",
                documentCargoModel);

        //Получатель
        addReceiver(form, "document.cargo.cargo_receiver_name", "receiverName",
                "document.cargo.cargo_receiver_address", "receiverAddress",
                documentCargoModel);

        boolean visible = id != null;

        //Примечания
        TextArea details = new TextArea<String>("document.cargo.details",
                new PropertyModel<String>(documentCargoModel, "details"));
        form.add(details);

        //Автор
        Label l_creator = new Label("l_creator", new ResourceModel("document.cargo.creator"));
        l_creator.setVisible(visible);
        form.add(l_creator);

        String fullCreator = "";
        if (visible) {
            fullCreator = dc.getCreator().getFullName();
            if (dc.getCreator().getJob() != null) {
                fullCreator += ", " + dc.getCreator().getJob().getDisplayName(getLocale(), localeDAO.systemLocale());
            }
            if (dc.getDepartment() != null && !dc.getDepartment().getId().equals(dc.getCreator().getDepartment().getId())) {
                fullCreator += ", " + dc.getCreator().getDepartment().getDisplayName(getLocale(), localeDAO.systemLocale());
            }
        }

        Label creator = new Label("creator", fullCreator);
        creator.setVisible(visible);
        form.add(creator);

        //Подразделение и Пункт пропуска через границу
        addDepartmentAndPoint(form, "document.cargo.department", "department", "document.cargo.department.label",
                "document.cargo.passingBorderPoint", "passingBorderPoint", "document.cargo.passingBorderPoint.label",
                documentCargoModel, currentUser, visible);

        //Дата создания
        Label l_created = new Label("l_created", new ResourceModel("document.cargo.created"));
        l_created.setVisible(visible);
        form.add(l_created);

        DateLabel created = new DateLabel("created", new Model<Date>(
                visible ? documentCargoModel.getObject().getCreated() : new Date()),
                new StyleDateConverter(true));
        created.setVisible(visible);
        form.add(created);

        //Блок транспортных средств
        final WebMarkupContainer vehicleContainer = new WebMarkupContainer("document.cargo.vehicle_container");
        vehicleContainer.setOutputMarkupId(true);
        form.add(vehicleContainer);

        //Блок грузов
        final WebMarkupContainer cargoContainer = new WebMarkupContainer("document.cargo.cargo_container");
        cargoContainer.setOutputMarkupId(true);
        form.add(cargoContainer);

        //Вид груза
        final Label cargoModeLabel = new Label("document.cargo.cargo_mode", new LoadableDetachableModel<String>(){
            @Override
            protected String load() {
                String s = "";

                CargoMode cargoMode = getCargoMode(documentCargoModel.getObject());

                if (cargoMode != null){
                    s = getString("document.cargo.cargo_mode") + ": " +
                            cargoMode.getDisplayName(getLocale(), localeDAO.systemLocale());
                }else if(!documentCargoModel.getObject().getCargos().isEmpty()
                        && documentCargoModel.getObject().getCargos().get(0).getCargoType() != null){
                    s= getString("document.cargo.cargo_mode") + ": " +
                            getString("document.cargo.edit.message.cargo_type.null_cargo_mode.error");
                }

                return s;
            }
        });
        cargoModeLabel.setOutputMarkupId(true);
        cargoContainer.add(cargoModeLabel);

        //Добавить транспортное средство
        final AjaxSubmitLink addVehicleLink = new AjaxSubmitLink("document.cargo.vehicle.add", form) {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                DocumentCargo dc = (DocumentCargo) form.getModelObject();

                if (dc.getVehicleType() != null && !dc.getVehicleType().isCompound() && dc.getVehicles().size() > 0) {
                    target.addComponent(feedbackPanel);
                    getSession().error(getString("document.cargo.vehicle.add.error"));
                    return;
                }

                Vehicle vehicle = new Vehicle();
                vehicle.setDocumentCargo(dc);
                vehicle.setVehicleType(dc.getVehicleType());
                dc.getVehicles().add(vehicle);

                target.addComponent(vehicleContainer);
                target.addComponent(cargoModeLabel);
                target.addComponent(cargoContainer);

                String setFocusOnNewVehicle = "newVehicleFirstInputId = $('#vehicles tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newVehicleFirstInputId);";
                target.appendJavascript(setFocusOnNewVehicle);
            }
        };
        addVehicleLink.setDefaultFormProcessing(false);
        form.add(addVehicleLink);

        //Список транспортных средств
        final ListView<Vehicle> vehicleListView = new AjaxRemovableListView<Vehicle>("document.cargo.vehicle_list",
                new PropertyModel<List<Vehicle>>(documentCargoModel, "vehicles")) {

            @Override
            protected void populateItem(final ListItem<Vehicle> item) {
                final Vehicle vehicle = item.getModelObject();

                TextField details = new TextField<String>("document.cargo.vehicle.details",
                        new PropertyModel<String>(vehicle, "vehicleDetails"));
                details.setOutputMarkupId(true);
                item.add(details);

                details.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        if (!documentCargoBean.validate(vehicle)) {
                            if (VehicleType.CONTAINER.equals(vehicle.getVehicleType())) {
                                vehicle.setName(getString("document.cargo.vehicle.validate.container.error"));
                            }
                        }
                        target.addComponent(vehicleContainer);
                        target.addComponent(cargoContainer);
                    }
                });

                Label name = new Label("document.cargo.vehicle.name", new PropertyModel<String>(vehicle, "name"));
                name.setOutputMarkupId(true);
                item.add(name);

                addRemoveSubmitLink("document.cargo.vehicle.delete", form, item, addVehicleLink,
                        vehicleContainer, cargoContainer, feedbackPanel, cargoModeLabel);
            }

            @Override
            protected boolean validate(ListItem<Vehicle> item) {
                Vehicle vehicle = item.getModelObject();

                for (Cargo cargo : documentCargoModel.getObject().getCargos()) {
                    if (vehicle.equals(cargo.getVehicle())) {
                        getSession().error(new StringResourceModel("document.cargo.vehicle.delete.error", this, null,
                                new Object[]{vehicle.getVehicleDetails()}).getString());
                        return false;
                    }
                }

                return true;
            }
        };
        vehicleContainer.add(vehicleListView);

        //Добавить груз
        final AjaxSubmitLink addCargoLink = new AjaxSubmitLink("document.cargo.cargo.add", form) {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                DocumentCargo dc = (DocumentCargo) form.getModelObject();

                Cargo cargo = new Cargo();
                cargo.setDocumentCargo(dc);
                dc.getCargos().add(cargo);

                if (dc.getVehicleType() != null && !dc.getVehicleType().isCompound() && !dc.getVehicles().isEmpty()) {
                    cargo.setVehicle(dc.getVehicles().get(0));
                }

                target.addComponent(cargoContainer);
                target.addComponent(vehicleContainer);

                String setFocusOnNewCargo = "newCargoFirstInputId = $('#cargos tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newCargoFirstInputId);";
                target.appendJavascript(setFocusOnNewCargo);
            }
        };
        addCargoLink.setDefaultFormProcessing(false);
        form.add(addCargoLink);

        //Список грузов
        final ListView cargoListView = new AjaxRemovableListView<Cargo>("document.cargo.cargo_list",
                new PropertyModel<List<Cargo>>(documentCargoModel, "cargos")) {

            @Override
            protected void populateItem(final ListItem<Cargo> item) {
                addCargo(item, getCargoMode(documentCargoModel.getObject()));

                //Копировать
                final AjaxSubmitLink copyCargoLink = new AjaxSubmitLink("document.cargo.copy", form) {

                    @Override
                    public void onSubmit(AjaxRequestTarget target, Form form) {
                        DocumentCargo dc = (DocumentCargo) form.getModelObject();

                        //update item model
                        for (Iterator<? extends Component> it = item.iterator(); it.hasNext();){
                            Component component = it.next();

                            if (component instanceof FormComponent){
                                FormComponent formComponent = (FormComponent)component;
                                try {
                                    formComponent.inputChanged();
                                    formComponent.validate();

                                    if (formComponent.hasErrorMessage()){
                                        formComponent.invalid();
                                    }else {
                                        formComponent.valid();
                                        formComponent.updateModel();
                                    }
                                } catch (RuntimeException e){
                                    // nothing
                                }
                            }
                        }

                        Cargo copyFrom = item.getModelObject();

                        Cargo cargo = new Cargo();
                        cargo.setCargoType(copyFrom.getCargoType());
                        cargo.setUnitType(copyFrom.getUnitType());
                        cargo.setCount(copyFrom.getCount());
                        cargo.setCertificateDetails(copyFrom.getCertificateDetails());
                        cargo.setCertificateDate(copyFrom.getCertificateDate());
                        cargo.setCargoProducer(copyFrom.getCargoProducer());
                        cargo.setVehicle(copyFrom.getVehicle());

                        cargo.setDocumentCargo(dc);
                        dc.getCargos().add(cargo);
                        target.addComponent(cargoContainer);


                        String setFocusOnNewCargo = "newCargoFirstInputId = $('.table_input tbody tr:last input[type=\"text\"]:first').attr('id');"
                                + "Wicket.Focus.setFocusOnId(newCargoFirstInputId);";
                        target.appendJavascript(setFocusOnNewCargo);
                    }
                };
                copyCargoLink.setDefaultFormProcessing(false);
                item.add(copyCargoLink);

                //Задержать груз
                PageParameters pageParameters = null;


                if (item.getModelObject().getId() != null){
                    pageParameters = new PageParameters("cargo_id=" + item.getModelObject().getId() + ","
                        + "client_id=" + item.getModelObject().getClient().getId() + ","
                        + "department_id=" + item.getModelObject().getDepartment().getId());
                }

                BookmarkablePageLink arrestLink = new BookmarkablePageLink<DocumentCargo>("document.cargo.arrest", ArrestDocumentEdit.class, pageParameters);
                arrestLink.setVisible(item.getModelObject().getId() != null);

                item.add(arrestLink);

                addRemoveSubmitLink("document.cargo.delete", form, item, addCargoLink, cargoContainer);
            }
        };
        cargoContainer.add(cargoListView);

//        form.add(new Spacer("spacer"));
    }



    private class UnitTypeModel extends LoadableDetachableModel<List<UnitType>> {

        private Cargo cargo;

        private UnitTypeModel(Cargo cargo) {
            this.cargo = cargo;
        }

        @Override
        protected List<UnitType> load() {
            return cargoTypeBean.getUnitTypes(cargo.getCargoType());
        }
    }

    private class CargoProducerModel extends LoadableDetachableModel<List<CargoProducer>> {

        private CountryBook country;

        private CargoProducerModel(CountryBook country) {
            this.country = country;
        }

        public void setCountry(CountryBook country) {
            this.country = country;
        }

        @Override
        protected List<CargoProducer> load() {
            return documentCargoBean.getCargoProducer(country);
        }
    }

    private void addCargo(ListItem<Cargo> item, CargoMode cargoMode) {
        //Единицы измерения        
        UnitTypeModel unitTypeModel = new UnitTypeModel(item.getModelObject());

        DropDownChoice<UnitType> ddcUnitTypes =  addBookDropDownChoice(item, "document.cargo.unit_type",
                item.getModel(), "unitType", unitTypeModel, false, true);

        //УКТЗЕД и Тип груза
        item.add(new UKTZEDField("document.cargo.cargo_type", new PropertyModel<CargoType>(item.getModel(), "cargoType"),
                cargoMode, ddcUnitTypes));

        //Количество
        TextField<Double> count = new TextField<Double>("document.cargo.count",
                new PropertyModel<Double>(item.getModel(), "count"));
        item.add(count);

        //Транспортное средство
        final DropDownChoice<Vehicle> ddcVehicle = new DropDownChoice<Vehicle>("document.cargo.vehicle",
                new PropertyModel<Vehicle>(item.getModel(), "vehicle"),
                item.getModelObject().getDocumentCargo().getVehicles(),
                new IChoiceRenderer<Vehicle>() {

                    @Override
                    public Object getDisplayValue(Vehicle object) {
                        return object.getVehicleDetails();
                    }

                    @Override
                    public String getIdValue(Vehicle object, int index) {
                        return index + ":" + object.getId();
                    }
                });
        item.add(ddcVehicle);

        ddcVehicle.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //update model
            }
        });

        //Производитель Страна
        CountryBook country = item.getModelObject().getCargoProducer() != null
                ? item.getModelObject().getCargoProducer().getCountry()
                : item.getModelObject().getDocumentCargo().getSenderCountry();

        final DropDownChoice<CountryBook> ddcCountryBook = addBookDropDownChoice(item, "document.cargo.producer_country",
                CountryBook.class, new Model<CountryBook>(country), true, false);

        //Производитель Название
        final CargoProducerModel cargoProducerModel = new CargoProducerModel(country);

        final DropDownChoice<CargoProducer> ddcCargoProducer = addBookDropDownChoice(item, "document.cargo.producer_name",
                item.getModel(), "cargoProducer", cargoProducerModel, true, true);

        ddcCountryBook.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                cargoProducerModel.setCountry(ddcCountryBook.getModelObject());
                target.addComponent(ddcCargoProducer);
            }
        });

        //Реквизиты сертификата
        TextField<String> certificateDetails = new TextField<String>("document.cargo.certificate_detail",
                new PropertyModel<String>(item.getModel(), "certificateDetails"));
        certificateDetails.setRequired(true);
        item.add(certificateDetails);

        //Дата сертификата
        DatePicker<Date> certificateDate = new DatePicker<Date>("document.cargo.certificate_date",
                new PropertyModel<Date>(item.getModel(), "certificateDate"));
        certificateDate.setRequired(true);
        item.add(certificateDate);
    }
}
