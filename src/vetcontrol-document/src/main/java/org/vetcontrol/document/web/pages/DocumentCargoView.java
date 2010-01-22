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
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.User;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Date;
import java.util.Locale;

import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.01.2010 17:35:54
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_VIEW, DOCUMENT_DEP_CHILD_VIEW})
public class DocumentCargoView extends TemplatePage{
    private static final Logger log = LoggerFactory.getLogger(DocumentCargoView.class);

    @EJB(name = "UserProfileBean")
    UserProfileBean userProfileBean;

    @EJB(name = "DocumentBean")
    DocumentCargoBean documentCargoBean;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    public DocumentCargoView(final PageParameters parameters) {
        super();

        final Long id = parameters.getAsLong("document_cargo_id");

        LoadableDetachableModel<DocumentCargo> model = new LoadableDetachableModel<DocumentCargo>(){

            @Override
            protected DocumentCargo load() {
                try {
                    return documentCargoBean.loadDocumentCargo(id);
                } catch (Exception e) {
                    log.error("Ошибка загрузки документа карточка на груз, id = "+id, e);
                    return null;
                }
            }
        };

        DocumentCargo dc = model.getObject();

        if (dc == null){
            getSession().error("Карточка на груз №"+id+" не найдена");
            setResponsePage(DocumentCargoList.class);
            return;
        }

        //Проверка доступа к данным
        User currentUser = userProfileBean.getCurrentUser();

        boolean authorized = hasAnyRole(DOCUMENT_CREATE)
                && currentUser.getId().equals(dc.getCreator().getId());

        if (!authorized && hasAnyRole(DOCUMENT_DEP_VIEW)){
            authorized = currentUser.getDepartment().getId().equals(dc.getCreator().getDepartment().getId());
        }

        if (!authorized && hasAnyRole(DOCUMENT_DEP_CHILD_VIEW)){
            for(Department d = dc.getCreator().getDepartment(); d != null; d = d.getParent()){
                if (d.getId().equals(currentUser.getDepartment().getId())){
                    authorized = true;
                    break;
                }
            }
        }

        if (!authorized){
            log.error("Пользователю запрещен доступ просмотр карточки на груз id = " + id +  ": "
                    + currentUser.toString());
            throw new UnauthorizedInstantiationException(DocumentCargoEdit.class);
        }

        String title = getString("document.cargo.view.title") + id;
        add(new Label("title", title));
        add(new Label("header", title));

        final Locale system = localeDAO.systemLocale();

        add(new Label("document.cargo.movement_type", dc.getMovementType().getDisplayName(getLocale(), system)));
        add(new Label("document.cargo.vehicle_type", dc.getVehicleType().getDisplayName(getLocale(), system)));
        add(new Label("document.cargo.vehicle_details", dc.getVehicleDetails()));
        add(new Label("document.cargo.cargo_sender", dc.getCargoSender().getDisplayName(getLocale(), system)));
        add(new Label("document.cargo.cargo_receiver", dc.getCargoReceiver().getDisplayName(getLocale(), system)));
        add(new Label("document.cargo.cargo_producer", dc.getCargoProducer().getDisplayName(getLocale(), system)));
        add(new Label("document.cargo.passingBorderPoint", dc.getPassingBorderPoint().getDisplayName(getLocale(), system)));
        add(new Label("document.cargo.detention_details", dc.getDetentionDetails()));
        add(new Label("document.cargo.details", dc.getDetails()));
        add(new Label("document.cargo.creator_name", dc.getCreator().getFullName()));
        add(new Label("document.cargo.department", dc.getCreator().getDepartment().getDisplayName(getLocale(), system)));
        add(new DateLabel("document.cargo.created", new Model<Date>(dc.getCreated()), new StyleDateConverter(true)));

        ListView<Cargo> dataView = new ListView<Cargo>("document.cargo.cargo_list", dc.getCargos()){

            @Override
            protected void populateItem(ListItem<Cargo> item) {
                item.add(new Label("document.cargo.cargo_type", item.getModelObject().getCargoType().getDisplayName(getLocale(), system)));
                item.add(new Label("document.cargo.count", String.valueOf(item.getModelObject().getCount())));
                item.add(new Label("document.cargo.unit_type", item.getModelObject().getUnitType().getDisplayName(getLocale(), system)));
                item.add(new Label("document.cargo.certificate_detail", item.getModelObject().getCertificateDetails()));
                item.add(new DateLabel("document.cargo.certificate_date", new Model<Date>(item.getModelObject().getCertificateDate()),
                        new StyleDateConverter(true)));
            }
        };

        add(dataView);

    }
}
