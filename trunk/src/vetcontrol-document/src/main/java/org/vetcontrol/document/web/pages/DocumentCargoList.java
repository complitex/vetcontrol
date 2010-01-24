package org.vetcontrol.document.web.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.vetcontrol.document.service.DocumentCargoBean;
import org.vetcontrol.document.service.DocumentCargoFilter;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.BookmarkablePageLinkPanel;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.AddDocumentButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.*;
import java.util.Locale;

import static org.vetcontrol.document.service.DocumentCargoBean.OrderBy;
import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 12:54:13
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_VIEW, DOCUMENT_DEP_CHILD_VIEW})
public class DocumentCargoList extends TemplatePage{
    private static final String PAGE_NUMBER_KEY = DocumentCargoList.class.getSimpleName()+"_PAGE_NUMBER";
    private static final String SORT_PROPERTY_KEY = DocumentCargoList.class.getSimpleName() + "_SORT_PROPERTY";
    private static final String SORT_ORDER_KEY = DocumentCargoList.class.getSimpleName() + "_SORT_ORDER";
    private static final String FILTER_KEY = DocumentCargoList.class.getSimpleName() + "_FILTER";

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "DocumentBean")
    DocumentCargoBean documentCargoBean;

    @EJB(name = "UserProfileBean")
    UserProfileBean userProfileBean;

    public DocumentCargoList() {
        super();

        final Locale systemLocale = localeDAO.systemLocale();

        add(new Label("title", getString("document.cargo.list.title")));
        add(new Label("header", getString("document.cargo.list.title")));

        add(new FeedbackPanel("messages"));

        final UIPreferences preferences = getPreferences();

        //Фильтр
        DocumentCargoFilter filterObject = preferences.getPreference(UIPreferences.PreferenceType.FILTER,
                FILTER_KEY, DocumentCargoFilter.class);
        if (filterObject == null){
            filterObject = newDocumentCargoFilter();
        }

        final IModel<DocumentCargoFilter> filter = new CompoundPropertyModel<DocumentCargoFilter>(filterObject);
        final Form<DocumentCargoFilter> filterForm = new Form<DocumentCargoFilter>("filter_form", filter);

        Button filter_reset = new Button("filter_reset"){
            @Override
            public void onSubmit() {
                filterForm.clearInput();
                filter.setObject(newDocumentCargoFilter());
            }
        };

        filter_reset.setDefaultFormProcessing(false);
        filterForm.add(filter_reset);

        filterForm.add(new TextField<Integer>("id"));
        filterForm.add(new DropDownChoice<MovementType>("movementType", documentCargoBean.getList(MovementType.class),
                new IChoiceRenderer<MovementType>(){

                    @Override
                    public Object getDisplayValue(MovementType object) {
                        return object.getDisplayName(getLocale(), systemLocale);
                    }

                    @Override
                    public String getIdValue(MovementType object, int index) {
                        return String.valueOf(object.getId());
                    }
                }));

        filterForm.add(new DropDownChoice<VehicleType>("vehicleType", documentCargoBean.getList(VehicleType.class),
                new IChoiceRenderer<VehicleType>(){

                    @Override
                    public Object getDisplayValue(VehicleType object) {
                        return object.getDisplayName(getLocale(), systemLocale);
                    }

                    @Override
                    public String getIdValue(VehicleType object, int index) {
                        return String.valueOf(object.getId());
                    }
                }));
        filterForm.add(new TextField("vehicleDetails"));
        filterForm.add(new TextField("cargoSenderName"));
        filterForm.add(new TextField("cargoReceiverName"));
        filterForm.add(new TextField("cargoProducerName"));

        DatePicker<Date> created = new DatePicker<Date>("created");
        created.setButtonImage("images/calendar.gif");
        created.setButtonImageOnly(true);
        created.setShowOn(DatePicker.ShowOnEnum.BOTH);
        filterForm.add(created);

        //Модель данных списка карточек на груз
        final SortableDataProvider<DocumentCargo> dataProvider = new SortableDataProvider<DocumentCargo>(){
            @Override
            public Iterator<? extends DocumentCargo> iterator(int first, int count) {
                DocumentCargoBean.OrderBy sort = OrderBy.valueOf(getSort().getProperty());
                boolean asc = getSort().isAscending();

                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, getSort().getProperty());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, asc);
                preferences.putPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, filter.getObject());

                return documentCargoBean.getDocumentCargos(filter.getObject(), first, count, sort, asc).iterator();
            }

            @Override
            public int size() {
                return documentCargoBean.getDocumentCargosSize(filter.getObject()).intValue();
            }

            @Override
            public IModel<DocumentCargo> model(DocumentCargo object) {
                return new Model<DocumentCargo>(object);
            }
        };
        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : OrderBy.ID.name();
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
        dataProvider.setSort(sortProp, asc);

        //Таблица документов
        DataView<DocumentCargo> dataView = new DataView<DocumentCargo>("documents", dataProvider, 1){

            @Override
            protected void populateItem(Item<DocumentCargo> item) {
                DocumentCargo dc = item.getModelObject();
                item.add(new BookmarkablePageLinkPanel<DocumentCargo>("id", String.valueOf(dc.getId()),
                        DocumentCargoView.class, new PageParameters("document_cargo_id=" + dc.getId())));
                item.add(new Label("movementType", dc.getMovementType().getDisplayName(getLocale(), systemLocale)));
                item.add(new Label("vehicleType", dc.getVehicleType().getDisplayName(getLocale(), systemLocale)));
                item.add(new Label("vehicleDetails", dc.getVehicleDetails()));
                item.add(new Label("cargoSender", dc.getCargoSender().getDisplayName(getLocale(), systemLocale)));
                item.add(new Label("cargoReceiver", dc.getCargoReceiver().getDisplayName(getLocale(), systemLocale)));
                item.add(new Label("cargoProducer", dc.getCargoProducer().getDisplayName(getLocale(), systemLocale)));
                item.add(new DateLabel("created", new Model<Date>(dc.getCreated()), new StyleDateConverter(true)));
                if (canEdit(dc)){
                    item.add(new BookmarkablePageLinkPanel<DocumentCargo>("action", getString("document.cargo.list.edit"),
                            DocumentCargoEdit.class, new PageParameters("document_cargo_id=" + dc.getId())));
                }else{
                    item.add(new BookmarkablePageLinkPanel<DocumentCargo>("action", getString("document.cargo.list.view"),
                        DocumentCargoView.class, new PageParameters("document_cargo_id=" + dc.getId())));
                }
            }
        };

        //Ссылки для сортировки
        addOrderByBorder(filterForm, "order_id", OrderBy.ID.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_movementType", OrderBy.MOVEMENT_TYPE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_vehicleType", OrderBy.VECHICLE_TYPE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_vehicleDetails", OrderBy.VECHICLE_DETAILS.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_cargoSender", OrderBy.CARGO_SENDER.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_cargoReceiver", OrderBy.CARGO_RECEIVER.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_cargoProducer", OrderBy.CARGO_PRODUCER.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_created", OrderBy.CREATED.name(), dataProvider, dataView);

        //Панель ссылок для постраничной навигации        
        filterForm.add(new PagingNavigator("navigator", dataView, "itemsPerPage", getPreferences(), PAGE_NUMBER_KEY));

        filterForm.add(dataView);
        add(filterForm);
    }

    private void addOrderByBorder(MarkupContainer container, String id, String property, ISortStateLocator stateLocator, final DataView dateView){
        container.add(new OrderByBorder(id, property, stateLocator) {

            @Override
            protected void onSortChanged() {
                dateView.setCurrentPage(0);
            }
        });
    }

    private DocumentCargoFilter newDocumentCargoFilter(){
        DocumentCargoFilter filter = new DocumentCargoFilter(getLocale(),localeDAO.systemLocale());

        if(hasAnyRole(DOCUMENT_DEP_VIEW)){
            filter.setDepartment(userProfileBean.getCurrentUser().getDepartment());
            filter.setChildDepartments(hasAnyRole(DOCUMENT_DEP_CHILD_VIEW));
            return  filter;
        }

        if (hasAnyRole(DOCUMENT_CREATE)){
            filter.setCreator(userProfileBean.getCurrentUser());
            return filter;
        }

        return filter;
    }

    private boolean canEdit(DocumentCargo dc){
        User currentUser = userProfileBean.getCurrentUser();
        
        boolean authorized = hasAnyRole(DOCUMENT_EDIT)
                && currentUser.getId().equals(dc.getCreator().getId());

        if (!authorized && hasAnyRole(DOCUMENT_DEP_EDIT)){
            authorized = currentUser.getDepartment().getId().equals(dc.getCreator().getDepartment().getId());
        }

        if (!authorized && hasAnyRole(DOCUMENT_DEP_CHILD_EDIT)){
            for(Department d = dc.getCreator().getDepartment(); d != null; d = d.getParent()){
                if (d.getId().equals(currentUser.getDepartment().getId())){
                    authorized = true;
                    break;
                }
            }
        }
        return authorized;
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        if (hasAnyRole(DOCUMENT_CREATE)){
            return Arrays.asList( (ToolbarButton) new AddDocumentButton(id) {

                @Override
                protected void onClick() {
                    setResponsePage(DocumentCargoEdit.class);
                }
            });
        }

        return null;
    }

}
