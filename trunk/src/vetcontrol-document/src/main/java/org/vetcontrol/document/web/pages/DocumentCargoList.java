package org.vetcontrol.document.web.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.vetcontrol.document.service.DocumentCargoBean;
import org.vetcontrol.document.service.DocumentCargoFilter;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.BookmarkablePageLinkPanel;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.AddDocumentButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.*;

import static org.vetcontrol.document.service.DocumentCargoBean.OrderBy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 12:54:13
 */
public class DocumentCargoList extends TemplatePage{
    private final static int ITEMS_ON_PAGE = 13;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    @EJB(name = "DocumentBean")
    DocumentCargoBean documentCargoBean;


    public DocumentCargoList() {
        super();

        final Locale systemLocale = localeDAO.systemLocale();
        final Locale locale = localeDAO.systemLocale();

        add(new Label("title", getString("document.cargo.list.title")));

        //Фильтр
        final IModel<DocumentCargoFilter> filter = new CompoundPropertyModel<DocumentCargoFilter>(new DocumentCargoFilter(locale,systemLocale));
        final Form<DocumentCargoFilter> filterForm = new Form<DocumentCargoFilter>("filter_form", filter);

        filterForm.add(new Button("filter_reset"){
            @Override
            public void onSubmit() {
                filter.setObject(new DocumentCargoFilter(locale,systemLocale));
            }
        });

        filterForm.add(new TextField<Integer>("id"));
        filterForm.add(new DropDownChoice<MovementType>("movementType", documentCargoBean.getList(MovementType.class),
                new IChoiceRenderer<MovementType>(){

                    @Override
                    public Object getDisplayValue(MovementType object) {
                        return object.getDisplayName(locale, systemLocale);
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
                        return object.getDisplayName(locale, systemLocale);
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
        dataProvider.setSort(OrderBy.ID.name(), true);

        //Таблица документов
        DataView<DocumentCargo> dataView = new DataView<DocumentCargo>("documents", dataProvider, ITEMS_ON_PAGE){

            @Override
            protected void populateItem(Item<DocumentCargo> item) {
                DocumentCargo dc = item.getModelObject();
                item.add(new Label("id", String.valueOf(dc.getId())));
                item.add(new Label("movementType", dc.getMovementType().getDisplayName(locale, systemLocale)));
                item.add(new Label("vehicleType", dc.getVehicleType().getDisplayName(locale, systemLocale)));
                item.add(new Label("vehicleDetails", dc.getVehicleDetails()));
                item.add(new Label("cargoSender", dc.getCargoSender().getDisplayName(locale, systemLocale)));
                item.add(new Label("cargoReceiver", dc.getCargoReceiver().getDisplayName(locale, systemLocale)));
                item.add(new Label("cargoProducer", dc.getCargoProducer().getDisplayName(locale, systemLocale)));
                item.add(new DateLabel("created", new Model<Date>(dc.getCreated()), new StyleDateConverter(true)));
                item.add(new BookmarkablePageLinkPanel<DocumentCargo>("action", "Редактировать", DocumentCargoEdit.class,
                        new PageParameters("doc_cargo_id=" + dc.getId())));
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
        filterForm.add(new PagingNavigator("navigator", dataView));

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

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList( (ToolbarButton) new AddDocumentButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(DocumentCargoEdit.class);
            }
        });
    }

}
