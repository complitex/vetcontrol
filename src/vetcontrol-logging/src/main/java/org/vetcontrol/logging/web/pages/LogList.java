package org.vetcontrol.logging.web.pages;

import org.apache.wicket.MarkupContainer;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Log;
import org.vetcontrol.logging.service.LogFilter;
import org.vetcontrol.logging.service.LogListBean;
import org.vetcontrol.logging.util.DisplayUserUtil;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.datatable.ArrowOrderByBorder;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.ListTemplatePage;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.vetcontrol.logging.web.component.change.DetailsPanel;
import org.vetcontrol.logging.web.component.change.DetailsLink;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.01.2010 10:57:03
 */
@AuthorizeInstantiation({SecurityRoles.LOGGING_VIEW})
public class LogList extends ListTemplatePage {

    private static final String PAGE_NUMBER_KEY = LogList.class.getSimpleName() + "_PAGE_NUMBER";

    private static final String SORT_PROPERTY_KEY = LogList.class.getSimpleName() + "_SORT_PROPERTY";

    private static final String SORT_ORDER_KEY = LogList.class.getSimpleName() + "_SORT_ORDER";

    private static final String FILTER_KEY = LogList.class.getSimpleName() + "_FILTER";

    @EJB(name = "LogListBean")
    private LogListBean logListBean;

    public LogList() {
        super();

        add(new Label("title", new ResourceModel("logging.log.list.title")));
        add(new Label("header", new ResourceModel("logging.log.list.title")));
        add(new FeedbackPanel("messages"));

        final UIPreferences preferences = getPreferences();

        //Фильтр модель
        LogFilter filterObject = preferences.getPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, LogFilter.class);

        if (filterObject == null) {
            filterObject = new LogFilter();
        }

        final IModel<LogFilter> filterModel = new CompoundPropertyModel<LogFilter>(filterObject);

        //Фильтр форма
        final Form<LogFilter> filterForm = new Form<LogFilter>("filter_form", filterModel);
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new LogFilter());
            }
        };
        filterForm.add(filter_reset);

        //Date
        DatePicker<Date> date = new DatePicker<Date>("date");
        filterForm.add(date);

        //Login
        filterForm.add(new TextField<String>("login"));

        //Client
        filterForm.add(new TextField<String>("client"));

        //Controller Class
        filterForm.add(new DropDownChoice<String>("controllerClass", logListBean.getControllerClasses(),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getStringOrKey(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        //Model Class
        filterForm.add(new DropDownChoice<String>("modelClass", logListBean.getModelClasses(),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getStringOrKey(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        //Module
        filterForm.add(new DropDownChoice<Log.MODULE>("module", Arrays.asList(Log.MODULE.values()),
                new IChoiceRenderer<Log.MODULE>() {

                    @Override
                    public Object getDisplayValue(Log.MODULE object) {
                        return getStringOrKey(object.name());
                    }

                    @Override
                    public String getIdValue(Log.MODULE object, int index) {
                        return String.valueOf(object.ordinal());
                    }
                }));

        //Event
        filterForm.add(new DropDownChoice<Log.EVENT>("event", Arrays.asList(Log.EVENT.values()),
                new IChoiceRenderer<Log.EVENT>() {

                    @Override
                    public Object getDisplayValue(Log.EVENT object) {
                        return getStringOrKey(object.name());
                    }

                    @Override
                    public String getIdValue(Log.EVENT object, int index) {
                        return String.valueOf(object.ordinal());
                    }
                }));

        //Status
        filterForm.add(new DropDownChoice<Log.STATUS>("status", Arrays.asList(Log.STATUS.values()),
                new IChoiceRenderer<Log.STATUS>() {

                    @Override
                    public Object getDisplayValue(Log.STATUS object) {
                        return getStringOrKey(object.name());
                    }

                    @Override
                    public String getIdValue(Log.STATUS object, int index) {
                        return String.valueOf(object.ordinal());
                    }
                }));

        //Description
        filterForm.add(new TextField<String>("description"));

        //Модель данных списка элементов журнала событий
        final SortableDataProvider<Log> dataProvider = new SortableDataProvider<Log>() {

            @Override
            public Iterator<? extends Log> iterator(int first, int count) {
                LogListBean.OrderBy sort = LogListBean.OrderBy.valueOf(getSort().getProperty());
                boolean asc = getSort().isAscending();

                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, getSort().getProperty());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, asc);
                preferences.putPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, filterModel.getObject());

                return logListBean.getLogs(filterModel.getObject(), first, count, sort, asc).iterator();
            }

            @Override
            public int size() {
                return logListBean.getLogsCount(filterModel.getObject()).intValue();
            }

            @Override
            public IModel<Log> model(Log object) {
                return new Model<Log>(object);
            }
        };

        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : LogListBean.OrderBy.ID.name();
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : false;
        dataProvider.setSort(sortProp, asc);

        //Таблица журнала событий
        DataView<Log> dataView = new DataView<Log>("logs", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Log> item) {
                Log log = item.getModelObject();
                item.add(DateLabel.forDatePattern("date", new Model<Date>(log.getDate()), "dd.MM.yy HH:mm:ss"));
                item.add(new Label("login", DisplayUserUtil.displayUser(log.getUser())));
                item.add(new Label("client", getClientAsString(log.getClient())));
                item.add(new Label("controllerClass", getStringOrKey(log.getControllerClass())));
                item.add(new Label("modelClass", getStringOrKey(log.getModelClass())));
                item.add(new Label("module", getStringOrKey(log.getModule().name())));
                item.add(new Label("event", getStringOrKey(log.getEvent().name())));
                item.add(new Label("status", getStringOrKey(log.getStatus().name())));


                if (log.getChangeDetails().isEmpty()) {
                    item.add(new Label("description", log.getDescription()));
                    item.add(new EmptyPanel("changeDetailsPanel"));
                } else {
                    item.add(new DetailsLink("description"));
                    item.add(new DetailsPanel("changeDetailsPanel", log.getChangeDetails()));
                }
            }
        };
        filterForm.add(dataView);

        //Сортировка
        addOrderByBorder(filterForm, "order_date", LogListBean.OrderBy.DATE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_login", LogListBean.OrderBy.LOGIN.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_client", LogListBean.OrderBy.CLIENT.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_controllerClass", LogListBean.OrderBy.CONTROLLER_CLASS.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_modelClass", LogListBean.OrderBy.MODEL_CLASS.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_module", LogListBean.OrderBy.MODULE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_event", LogListBean.OrderBy.EVENT.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_status", LogListBean.OrderBy.STATUS.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_description", LogListBean.OrderBy.DESCRIPTION.name(), dataProvider, dataView);

        //Панель ссылок для постраничной навигации
        filterForm.add(new PagingNavigator("navigator", dataView, "itemsPerPage", preferences, PAGE_NUMBER_KEY));
    }

    private String getClientAsString(Client client) {
        if (client == null) {
            return "";
        }
        return client.getId() != null ? String.valueOf(client.getId()) : "";
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
