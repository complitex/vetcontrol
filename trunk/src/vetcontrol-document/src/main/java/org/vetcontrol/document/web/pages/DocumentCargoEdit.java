package org.vetcontrol.document.web.pages;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.document.service.DocumentCargoBean;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.UKTZEDField;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static org.vetcontrol.entity.Log.EVENT.CREATE;
import static org.vetcontrol.entity.Log.EVENT.EDIT;
import static org.vetcontrol.entity.Log.EVENT.VIEW;
import static org.vetcontrol.entity.Log.MODULE.DOCUMENT;
import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 15:44:20
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_EDIT, DOCUMENT_DEP_CHILD_EDIT})
public class DocumentCargoEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(DocumentCargoEdit.class);
    @EJB(name = "DocumentBean")
    private DocumentCargoBean documentCargoBean;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "CargoTypeBean")
    private CargoTypeBean cargoTypeBean;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "LogBean")
    private LogBean logBean;

    public DocumentCargoEdit() {
        super();
        init(null);
    }

    public DocumentCargoEdit(final PageParameters parameters) {
        super();
        init(parameters.getAsLong("document_cargo_id"));
    }

    private void init(final Long id) {
        //Модель данных
        DocumentCargo dc;
        try {
            dc = (id != null) ? documentCargoBean.loadDocumentCargo(id) : new DocumentCargo();
        } catch (Exception e) {
            log.error("Карточка на груз по id = " + id + " не найдена", e);
            getSession().error("Карточка на груз №" + id + " не найдена");
            logBean.error(DOCUMENT, EDIT, DocumentCargoEdit.class, DocumentCargo.class,
                    "Карточка не найдена. ID: " + id);

            setResponsePage(DocumentCargoList.class);
            return;
        }

        //Проверка доступа к данным        
        User currentUser = userProfileBean.getCurrentUser();

        //Установка подразделения по умолчанию
        if (dc.getId() == null){
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

            if (!authorized) {
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
                        ? getString("document.cargo.edit.title.edit") + id
                        : getString("document.cargo.edit.title.create");
            }
        };
        add(new Label("title", title));
        add(new Label("header", title));

        add(new FeedbackPanel("messages"));

        final Model<DocumentCargo> documentCargoModel = new Model<DocumentCargo>(dc);

        //Форма
        final Form form = new Form<DocumentCargo>("doc_cargo_edit_form", documentCargoModel) {

            @Override
            protected void onValidate() {
                for (Cargo c : getModelObject().getCargos()) {
                    if (c.getCargoType() == null) {
                        error(getString("document.cargo.edit.message.cargo_type.not_found.error"));
                    }
                }
            }

            @Override
            protected void onSubmit() {
                try {
                    documentCargoBean.save(getModelObject());
                    setResponsePage(DocumentCargoList.class);
                    if (id == null) {
                        getSession().info(getString("document.cargo.edit.message.added", getModel()));
                    } else {
                        getSession().info(getString("document.cargo.edit.message.saved", getModel()));
                    }

                    logBean.info(DOCUMENT, id == null ? CREATE : EDIT, DocumentCargoEdit.class, DocumentCargo.class,
                                "ID: " + getModelObject().getId());
                } catch (Exception e) {
                    getSession().info(getString("document.cargo.edit.message.save.error", getModel()));

                    log.error("Ошибка сохранения карточки на груз №" + getModelObject().getId(), e);

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
        addDropDownChoice(form, "document.cargo.movement_type", MovementType.class, documentCargoModel, "movementType");

        //Тип транспортного средства
        addDropDownChoice(form, "document.cargo.vehicle_type", VehicleType.class, documentCargoModel, "vehicleType");

        //Тип транспортного средства - реквизиты
        TextField vehicleDetails = new TextField<String>("document.cargo.vehicle_details",
                new PropertyModel<String>(documentCargoModel, "vehicleDetails"));
        vehicleDetails.setRequired(true);
        form.add(vehicleDetails);

        //Список грузов
        final WebMarkupContainer cargoContainer = new WebMarkupContainer("document.cargo.cargo_container");
        cargoContainer.setOutputMarkupId(true);
        form.add(cargoContainer);

        //Добавить груз
        final AjaxSubmitLink addCargoLink = new AjaxSubmitLink("document.cargo.cargo.add", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                DocumentCargo dc = (DocumentCargo) form.getModelObject();
                Cargo cargo = new Cargo();
                cargo.setDocumentCargo(dc);
                dc.getCargos().add(cargo);
                target.addComponent(cargoContainer);

                String setFocusOnNewCargo = "newCargoFirstInputId = $('.table_input tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newCargoFirstInputId);";
                target.appendJavascript(setFocusOnNewCargo);
            }
        };
        addCargoLink.setDefaultFormProcessing(false);
        cargoContainer.add(addCargoLink);


        final ListView cargoListView = new ListView<Cargo>("document.cargo.cargo_list",
                new PropertyModel<List<Cargo>>(documentCargoModel, "cargos")) {

            @Override
            protected IModel<Cargo> getListItemModel(IModel<? extends List<Cargo>> listViewModel, int index) {
                return new Model<Cargo>(listViewModel.getObject().get(index));
            }

            @Override
            protected void populateItem(final ListItem<Cargo> item) {
                addCargo(item);

                //Удалить
                AjaxSubmitLink deleteLink = new AjaxSubmitLink("document.cargo.delete", form) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        @SuppressWarnings({"unchecked"})
                        ListItem<Cargo> item = (ListItem) getParent();
                        Cargo remove = item.getModelObject();
                        @SuppressWarnings({"unchecked"})
                        ListView<Cargo> list = (ListView<Cargo>) item.getParent();

                        int last_index = list.getModelObject().size() - 1;

                        //Copy childs from next list item and remove last 
                        for (int index = item.getIndex(); index < last_index; index++) {
                            ListItem li = (ListItem) item.getParent().get(index);
                            ListItem li_next = (ListItem) item.getParent().get(index + 1);

                            li.removeAll();
                            //noinspection unchecked
                            li.setModelObject(li_next.getModelObject());

                            int size = li_next.size();
                            Component[] childs = new Component[size];
                            for (int i = 0; i < size; i++) {
                                childs[i] = li_next.get(i);
                            }
                            li.add(childs);
                        }

                        list.getModelObject().remove(remove);                         
                        item.getParent().get(last_index).remove();

                        target.addComponent(cargoContainer);
                        target.focusComponent(addCargoLink);
                    }
                };
                deleteLink.setDefaultFormProcessing(false);

                item.add(deleteLink);
            }
        };
        cargoListView.setReuseItems(true);
        cargoContainer.add(cargoListView);

        //Отравитель
        addDropDownChoice(form, "document.cargo.cargo_sender", CargoSender.class, documentCargoModel, "cargoSender");

        //Получатель
        addDropDownChoice(form, "document.cargo.cargo_receiver", CargoReceiver.class, documentCargoModel, "cargoReceiver");

        //Производитель
        addDropDownChoice(form, "document.cargo.cargo_producer", CargoProducer.class, documentCargoModel, "cargoProducer");

        //Пункт пропуска через границу
        addDropDownChoice(form, "document.cargo.passingBorderPoint", PassingBorderPoint.class, documentCargoModel, "passingBorderPoint");

        //Реквизиты акта задержания груза
        TextField detentionDetails = new TextField<String>("document.cargo.detention_details",
                new PropertyModel<String>(documentCargoModel, "detentionDetails"));
        form.add(detentionDetails);

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
        if (visible){
            fullCreator =  dc.getCreator().getFullName();
            if (dc.getCreator().getJob() != null){
                fullCreator += ", " + dc.getCreator().getJob().getDisplayName(getLocale(), localeDAO.systemLocale());
            }
            if (dc.getDepartment() != null && !dc.getDepartment().getId().equals(dc.getCreator().getDepartment().getId())){
                fullCreator += ", " + dc.getCreator().getDepartment().getDisplayName(getLocale(), localeDAO.systemLocale());
            }
        }
                
        Label creator = new Label("creator", fullCreator);
        creator.setVisible(visible);
        form.add(creator);

        //Подразделение
        DropDownChoice ddcDepartment = addDropDownChoice(form, "document.cargo.department", Department.class, documentCargoModel, "department");
        ddcDepartment.setEnabled(hasAnyRole(DOCUMENT_DEP_CHILD_EDIT));

        //Дата создания
        Label l_created = new Label("l_created", new ResourceModel("document.cargo.created"));
        l_created.setVisible(visible);
        form.add(l_created);

        DateLabel created = new DateLabel("created", new Model<Date>(
                visible ? documentCargoModel.getObject().getCreated() : new Date()),
                new StyleDateConverter(true));
        created.setVisible(visible);
        form.add(created);
    }

    private <T extends Localizable> DropDownChoice<T> addDropDownChoice(WebMarkupContainer container, String id, Class<T> bookClass, IModel<DocumentCargo>  model, String property){
        List<T> list = null;

        try {
            list = documentCargoBean.getList(bookClass);
        } catch (Exception e) {
            log.error("Ошибка загрузки списка справочников: " + bookClass, e);
            logBean.error(DOCUMENT, VIEW, DocumentCargoEdit.class, bookClass, "Ошибка загрузки данных из базы данных");
        }

        DropDownChoice<T> ddc = new DropDownChoice<T>(id,
                new PropertyModel<T>(model, property), list,
                new IChoiceRenderer<T>() {

                    @Override
                    public Object getDisplayValue(T object) {
                        return object.getDisplayName(getLocale(), localeDAO.systemLocale());
                    }

                    @Override
                    public String getIdValue(T object, int index) {
                        return String.valueOf(object.getId());
                    }
                });

        ddc.setRequired(true);
        container.add(ddc);

        return ddc;
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

    private void addCargo(ListItem<Cargo> item) {
        //Единицы измерения        
        UnitTypeModel unitTypeModel = new UnitTypeModel(item.getModelObject());

        DropDownChoice<UnitType> ddcUnitTypes = new DropDownChoice<UnitType>("document.cargo.unit_type",
                new PropertyModel<UnitType>(item.getModelObject(), "unitType"), unitTypeModel,
                new IChoiceRenderer<UnitType>() {

                    @Override
                    public Object getDisplayValue(UnitType object) {
                        return object.getDisplayName(getLocale(), localeDAO.systemLocale());
                    }

                    @Override
                    public String getIdValue(UnitType object, int index) {
                        return String.valueOf(object.getId());
                    }
                });

        ddcUnitTypes.setRequired(true);
        ddcUnitTypes.setOutputMarkupId(true);
        item.add(ddcUnitTypes);

        //УКТЗЕД и Тип груза
        item.add(new UKTZEDField("document.cargo.cargo_type", new PropertyModel<CargoType>(item.getModel(), "cargoType"),
                ddcUnitTypes));

        //Количество
        TextField<Integer> count = new TextField<Integer>("document.cargo.count",
                new PropertyModel<Integer>(item.getModel(), "count"));
        count.setRequired(true);
        item.add(count);

        //Реквизиты сертификата
        TextField<String> certificateDetails = new TextField<String>("document.cargo.certificate_detail",
                new PropertyModel<String>(item.getModel(), "certificateDetails"));
        certificateDetails.setRequired(true);
        item.add(certificateDetails);

        //Дата сертификата
        DatePicker<Date> certificateDate = new DatePicker<Date>("document.cargo.certificate_date",
                new PropertyModel<Date>(item.getModel(), "certificateDate"));
        certificateDate.setButtonImage("images/calendar.gif");
        certificateDate.setButtonImageOnly(true);
        certificateDate.setShowOn(DatePicker.ShowOnEnum.BOTH);
        certificateDate.setRequired(true);
        item.add(certificateDate);
    }
}
