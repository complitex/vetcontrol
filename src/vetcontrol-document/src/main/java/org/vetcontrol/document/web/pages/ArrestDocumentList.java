package org.vetcontrol.document.web.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
import org.vetcontrol.document.service.ArrestDocumentBean;
import org.vetcontrol.document.service.ArrestDocumentFilter;
import org.vetcontrol.document.web.component.BookNamedChoiceRenderer;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.component.BookmarkablePageLinkPanel;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;
import org.vetcontrol.web.component.datatable.ArrowOrderByBorder;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.AddDocumentButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.template.ListTemplatePage;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.04.2010 19:46:51
 *
 * Класс контроллер страницы списка актов задержания груза
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_VIEW, DOCUMENT_DEP_CHILD_VIEW})
public class ArrestDocumentList extends ListTemplatePage {
    private static final String PAGE_NUMBER_KEY = ArrestDocumentList.class.getSimpleName() + "_PAGE_NUMBER";
    private static final String SORT_PROPERTY_KEY = ArrestDocumentList.class.getSimpleName() + "_SORT_PROPERTY";
    private static final String SORT_ORDER_KEY = ArrestDocumentList.class.getSimpleName() + "_SORT_ORDER";
    private static final String FILTER_KEY = ArrestDocumentList.class.getSimpleName() + "_FILTER";

    @EJB(name = "ArrestDocumentBean")
    private ArrestDocumentBean arrestDocumentBean;

    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;

    @EJB(name = "ClientBean")
    private ClientBean clientBean;

    private final boolean server = clientBean.isServer();

