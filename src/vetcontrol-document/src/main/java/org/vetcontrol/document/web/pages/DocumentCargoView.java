package org.vetcontrol.document.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.document.service.DocumentCargoBean;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Date;
import org.vetcontrol.web.component.MovementTypeChoicePanel;

import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.01.2010 17:35:54
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_VIEW, DOCUMENT_DEP_CHILD_VIEW})
public class DocumentCargoView extends TemplatePage {

    private static final Logger log = LoggerFactory.getLogger(DocumentCargoView.class);
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "DocumentCargoBean")
    private DocumentCargoBean documentCargoBean;

    public DocumentCargoView(final PageParameters parameters) {
        super();

        final ClientEntityId id = documentCargoBean.getDocumentCargoId(
                parameters.getAsLong("document_cargo_id"),
                parameters.getAsLong("client_id"),
                parameters.getAsLong("department_id"));

        LoadableDetachableModel<DocumentCargo> model = new LoadableDetachableModel<DocumentCargo>() {

            @Override
            protected DocumentCargo load() {
                try {
                    return documentCargoBean.loadDocumentCargo(id);
                } catch (Exception e) {
                    log.error("Ошибка загрузки документа карточка на груз, id = " + id, e);
                    return null;
                }
            }
        };

        DocumentCargo dc = model.getObject();

        if (dc == null) {
            getSession().error("Карточка на груз №" + id + " не найдена");
            setResponsePage(DocumentCargoList.class);
            return;
        }

        //Проверка доступа к данным
        User currentUser = userProfileBean.getCurrentUser();

        boolean authorized = hasAnyRole(DOCUMENT_CREATE)
                && currentUser.getId().equals(dc.getCreator().getId());

        if (!authorized && hasAnyRole(DOCUMENT_DEP_VIEW)) {
            authorized = currentUser.getDepartment().getId().equals(dc.getCreator().getDepartment().getId());
        }

        if (!authorized && hasAnyRole(DOCUMENT_DEP_CHILD_VIEW)) {
            for (Department d = dc.getCreator().getDepartment(); d != null; d = d.getParent()) {
                if (d.getId().equals(currentUser.getDepartment().getId())) {
                    authorized = true;
                    break;
                }
            }
        }

        if (!authorized) {
            log.error("Пользователю запрещен доступ просмотр карточки на груз id = " + id + ": "
                    + currentUser.toString());
            throw new UnauthorizedInstantiationException(DocumentCargoEdit.class);
        }

        String title = getString("document.cargo.view.title") + " " + id;
        add(new Label("title", title));
        add(new Label("header", title));

        add(new Label("document.cargo.movement_type", MovementTypeChoicePanel.getDysplayName(dc.getMovementType(), getLocale())));
        add(new Label("document.cargo.vehicle_type", getString(dc.getVehicleType().name())));
        add(new Label("document.cargo.cargo_sender_country", dc.getSenderCountry().getDisplayName(getLocale(), getSystemLocale())));
        add(new Label("document.cargo.cargo_sender_name", dc.getSenderName()));
        add(new Label("document.cargo.cargo_receiver_name", dc.getReceiverName()));
        add(new Label("document.cargo.cargo_receiver_address", dc.getReceiverAddress()));
        add(new Label("document.cargo.details", dc.getDetails()));
        add(new Label("document.cargo.department", dc.getDepartment().getDisplayName(getLocale(), getSystemLocale())));
        add(new Label("document.cargo.passingBorderPoint", dc.getPassingBorderPoint() != null ? dc.getPassingBorderPoint().getName() : ""));

        String fullCreator = dc.getCreator().getFullName();
        if (dc.getCreator().getJob() != null) {
            fullCreator += ", " + dc.getCreator().getJob().getDisplayName(getLocale(), getSystemLocale());
        }
        if (dc.getDepartment() != null && !dc.getDepartment().getId().equals(dc.getCreator().getDepartment().getId())) {
            fullCreator += ", " + dc.getCreator().getDepartment().getDisplayName(getLocale(), getSystemLocale());
        }
        add(new Label("document.cargo.creator_name", fullCreator));
        add(new DateLabel("document.cargo.created", new Model<Date>(dc.getCreated()), new StyleDateConverter(true)));

        ListView<Cargo> dataView = new ListView<Cargo>("document.cargo.cargo_list", dc.getCargos()) {

            @Override
            protected void populateItem(ListItem<Cargo> item) {
                Cargo c = item.getModelObject();

                item.add(new Label("document.cargo.cargo_type", c.getCargoType() != null ? c.getCargoType().getDisplayName(getLocale(), getSystemLocale()) : ""));
                item.add(new Label("document.cargo.count", c.getCount() != null ? c.getCount() + "" : ""));
                item.add(new Label("document.cargo.unit_type", c.getUnitType() != null ? c.getUnitType().getDisplayName(getLocale(), getSystemLocale()) : ""));
                item.add(new Label("document.cargo.vehicle", c.getVehicle() != null ? c.getVehicle().getVehicleDetails() : ""));
                item.add(new Label("document.cargo.producer_country", c.getCargoProducer().getCountry().getDisplayName(getLocale(), getSystemLocale())));
                item.add(new Label("document.cargo.producer_name", c.getCargoProducer().getDisplayName(getLocale(), getSystemLocale())));
                item.add(new Label("document.cargo.certificate_detail", c.getCertificateDetails()));
                item.add(new DateLabel("document.cargo.certificate_date", new Model<Date>(c.getCertificateDate()),
                        new StyleDateConverter(true)));
            }
        };

        add(dataView);

        add(new Spacer("spacer"));
    }
}
