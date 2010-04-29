package org.vetcontrol.document.web.pages;

import java.util.ArrayList;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.document.service.ArrestDocumentBean;
import org.vetcontrol.entity.ArrestDocument;
import org.vetcontrol.entity.ClientEntityId;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.User;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.web.components.PrintButton;
import org.vetcontrol.report.document.jasper.arrest.ArrestDocumentReportServlet;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.web.component.toolbar.ToolbarButton;

import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.04.2010 10:54:47
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_VIEW, DOCUMENT_DEP_CHILD_VIEW})
public class ArrestDocumentView extends TemplatePage {

    private static final Logger log = LoggerFactory.getLogger(ArrestDocumentView.class);
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "ArrestDocumentBean")
    private ArrestDocumentBean arrestDocumentBean;
    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    public ArrestDocumentView(final PageParameters parameters) {
        super();

        final ClientEntityId id = arrestDocumentBean.getClientEntityId(
                parameters.getAsLong("arrest_document_id"),
                parameters.getAsLong("client_id"),
                parameters.getAsLong("department_id"));


        ArrestDocument ad;

        try {
            ad = arrestDocumentBean.loadArrestDocument(id);

            //report related stuffs
            loadDependencies(ad);
        } catch (Exception e) {
            log.error("Ошибка загрузки акта задержания груза,  id = " + id, e);
            getSession().error("Ошибка загрузки акта задержания груза № " + id);
            setResponsePage(ArrestDocumentList.class);
            return;
        }

        //Проверка доступа к данным
        User currentUser = userProfileBean.getCurrentUser();

        boolean authorized = hasAnyRole(DOCUMENT_CREATE)
                && currentUser.getId().equals(ad.getCreator().getId());

        if (!authorized && hasAnyRole(DOCUMENT_DEP_VIEW)) {
            authorized = currentUser.getDepartment().getId().equals(ad.getCreator().getDepartment().getId());
        }

        if (!authorized && hasAnyRole(DOCUMENT_DEP_CHILD_VIEW)) {
            for (Department d = ad.getCreator().getDepartment(); d != null; d = d.getParent()) {
                if (d.getId().equals(currentUser.getDepartment().getId())) {
                    authorized = true;
                    break;
                }
            }
        }

        if (!authorized) {
            log.error("Пользователю запрещен доступ просмотр акта задержания груза id = " + id + ": "
                    + currentUser.toString());
            throw new UnauthorizedInstantiationException(DocumentCargoEdit.class);
        }

        String title = getString("arrest.document.view.title") + " " + id;
        add(new Label("title", title));
        add(new Label("header", title));

        //Дата задержания
        add(DateLabel.forDatePattern("arrest.document.arrest_date", new Model<Date>(ad.getArrestDate()), "dd.MM.yyyy"));

        //Причина задержания
        add(new Label("arrest.document.arrest_reason", getString(ad.getArrestReason())));

        //Детали задержания
        add(new Label("arrest.document.arrest_reason_details", ad.getArrestReasonDetails()));

        //Тип груза
        add(new Label("arrest.document.cargo_type", getString(ad.getCargoType())));

        //Вид груза
        add(new Label("arrest.document.cargo_mode", getString(ad.getCargoMode().getParent()) + " - " + getString(ad.getCargoMode())));

        //Количество
        add(new Label("arrest.document.count", String.valueOf(ad.getCount())));

        //Единицы измерения
        add(new Label("arrest.document.unit_type", getString(ad.getUnitType())));

        //Тип транспортного средства
        add(new Label("arrest.document.vehicle_type", getStringOrKey(ad.getVehicleType() != null ? ad.getVehicleType().name() : "")));

        //Реквизиты транспортного средства
        add(new Label("arrest.document.vehicle_details", ad.getVehicleDetails()));

        //Отправитель
        add(new Label("arrest.document.sender_country", getString(ad.getSenderCountry())));
        add(new Label("arrest.document.sender_name", ad.getSenderName()));

        //Получатель
        add(new Label("arrest.document.receiver_address", ad.getReceiverAddress()));
        add(new Label("arrest.document.receiver_name", ad.getReceiverName()));

        //Дата создания документа
        add(DateLabel.forDatePattern("arrest.document.document_cargo_created", new Model<Date>(ad.getDocumentCargoCreated()), "dd.MM.yyyy"));

        //Реквизиты сертификата
        add(new Label("arrest.document.certificate_details", ad.getCertificateDetails()));

        //Дата сертификата
        add(DateLabel.forDatePattern("arrest.document.certificate_date", new Model<Date>(ad.getCertificateDate()), "dd.MM.yyyy"));

        //Автор
        add(new Label("arrest.document.creator", ad.getCreator() != null ? ad.getCreator().getFullName() : ""));

        //Подразделение
        add(new Label("arrest.document.department", getString(ad.getDepartment())));

        //Пункт пропуска через границу
        add(new Label("arrest.document.passing_border_point", ad.getPassingBorderPoint() != null ? ad.getPassingBorderPoint().getName() : ""));

        //report related stuffs
        storeArrestDocument(ad);
    }

    private void storeArrestDocument(ArrestDocument ad) {
        WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
        HttpServletRequest servletRequest = webRequestCycle.getWebRequest().getHttpServletRequest();
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.setAttribute(ArrestDocumentReportServlet.ARREST_DOCUMENT_KEY, ad);
        }
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        //report related stuffs
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new PrintButton(id, ExportType.PDF, "pdfForm"));
        toolbarButtons.add(new PrintButton(id, ExportType.TEXT, "textForm"));
        return toolbarButtons;
    }

    private void loadDependencies(ArrestDocument ad) {
        bookViewDAO.addLocalizationSupport(ad);
    }
}