    public ArrestDocumentList() {
        super();

        add(new Label("title", new ResourceModel("arrest.document.list.title")));
        add(new Label("header", new ResourceModel("arrest.document.list.title")));

        add(new FeedbackPanel("messages"));

        final UIPreferences preferences = getPreferences();

        //Фильтр
        ArrestDocumentFilter filterObject = preferences.getPreference(UIPreferences.PreferenceType.FILTER,
                FILTER_KEY, ArrestDocumentFilter.class);

        if (filterObject == null){
            filterObject = newArrestDocumentFilter();
        }

        final IModel<ArrestDocumentFilter> filter = new CompoundPropertyModel<ArrestDocumentFilter>(filterObject);
        final Form<ArrestDocumentFilter> filterForm = new Form<ArrestDocumentFilter>("filter_form", filter);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filter.setObject(newArrestDocumentFilter());
            }
        };
        filterForm.add(filter_reset);

        filterForm.add(new TextField<String>("id"));
        filterForm.add(new TextField<String>("cargoType"));
        filterForm.add(new TextField<String>("cargoMode"));
        filterForm.add(new TextField<String>("count"));
        filterForm.add(new DropDownChoice<UnitType>("unitType", arrestDocumentBean.getList(UnitType.class),
                new BookNamedChoiceRenderer<UnitType>(getSystemLocale())));
        filterForm.add(new VehicleTypeChoicePanel("vehicleType", new PropertyModel<VehicleType>(filter, "vehicleType"), false));
        filterForm.add(new DropDownChoice<CountryBook>("senderCountry", arrestDocumentBean.getList(CountryBook.class),
                new BookNamedChoiceRenderer<CountryBook>(getSystemLocale())));
        filterForm.add(new TextField<String>("senderName"));
        filterForm.add(new TextField<String>("receiverAddress"));
        filterForm.add(new TextField<String>("receiverName"));

        filterForm.add(new DropDownChoice<ArrestReason>("arrestReason", arrestDocumentBean.getList(ArrestReason.class),
                new BookNamedChoiceRenderer<ArrestReason>(getSystemLocale())));
        filterForm.add(new DatePicker<Date>("arrestDate"));

        DropDownChoice<Synchronized.SyncStatus> ddcSyncStatus =
                new DropDownChoice<Synchronized.SyncStatus>("syncStatus", Arrays.asList(Synchronized.SyncStatus.values()),
                        new IChoiceRenderer<Synchronized.SyncStatus>() {

                            @Override
                            public Object getDisplayValue(Synchronized.SyncStatus object) {
                                return getString(object.name());
                            }

                            @Override
                            public String getIdValue(Synchronized.SyncStatus object, int index) {
                                return object.name();
                            }
                        });
        ddcSyncStatus.setVisible(!server);
        filterForm.add(ddcSyncStatus);

        //Модель данных списка карточек на груз
        final SortableDataProvider<ArrestDocument> dataProvider = new SortableDataProvider<ArrestDocument>() {

            @Override
            public Iterator<? extends ArrestDocument> iterator(int first, int count) {
                ArrestDocumentBean.OrderBy sort = ArrestDocumentBean.OrderBy.valueOf(getSort().getProperty());
                boolean asc = getSort().isAscending();

                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, getSort().getProperty());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, asc);
                preferences.putPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, filter.getObject());

                return arrestDocumentBean.getArrestDocuments(filter.getObject(), first, count, sort, asc).iterator();
            }

            @Override
            public int size() {
                return arrestDocumentBean.getArrestDocumentsSize(filter.getObject()).intValue();
            }

            @Override
            public IModel<ArrestDocument> model(ArrestDocument object) {
                return new Model<ArrestDocument>(object);
            }
        };
        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : ArrestDocumentBean.OrderBy.ARREST_DATE.name();
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
        dataProvider.setSort(sortProp, asc);


        //Таблица актов задержания грузов
        final DataView<ArrestDocument> dataView = new DataView<ArrestDocument>("documents", dataProvider, 1) {

            @Override
            protected void populateItem(Item<ArrestDocument> item) {
                ArrestDocument ad = item.getModelObject();

                PageParameters pageParameters = new PageParameters("arrest_document_id=" + ad.getId() + ","
                        + "client_id=" + ad.getClient().getId() + "," + "department_id=" + ad.getDepartment().getId());
                item.add(new BookmarkablePageLinkPanel<DocumentCargo>("id", ad.getDisplayId(),
                        ArrestDocumentView.class, pageParameters));

                item.add(new Label("cargoType", ad.getCargoType().getDisplayName(getLocale(), getSystemLocale())));
                item.add(new Label("cargoMode", ad.getCargoMode() != null ? ad.getCargoMode().getDisplayName(getLocale(), getSystemLocale()) : ""));
                item.add(new Label("count", ad.getCount() + ""));
                item.add(new Label("unitType", ad.getUnitType() != null ? ad.getUnitType().getDisplayName(getLocale(), getSystemLocale()) : ""));
                item.add(new Label("vehicleType", VehicleTypeChoicePanel.getDysplayName(ad.getVehicleType(), getLocale())));
                item.add(new Label("senderCountry", ad.getSenderCountry().getDisplayName(getLocale(), getSystemLocale())));
                item.add(new Label("senderName", ad.getSenderName()));
                item.add(new Label("receiverAddress", ad.getReceiverAddress()));
                item.add(new Label("receiverName", ad.getReceiverName()));
                item.add(new Label("arrestReason", ad.getArrestReason().getDisplayName(getLocale(), getSystemLocale())));
                item.add(new DateLabel("arrestDate", new Model<Date>(ad.getArrestDate()), new StyleDateConverter(true)));

                Label syncStatus = new Label("syncStatus", getString(ad.getSyncStatus().name()));
                syncStatus.setVisible(!server);
                item.add(syncStatus);

                if ((server || !ad.getSyncStatus().equals(Synchronized.SyncStatus.SYNCHRONIZED)) && canEdit(ad)) {
                    item.add(new BookmarkablePageLinkPanel<DocumentCargo>("action", getString("arrest.document.list.edit"),
                            ArrestDocumentEdit.class, pageParameters));
                } else {
                    item.add(new BookmarkablePageLinkPanel<DocumentCargo>("action", getString("arrest.document.list.view"),
                            ArrestDocumentView.class, pageParameters));
                }
            }
        };

        //Ссылки для сортировки
        addOrderByBorder(filterForm, "order_id", ArrestDocumentBean.OrderBy.ID.name(), dataProvider, dataView);        
        addOrderByBorder(filterForm, "order_arrest_date", ArrestDocumentBean.OrderBy.ARREST_DATE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_arrest_reason", ArrestDocumentBean.OrderBy.ARREST_REASON.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_cargo_type", ArrestDocumentBean.OrderBy.CARGO_TYPE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_cargo_mode", ArrestDocumentBean.OrderBy.CARGO_MODE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_count", ArrestDocumentBean.OrderBy.COUNT.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_unit_type", ArrestDocumentBean.OrderBy.UNIT_TYPE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_vehicle_type", ArrestDocumentBean.OrderBy.VEHICLE_TYPE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_sender_country", ArrestDocumentBean.OrderBy.SENDER_COUNTRY.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_sender_name", ArrestDocumentBean.OrderBy.SENDER_NAME.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_receiver_address", ArrestDocumentBean.OrderBy.RECEIVER_ADDRESS.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_receiver_name", ArrestDocumentBean.OrderBy.RECEIVER_NAME.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_sync_status", ArrestDocumentBean.OrderBy.SYNC_STATUS.name(), dataProvider, dataView).setVisible(!server);

        //Панель ссылок для постраничной навигации
        filterForm.add(new PagingNavigator("navigator", dataView, "itemsPerPage", getPreferences(), PAGE_NUMBER_KEY));

        filterForm.add(dataView);
        add(filterForm);
    }

    private ArrowOrderByBorder addOrderByBorder(MarkupContainer container, String id, String property, ISortStateLocator stateLocator, final DataView dateView) {
        ArrowOrderByBorder arrowOrderByBorder  = new ArrowOrderByBorder(id, property, stateLocator) {

            @Override
            protected void onSortChanged() {
                dateView.setCurrentPage(0);
            }
        };
        container.add(arrowOrderByBorder);

        return arrowOrderByBorder;
    }

    private ArrestDocumentFilter newArrestDocumentFilter(){
        ArrestDocumentFilter filter = new ArrestDocumentFilter(getLocale(), getSystemLocale());

        if (hasAnyRole(DOCUMENT_DEP_VIEW)) {
            filter.setDepartment(userProfileBean.getCurrentUser().getDepartment());
            filter.setChildDepartments(hasAnyRole(DOCUMENT_DEP_CHILD_VIEW));
            return filter;
        }

        if (hasAnyRole(DOCUMENT_CREATE)) {
            filter.setCreator(userProfileBean.getCurrentUser());
            return filter;
        }

        return filter;
    }

    private boolean canEdit(ArrestDocument ad) {
        User currentUser = userProfileBean.getCurrentUser();

        boolean authorized = hasAnyRole(DOCUMENT_EDIT)
                && currentUser.getId().equals(ad.getCreator().getId());

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
        return authorized;
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        if (hasAnyRole(DOCUMENT_CREATE)) {
            return Arrays.asList((ToolbarButton) new AddDocumentButton(id) {

                @Override
                protected void onClick() {
                    setResponsePage(ArrestDocumentEdit.class);
                }
            });
        }

        return null;
    }
}
