package org.vetcontrol.document.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.document.service.ArrestDocumentBean;
import org.vetcontrol.document.service.CommonDocumentBean;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.UKTZEDField;

import javax.ejb.EJB;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.web.components.PrintButton;
import org.vetcontrol.report.document.jasper.arrest.ArrestDocumentReportServlet;
import org.vetcontrol.service.dao.IBookViewDAO;

import static org.vetcontrol.entity.Log.EVENT.CREATE;
import static org.vetcontrol.entity.Log.EVENT.EDIT;
import static org.vetcontrol.entity.Log.MODULE.DOCUMENT;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.04.2010 19:46:33
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_EDIT, DOCUMENT_DEP_CHILD_EDIT})
public class ArrestDocumentEdit extends DocumentEditPage {

    private static final Logger log = LoggerFactory.getLogger(DocumentCargoEdit.class);
    @EJB(name = "DocumentBean")
    private CommonDocumentBean commonDocumentBean;
    @EJB(name = "LogBean")
    private LogBean logBean;
    @EJB(name = "CargoTypeBean")
    private CargoTypeBean cargoTypeBean;
    @EJB(name = "ArrestDocumentBean")
    private ArrestDocumentBean arrestDocumentBean;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "ClientBean")
    private ClientBean clientBean;
    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;
    //Объект данных
    private ArrestDocument ad;

    public ArrestDocumentEdit() {
        super();
        init(null, null);
    }

    public ArrestDocumentEdit(final PageParameters parameters) {
        super();

        Long cargoId = parameters.getAsLong("cargo_id");
        Long arrestDocumentId = parameters.getAsLong("arrest_document_id");

        if (cargoId != null) {
            init(null, arrestDocumentBean.getClientEntityId(
                    cargoId,
                    parameters.getAsLong("client_id"),
                    parameters.getAsLong("department_id")));
        } else if (arrestDocumentId != null) {
            init(arrestDocumentBean.getClientEntityId(
                    arrestDocumentId,
                    parameters.getAsLong("client_id"),
                    parameters.getAsLong("department_id")), null);
        } else {
            init(null, null);
        }
    }

    private void init(final ClientEntityId arrestDocumentId, final ClientEntityId cargoId) {
        //редактирование
        if (arrestDocumentId != null) {
            try {
                ad = arrestDocumentBean.loadArrestDocument(arrestDocumentId);

                //report related stuffs
                loadDependencies(ad);
                storeArrestDocument(ad);
            } catch (Exception e) {
                log.error("Акт задержания груза по id = " + arrestDocumentId + " не найден", e);
                getSession().error("Акт задержания груза № " + arrestDocumentId + " не найден");
                logBean.error(DOCUMENT, EDIT, ArrestDocumentEdit.class, ArrestDocument.class,
                        "Акт задержания груза не найден. ID: " + arrestDocumentId);

                setResponsePage(DocumentCargoList.class);
                return;
            }
        }

        final IModel<Cargo> cargoModel = new Model<Cargo>();

        //создание из карточки на груз
        if (cargoId != null) {
            Cargo cargo = arrestDocumentBean.loadCargo(cargoId);
            cargoModel.setObject(cargo);

            ad = new ArrestDocument();

            ad.setClient(cargo.getClient());
            ad.setDepartment(cargo.getDepartment());
            ad.setCreator(cargo.getDocumentCargo().getCreator());
            ad.setArrestDate(DateUtil.getCurrentDate());
            ad.setCargoType(cargo.getCargoType());
            ad.setUnitType(cargo.getUnitType());
            ad.setCount(cargo.getCount());
            ad.setVehicleType(cargo.getDocumentCargo().getVehicleType());
            ad.setVehicleDetails(cargo.getVehicle() != null ? cargo.getVehicle().getVehicleDetails() : null);
            ad.setSenderCountry(cargo.getDocumentCargo().getSenderCountry());
            ad.setSenderName(cargo.getDocumentCargo().getSenderName());
            ad.setReceiverAddress(cargo.getDocumentCargo().getReceiverAddress());
            ad.setReceiverName(cargo.getDocumentCargo().getReceiverName());
            ad.setPassingBorderPoint(cargo.getDocumentCargo().getPassingBorderPoint());
            ad.setCargoMode(cargo.getDocumentCargo().getCargoMode());
            ad.setDocumentCargoCreated(cargo.getDocumentCargo().getCreated());
            ad.setCertificateDetails(cargo.getCertificateDetails());
            ad.setCertificateDate(cargo.getCertificateDate());
        }

        //Текущий пользователь
        final User currentUser = userProfileBean.getCurrentUser();

        //новый акт
        if (arrestDocumentId == null && cargoId == null) {
            ad = new ArrestDocument();
            ad.setDepartment(currentUser.getDepartment());
            ad.setPassingBorderPoint(currentUser.getPassingBorderPoint());
            ad.setClient(clientBean.getCurrentClient());
            ad.setCreator(currentUser);
        }

        //Проверка доступа к данным
        if (arrestDocumentId == null && cargoId == null && !hasAnyRole(DOCUMENT_CREATE)) {
            log.error("Пользователю запрещен доступ на создание акта задержания груза: " + currentUser.toString());
            logBean.error(DOCUMENT, CREATE, ArrestDocumentEdit.class, ArrestDocument.class, "Доступ запрещен");
            throw new UnauthorizedInstantiationException(ArrestDocumentEdit.class);
        }

        if (arrestDocumentId != null) {
            boolean authorized = hasAnyRole(DOCUMENT_EDIT) && currentUser.getId().equals(ad.getCreator().getId());

            if (!authorized && hasAnyRole(DOCUMENT_DEP_EDIT)) {
                authorized = currentUser.getDepartment().getId().equals(ad.getCreator().getDepartment().getId());
            }

            if (!authorized && hasAnyRole(DOCUMENT_DEP_CHILD_EDIT)) {
                for (Department d = ad.getCreator().getDepartment(); d != null; d = d.getParent()) {
                    if (d.getId().equals(currentUser.getDepartment().getId())) {
                        authorized = true;
                        break;
                    }
                }
            }

            if (!authorized || (!clientBean.isServer() && ad.getSyncStatus().equals(Synchronized.SyncStatus.SYNCHRONIZED))) {
                log.error("Пользователю запрещен доступ редактирования акта задержания груза id = " + ad + ": "
                        + currentUser.toString());
                logBean.error(DOCUMENT, EDIT, ArrestDocumentEdit.class, ArrestDocument.class,
                        "Доступ запрещен. ID: " + arrestDocumentId);
                throw new UnauthorizedInstantiationException(ArrestDocumentEdit.class);
            }
        }

        //Заголовок
        IModel title = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return (arrestDocumentId != null)
                        ? getString("arrest.document.edit.title.edit") + " " + arrestDocumentId
                        : getString("arrest.document.edit.title.create");
            }
        };
        add(new Label("title", title));
        add(new Label("header", title));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        //Модель данных
        final Model<ArrestDocument> arrestDocumentModel = new Model<ArrestDocument>(ad);

        //Форма
        final Form form = new Form<ArrestDocument>("arrest_document_edit_form", arrestDocumentModel) {

            @Override
            protected void onValidate() {
                super.onValidate();
            }

            @Override
            protected void onSubmit() {
                try {
                    arrestDocumentBean.save(getModelObject(), cargoId);

                    if (cargoId != null) {
                        DocumentCargo dc = cargoModel.getObject().getDocumentCargo();

                        setResponsePage(DocumentCargoEdit.class,
                                new PageParameters("document_cargo_id=" + dc.getId() + ","
                                + "client_id=" + dc.getClient().getId() + ","
                                + "department_id=" + dc.getDepartment().getId()));
                    } else {
                        setResponsePage(ArrestDocumentList.class);
                    }

                    if (arrestDocumentId == null) {
                        getSession().info(new StringResourceModel("arrest.document.edit.message.added", this, null,
                                new Object[]{getModelObject().getDisplayId()}).getString());
                    } else {
                        getSession().info(new StringResourceModel("arrest.document.edit.message.saved", this, null,
                                new Object[]{arrestDocumentId}).getString());
                    }

                    logBean.info(DOCUMENT, arrestDocumentId == null ? CREATE : EDIT, ArrestDocumentEdit.class,
                            ArrestDocument.class, "ID: " + getModelObject().getDisplayId());
                } catch (Exception e) {
                    getSession().error(new StringResourceModel("arrest.document.edit.message.save.error", this, null,
                            new Object[]{arrestDocumentId}).getString());

                    log.error("Ошибка сохранения карточки на груз № " + getModelObject().getDisplayId(), e);

                    logBean.error(DOCUMENT, arrestDocumentId == null ? CREATE : EDIT, ArrestDocumentEdit.class,
                            ArrestDocument.class, "Ошибка сохранения в базу данных");
                }
            }
        };
        add(form);

        //Кнопка назад
        Button cancel = new Button("cancel") {

            @Override
            public void onSubmit() {
                if (cargoId != null) {
                    DocumentCargo dc = cargoModel.getObject().getDocumentCargo();

                    setResponsePage(DocumentCargoEdit.class,
                            new PageParameters("document_cargo_id=" + dc.getId() + ","
                            + "client_id=" + dc.getClient().getId() + ","
                            + "department_id=" + dc.getDepartment().getId()));
                } else {
                    setResponsePage(ArrestDocumentList.class);
                }
            }
        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        //Дата задержания
        DatePicker<Date> arrestDate = new DatePicker<Date>("arrest.document.arrest_date",
                new PropertyModel<Date>(arrestDocumentModel, "arrestDate"));
        arrestDate.setRequired(true);
        form.add(arrestDate);

        //Причина задержания
        addBookDropDownChoice(form, "arrest.document.arrest_reason", ArrestReason.class, arrestDocumentModel, "arrestReason");

        //Детали задержания
        TextArea<String> arrestReasonDetails = new TextArea<String>("arrest.document.arrest_reason_details",
                new PropertyModel<String>(arrestDocumentModel, "arrestReasonDetails"));
        arrestReasonDetails.setRequired(true);
        form.add(arrestReasonDetails);

        //Единицы измерения
        IModel<List<UnitType>> unitTypeModel = new LoadableDetachableModel<List<UnitType>>() {

            @Override
            protected List<UnitType> load() {
                CargoMode cargoMode = arrestDocumentModel.getObject().getCargoMode();

                if (cargoMode == null) {
                    return Collections.emptyList();
                }

                return new ArrayList<UnitType>(cargoMode.getUnitTypes());
            }
        };

        final DropDownChoice<UnitType> ddcUnitTypes = addBookDropDownChoice(form, "arrest.document.unit_type",
                arrestDocumentModel, "unitType", unitTypeModel, false, true);
        ddcUnitTypes.setOutputMarkupId(true);
        form.add(ddcUnitTypes);

        //Блок вид груза
        final WebMarkupContainer cargoModeContainer = new WebMarkupContainer("document.cargo.cargo_mode_container");
        cargoModeContainer.setOutputMarkupId(true);
        form.add(cargoModeContainer);

        //Тип груза
        final IModel<CargoType> cargoTypeModel = new PropertyModel<CargoType>(arrestDocumentModel, "cargoType");
        form.add(new UKTZEDField("arrest.document.cargo_type", cargoTypeModel, new Model<CargoMode>(), cargoModeContainer));

        final ListView cargoModeListView = new ListView<CargoMode>("document.cargo.cargo_mode_parent_list",
                new LoadableDetachableModel<List<CargoMode>>() {

                    @Override
                    protected List<CargoMode> load() {
                        List<CargoMode> list = new ArrayList<CargoMode>();
                        CargoType ct = cargoTypeModel.getObject();

                        if (ct != null && ct.getCargoModes() != null) {
                            for (CargoMode cm : ct.getCargoModes()) {
                                if (cm.getParent() != null && !list.contains(cm.getParent())) {
                                    list.add(cm.getParent());
                                }
                            }
                        }

                        return list;
                    }
                }) {

            @Override
            protected void populateItem(final ListItem<CargoMode> item) {
                item.add(new Label("document.cargo.cargo_mode_parent",
                        item.getModelObject().getDisplayName(getLocale(), getSystemLocale())));

                //список дочерних видов
                ListView childList = new ListView<CargoMode>("document.cargo.cargo_mode_child_list",
                        new LoadableDetachableModel<List<CargoMode>>() {

                            @Override
                            protected List<CargoMode> load() {
                                List<CargoMode> list = new ArrayList<CargoMode>();
                                CargoType ct = cargoTypeModel.getObject();

                                if (ct != null && ct.getCargoModes() != null) {
                                    for (CargoMode cm : ct.getCargoModes()) {
                                        if (cm.getParent() != null && cm.getParent().equals(item.getModelObject())) {
                                            list.add(cm);
                                        }
                                    }
                                }

                                return list;
                            }
                        }) {

                    @Override
                    protected void populateItem(final ListItem<CargoMode> childItem) {
                        childItem.add(new Radio<CargoMode>("document.cargo.cargo_mode_child_radio", childItem.getModel()));
                        childItem.add(new Label("document.cargo.cargo_mode_child_label",
                                childItem.getModelObject().getDisplayName(getLocale(), getSystemLocale())));
                    }
                };

                item.add(childList);
            }
        };
        cargoModeListView.setOutputMarkupId(true);

        RadioGroup radioGroup = new RadioGroup<CargoMode>("document.cargo.cargo_mode_parent_radio_group",
                new PropertyModel<CargoMode>(arrestDocumentModel, "cargoMode"));
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(cargoModeContainer);
                target.addComponent(ddcUnitTypes);
            }
        });
        radioGroup.setRequired(true);
        radioGroup.setOutputMarkupId(true);
        radioGroup.add(cargoModeListView);

        cargoModeContainer.add(radioGroup);

        //Количество
        TextField<Double> count = new TextField<Double>("arrest.document.count",
                new PropertyModel<Double>(arrestDocumentModel, "count"));
        form.add(count);

        //Тип транспортного средства
        final DropDownChoice<VehicleType> ddcVehicleType = new DropDownChoice<VehicleType>("arrest.document.vehicle_type",
                new PropertyModel<VehicleType>(arrestDocumentModel, "vehicleType"),
                Arrays.asList(VehicleType.values()),
                new EnumChoiceRenderer<VehicleType>(this));
        ddcVehicleType.setRequired(true);
        form.add(ddcVehicleType);

        //Реквизиты транспортного средства
        TextField<String> vehicleDetails = new TextField<String>("arrest.document.vehicle_details",
                new PropertyModel<String>(arrestDocumentModel, "vehicleDetails"));
        vehicleDetails.setRequired(true);
        form.add(vehicleDetails);

        //Отправитель
        addSender(form, "arrest.document.cargo_sender_country", "senderCountry",
                "arrest.document.cargo_sender_name", "senderName",
                arrestDocumentModel);

        //Получатель
        addReceiver(form, "arrest.document.cargo_receiver_name", "receiverName",
                "arrest.document.cargo_receiver_address", "receiverAddress",
                arrestDocumentModel);

        //Дата создания документа
        DatePicker<Date> documentCargoCreated = new DatePicker<Date>("arrest.document.document_cargo_created",
                new PropertyModel<Date>(arrestDocumentModel, "documentCargoCreated"));
        documentCargoCreated.setRequired(true);
        form.add(documentCargoCreated);

        //Реквизиты сертификата
        TextField<String> certificateDetails = new TextField<String>("arrest.document.certificate_detail",
                new PropertyModel<String>(arrestDocumentModel, "certificateDetails"));
        certificateDetails.setRequired(true);
        form.add(certificateDetails);

        //Дата сертификата
        DatePicker<Date> certificateDate = new DatePicker<Date>("arrest.document.certificate_date",
                new PropertyModel<Date>(arrestDocumentModel, "certificateDate"));
        certificateDate.setRequired(true);
        form.add(certificateDate);

        boolean visible = arrestDocumentId != null;

        //Автор
        Label l_creator = new Label("l_creator", new ResourceModel("arrest.document.creator"));
        l_creator.setVisible(visible);
        form.add(l_creator);

        String fullCreator = "";
        if (visible) {
            fullCreator = ad.getCreator().getFullName();
            if (ad.getCreator().getJob() != null) {
                fullCreator += ", " + ad.getCreator().getJob().getDisplayName(getLocale(), getSystemLocale());
            }
            if (ad.getDepartment() != null && !ad.getDepartment().getId().equals(ad.getCreator().getDepartment().getId())) {
                fullCreator += ", " + ad.getCreator().getDepartment().getDisplayName(getLocale(), getSystemLocale());
            }
        }

        Label creator = new Label("creator", fullCreator);
        creator.setVisible(visible);
        form.add(creator);

        //Подразделение и Пункт пропуска через границу
        addDepartmentAndPoint(form, "arrest.document.department", "department", "arrest.document.department.label",
                "arrest.document.passingBorderPoint", "passingBorderPoint", "arrest.document.passingBorderPoint.label",
                arrestDocumentModel, currentUser, visible);

    }

    private void storeArrestDocument(ArrestDocument ad) {
        WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
        HttpServletRequest servletRequest = webRequestCycle.getWebRequest().getHttpServletRequest();
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.setAttribute(ArrestDocumentReportServlet.ARREST_DOCUMENT_KEY, ad);
        }
    }

    private void loadDependencies(ArrestDocument ad) {
        bookViewDAO.addLocalizationSupport(ad);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        //report related stuffs
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new PrintButton(id, ExportType.PDF, "pdfForm") {

            @Override
            protected void onBeforeRender() {
                if (ad.getId() == null) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        toolbarButtons.add(new PrintButton(id, ExportType.TEXT, "textForm") {

            @Override
            protected void onBeforeRender() {
                if (ad.getId() == null) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }
}
