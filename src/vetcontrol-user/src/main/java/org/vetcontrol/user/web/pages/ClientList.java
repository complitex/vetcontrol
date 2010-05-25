package org.vetcontrol.user.web.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.user.service.ClientBean;
import org.vetcontrol.user.service.ClientFilter;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.book.BookDropDownChoice;
import org.vetcontrol.web.component.datatable.ArrowOrderByBorder;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.05.2010 12:49:22
 *
 * Страница списка зарегистрированных клиентов
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class ClientList extends TemplatePage {
    private static final Logger log = LoggerFactory.getLogger(ClientList.class);

    @EJB(name = "user/ClientBean")
    private ClientBean clientBean;

    @EJB(name = "LogBean")
    private LogBean logBean;

    private UIPreferences preferences = getPreferences();
    private static final String SORT_PROPERTY_KEY = ClientList.class.getSimpleName() + "_SORT_PROPERTY";
    private static final String SORT_ORDER_KEY = ClientList.class.getSimpleName() + "_SORT_ORDER";
    private static final String FILTER_KEY = ClientList.class.getSimpleName() + "_FILTER";
    private static final String PAGE_NUMBER_KEY = ClientList.class.getSimpleName() + "_PAGE_NUMBER";

    public ClientList() {
        super();

        add(new Label("title", getString("user.client.list.title")));
        add(new Label("header", getString("user.client.list.title")));

        add(new FeedbackPanel("messages"));

        //Фильтр модель
        ClientFilter filterObject = preferences.getPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, ClientFilter.class);

        if (filterObject == null){
            filterObject = new ClientFilter();
        }

        //Фильтр форма
        final Form<ClientFilter> filterForm = new Form<ClientFilter>("filter_form", new CompoundPropertyModel<ClientFilter>(filterObject));
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterForm.setModelObject(new ClientFilter());
            }
        };
        filterForm.add(filter_reset);

        //Id
        filterForm.add(new TextField<Long>("id"));

        //Passing Border Point
        final DropDownChoice passingBorderPoint = new DropDownChoice<PassingBorderPoint>("passingBorderPoint",
                new LoadableDetachableModel<List<PassingBorderPoint>>(){
                    @Override
                    protected List<PassingBorderPoint> load() {
                        return clientBean.getPassingBorderPoints(filterForm.getModelObject().getDepartment());
                    }
                }, new IChoiceRenderer<PassingBorderPoint>(){
                    @Override
                    public Object getDisplayValue(PassingBorderPoint object) {
                        return object.getName();
                    }

                    @Override
                    public String getIdValue(PassingBorderPoint object, int index) {
                        return object.getId().toString(); 
                    }
                });
        passingBorderPoint.setOutputMarkupId(true);
        filterForm.add(passingBorderPoint);

        //Department
        BookDropDownChoice department = new BookDropDownChoice<Department>("department", clientBean.getDepartments(), getSystemLocale());
        department.add(new AjaxFormComponentUpdatingBehavior("onchange"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(passingBorderPoint);                 
            }
        });
        department.setOutputMarkupId(true);
        filterForm.add(department);

        //Ip
        filterForm.add(new TextField<String>("ip"));

        //Mac
        filterForm.add(new TextField<String>("mac"));

        //Created
        filterForm.add(new DatePicker<Date>("created"));

        //Last Sync
        filterForm.add(new DatePicker<Date>("lastSync"));

        //Version
        filterForm.add(new TextField<String>("version"));

        //Модель данных списка клиентов
        final SortableDataProvider<Client> dataProvider = new SortableDataProvider<Client>() {

            @Override
            public Iterator<? extends Client> iterator(int first, int count) {
                ClientBean.OrderBy sort = ClientBean.OrderBy.valueOf(getSort().getProperty());
                boolean asc = getSort().isAscending();

                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, getSort().getProperty());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, asc);
                preferences.putPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, filterForm.getModelObject());

                return clientBean.getClients(filterForm.getModelObject(), first, count, sort, asc).iterator();
            }

            @Override
            public int size() {
                return clientBean.getClientsCount(filterForm.getModelObject()).intValue();
            }

            @Override
            public IModel<Client> model(Client object) {
                return new Model<Client>(object);
            }
        };

        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : ClientBean.OrderBy.ID.name();
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : false;
        dataProvider.setSort(sortProp, asc);

        //Таблица зарегистрированных клиентов
        DataView<Client> dataView = new DataView<Client>("logs", dataProvider, 1) {
            @Override
            protected void populateItem(Item<Client> item) {
                Client client = item.getModelObject();

                item.add(new Label("id", String.valueOf(client.getId())));
                item.add(new Label("department", client.getDepartment().getDisplayName(getLocale(), getSystemLocale())));
                item.add(new Label("passingBorderPoint", client.getPassingBorderPoint() != null ? client.getPassingBorderPoint().getName() : ""));
                item.add(new Label("ip", client.getIp()));
                item.add(new Label("mac", client.getMac()));
                item.add(DateLabel.forDatePattern("created", new Model<Date>(client.getCreated()), "dd.MM.yy HH:mm:ss"));
                item.add(DateLabel.forDatePattern("lastSync", new Model<Date>(client.getLastSync()), "dd.MM.yy HH:mm:ss"));
                item.add(new Label("version", client.getVersion()));
            }
        };
        filterForm.add(dataView);

        //Сортировка
        addOrderByBorder(filterForm, "order_id", ClientBean.OrderBy.ID.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_department", ClientBean.OrderBy.DEPARTMENT.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_passing_border_point", ClientBean.OrderBy.PASSING_BORDER_POINT.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_ip", ClientBean.OrderBy.IP.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_mac", ClientBean.OrderBy.MAC.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_created", ClientBean.OrderBy.CREATED.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_last_sync", ClientBean.OrderBy.LAST_SYNC.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_version", ClientBean.OrderBy.VERSION.name(), dataProvider, dataView);

        //Панель ссылок для постраничной навигации
        filterForm.add(new PagingNavigator("navigator", dataView, "itemsPerPage", preferences, PAGE_NUMBER_KEY));
    }

    private void addOrderByBorder(MarkupContainer container, String id, String property, ISortStateLocator stateLocator, final DataView dateView) {
        container.add(new ArrowOrderByBorder(id, property, stateLocator) {

            @Override
            protected void onSortChanged() {
                dateView.setCurrentPage(0);
            }
        });
    }
}
