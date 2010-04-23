package org.vetcontrol.document.web.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.document.service.DocumentBean;
import org.vetcontrol.document.web.component.BookNamedChoiceRenderer;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.vetcontrol.entity.Log.EVENT.EDIT;
import static org.vetcontrol.entity.Log.MODULE.DOCUMENT;
import static org.vetcontrol.web.security.SecurityRoles.DOCUMENT_DEP_CHILD_EDIT;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.04.2010 7:17:41
 */
public abstract class DocumentEditPage extends FormTemplatePage {
    private static final Logger log = LoggerFactory.getLogger(DocumentEditPage.class);

    @EJB(name = "DocumentBean")
    private transient DocumentBean documentBean;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    private Locale system = localeDAO.systemLocale();

    @EJB(name = "LogBean")
    private LogBean logBean;

    @EJB(name = "CargoTypeBean")
    private CargoTypeBean cargoTypeBean;         

    protected <T extends Localizable> DropDownChoice<T> addBookDropDownChoice(
            WebMarkupContainer container, String id, Class<T> bookClass, IModel model, String property) {
        return addBookDropDownChoice(container, id, bookClass, new PropertyModel<T>(model, property), true, false);
    }

    protected <T extends Localizable> DropDownChoice<T> addBookDropDownChoice(
            WebMarkupContainer container, String id, Class<T> bookClass, IModel model, String property,
            boolean required, boolean outputMarkupId) {
        return addBookDropDownChoice(container, id, bookClass, new PropertyModel<T>(model, property), required, outputMarkupId);
    }

    protected <T extends Localizable> DropDownChoice<T> addBookDropDownChoice(
            WebMarkupContainer container, String id, Class<T> bookClass, IModel<T> model,
            boolean required, boolean outputMarkupId) {
        List<T> list = null;

        try {
            list = documentBean.getBookList(bookClass);
        } catch (Exception e) {
            log.error("Ошибка загрузки списка справочников: " + bookClass, e);
            logBean.error(DOCUMENT, EDIT, this.getClass(), bookClass, "Ошибка загрузки данных из базы данных");
        }

        DropDownChoice<T> ddc = new DropDownChoice<T>(id, model, list, new BookNamedChoiceRenderer<T>(system));

        ddc.setRequired(required);
        ddc.setOutputMarkupId(outputMarkupId);
        container.add(ddc);

        return ddc;
    }

    protected <T extends Localizable> DropDownChoice<T> addBookDropDownChoice(
            WebMarkupContainer container, String id, IModel model, String property ,
            IModel<List<T>> listModel, boolean required, boolean outputMarkupId) {
        return addBookDropDownChoice(container, id,  new PropertyModel<T>(model, property),
                listModel, required, outputMarkupId);
    }

    protected <T extends Localizable> DropDownChoice<T> addBookDropDownChoice(
            WebMarkupContainer container, String id, IModel<T> model, IModel<List<T>> listModel,
            boolean required, boolean outputMarkupId) {
        DropDownChoice<T> ddc = new DropDownChoice<T>(id, model, listModel, new BookNamedChoiceRenderer<T>(system));

        ddc.setRequired(required);
        ddc.setOutputMarkupId(outputMarkupId);
        container.add(ddc);

        return ddc;
    }

    protected void addSender(WebMarkupContainer container, String countryId, String countryProperty, String nameId,
                             String nameProperty, IModel model){
        //Отравитель Страна
        final DropDownChoice<CountryBook> ddcSenderCountry = addBookDropDownChoice(container, countryId,
                CountryBook.class, model, countryProperty);
        ddcSenderCountry.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //update model
            }
        });
        ddcSenderCountry.setOutputMarkupId(true);

        //Настройки автокомплита
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setAdjustInputWidth(false);

        //Отравитель Название
        final AutoCompleteTextField<String> senderName = new AutoCompleteTextField<String>(nameId,
                new PropertyModel<String>(model, nameProperty), settings) {

            @Override
            protected Iterator<String> getChoices(String input) {
                return documentBean.getSenderNames(ddcSenderCountry.getModelObject(), input).iterator();
            }
        };
        senderName.setRequired(true);
        senderName.setOutputMarkupId(true);
        container.add(senderName);
    }

    protected void addReceiver(WebMarkupContainer container, String nameId, String nameProperty,
                               String addressId, String addressProperty, IModel model){
        //Настройки автокомплита
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setAdjustInputWidth(false);

        //Получатель Название
        final AutoCompleteTextField<String> receiverName = new AutoCompleteTextField<String>(nameId,
                new PropertyModel<String>(model, nameProperty), settings) {

            @Override
            protected Iterator<String> getChoices(String input) {
                return documentBean.getReceiverNames(input).iterator();
            }
        };
        receiverName.setRequired(true);
        receiverName.setOutputMarkupId(true);
        container.add(receiverName);

        //Получатель Адрес
        final AutoCompleteTextField<String> receiverAddress = new AutoCompleteTextField<String>(addressId,
                new PropertyModel<String>(model, addressProperty), settings) {

            @Override
            protected Iterator<String> getChoices(String input) {

                return documentBean.getReceiverNames(input).iterator();
            }
        };
        receiverAddress.setRequired(true);
        receiverAddress.setOutputMarkupId(true);
        container.add(receiverAddress);

        //Подстановка адреса при вводе получателя
        receiverName.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String address = receiverAddress.getModelObject();
                String name = receiverName.getModelObject();

                if ((address == null || address.isEmpty()) && name != null && !name.isEmpty()) {
                    address = documentBean.getReceiverAddress(name);
                    if (address != null) {
                        receiverAddress.setModelObject(address);
                        target.addComponent(receiverAddress);
                    }
                }
            }
        });
    }

    protected void addDepartmentAndPoint(WebMarkupContainer container, String ddcDepartmentId, String departmentProperty,
                                         String labelDepartmentId, String ddcPointId, String pointProperty,
                                         String labelPointId, IModel model, User currentUser, boolean visible){
        final Department department = new PropertyModel<Department>(model, departmentProperty).getObject();
        final PassingBorderPoint point =  new PropertyModel<PassingBorderPoint>(model, pointProperty).getObject();

        //Подразделение
        List<Department> list;

        try {
            list = documentBean.getChildDepartments(currentUser.getDepartment());
            if (!list.contains(department)) {
                list.add(department);
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки списка дочерних подразделений:", e);
            logBean.error(DOCUMENT, EDIT, this.getClass(), Department.class, "Ошибка загрузки данных из базы данных");
        }

        //Если роль редактировать подразделение то отобразить выпадающий список иначе статический текс
        final DropDownChoice<Department> ddcDepartment =  addBookDropDownChoice(container, ddcDepartmentId, Department.class,
                model, departmentProperty, true, true);
        ddcDepartment.setVisible(hasAnyRole(DOCUMENT_DEP_CHILD_EDIT) && !visible);

        Label departmentLabel = new Label(labelDepartmentId, department.getDisplayName(getLocale(), system));
        departmentLabel.setVisible(!hasAnyRole(DOCUMENT_DEP_CHILD_EDIT) || visible);
        container.add(departmentLabel);

        //Пункт пропуска через границу
        final DropDownChoice<PassingBorderPoint> ddcPassingBorderPoint = new DropDownChoice<PassingBorderPoint>(ddcPointId,
                new PropertyModel<PassingBorderPoint>(model, pointProperty),
                new LoadableDetachableModel<List<PassingBorderPoint>>() {

                    @Override
                    protected List<PassingBorderPoint> load() {
                        return documentBean.getPassingBorderPoints(ddcDepartment.getModelObject());
                    }
                }, new IChoiceRenderer<PassingBorderPoint>() {

                    @Override
                    public Object getDisplayValue(PassingBorderPoint object) {
                        return object.getName();
                    }

                    @Override
                    public String getIdValue(PassingBorderPoint object, int index) {
                        return object.getId().toString();
                    }
                });
        ddcPassingBorderPoint.setOutputMarkupId(true);
        ddcPassingBorderPoint.setVisible(hasAnyRole(DOCUMENT_DEP_CHILD_EDIT) && !visible);
        container.add(ddcPassingBorderPoint);

        ddcDepartment.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(ddcPassingBorderPoint);
            }
        });

        Label passingBorderPointLabel = new Label(labelPointId, point != null ? point.getName() : "");
        passingBorderPointLabel.setVisible(!hasAnyRole(DOCUMENT_DEP_CHILD_EDIT) || visible);
        container.add(passingBorderPointLabel);
    }

}
