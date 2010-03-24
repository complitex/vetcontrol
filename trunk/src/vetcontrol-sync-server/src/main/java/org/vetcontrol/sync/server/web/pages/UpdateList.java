package org.vetcontrol.sync.server.web.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
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
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Update;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.sync.server.service.UpdateBean;
import org.vetcontrol.sync.server.service.UpdateFilter;
import org.vetcontrol.web.component.BookmarkablePageLinkPanel;
import org.vetcontrol.web.component.datatable.ArrowOrderByBorder;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.AddItemButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 16:00:46
 *
 * Класс отображения страницы списка доступных обновлений
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UpdateList extends TemplatePage{
    private static final Logger log = LoggerFactory.getLogger(UpdateList.class);

    private static final String SORT_PROPERTY_KEY = UpdateList.class.getSimpleName() + "_SORT_PROPERTY";
    private static final String SORT_ORDER_KEY = UpdateList.class.getSimpleName() + "_SORT_ORDER";
    private static final String FILTER_KEY = UpdateList.class.getSimpleName() + "_FILTER";
    private static final String PAGE_NUMBER_KEY = UpdateList.class.getSimpleName() + "_PAGE_NUMBER";

    @EJB(name = "UpdateBean")
    private UpdateBean updateBean;

    public UpdateList() {
        super();

        final UIPreferences preferences = getPreferences();

        add(new Label("title", new ResourceModel("sync.server.update.list.title")));
        add(new Label("header", new ResourceModel("sync.server.update.list.title")));
        add(new FeedbackPanel("messages"));

        //Фильтр модель
        UpdateFilter filterObject = preferences.getPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, UpdateFilter.class);
        if (filterObject == null){
            filterObject = new UpdateFilter();
        }

        final IModel<UpdateFilter> filterModel = new CompoundPropertyModel<UpdateFilter>(filterObject);

        //Фильтр форма
        final Form<UpdateFilter> filterForm = new Form<UpdateFilter>("filter_form", filterModel);
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new UpdateFilter());
            }
        };
        filterForm.add(filter_reset);

        //Id
        filterForm.add(new TextField<Long>("id"));

        //Version
        filterForm.add(new TextField<String>("version"));

        //Type
        filterForm.add(new DropDownChoice<Update.TYPE>("type", Arrays.asList(Update.TYPE.values()),
                new IChoiceRenderer<Update.TYPE>() {

                    @Override
                    public Object getDisplayValue(Update.TYPE object) {
                        return getStringOrKey(object.name());
                    }

                    @Override
                    public String getIdValue(Update.TYPE object, int index) {
                        return String.valueOf(object.ordinal());
                    }
                }));

        //Active
        filterForm.add(new DropDownChoice<Boolean>("active", Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                new IChoiceRenderer<Boolean>() {

                    @Override
                    public Object getDisplayValue(Boolean object) {
                        return getStringOrKey(object.toString());
                    }

                    @Override
                    public String getIdValue(Boolean object, int index) {
                        return object.toString();
                    }
                }));

        //Created
        DatePicker<Date> date = new DatePicker<Date>("created");        
        filterForm.add(date);

        //Модель данных списка элементов доступных обновлений
        final SortableDataProvider<Update> dataProvider = new SortableDataProvider<Update>() {

            @Override
            public Iterator<? extends Update> iterator(int first, int count) {
                UpdateBean.OrderBy sort = UpdateBean.OrderBy.valueOf(getSort().getProperty());
                boolean asc = getSort().isAscending();

                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, getSort().getProperty());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, asc);
                preferences.putPreference(UIPreferences.PreferenceType.FILTER, FILTER_KEY, filterModel.getObject());

                return updateBean.getUpdates(filterModel.getObject(), first, count, sort, asc).iterator();
            }

            @Override
            public int size() {
                return updateBean.getUpdatesCount(filterModel.getObject()).intValue();
            }

            @Override
            public IModel<Update> model(Update object) {
                return new Model<Update>(object);
            }
        };

        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : UpdateBean.OrderBy.ID.name();
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : false;
        dataProvider.setSort(sortProp, asc);

         //Таблица доступных обновлений
        DataView<Update> dataView = new DataView<Update>("updates", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Update> item) {
                Update update = item.getModelObject();

                item.add(new Label("id", update.getId().toString()));

                item.add(new Label("version", update.getVersion()));

                item.add(new Label("type", getStringOrKey(update.getType().name())));

                item.add(new Label("active", getStringOrKey(String.valueOf(update.isActive()))));

                item.add(DateLabel.forDatePattern("created", new Model<Date>(update.getCreated()), "dd.MM.yy HH:mm:ss"));

                item.add(new BookmarkablePageLinkPanel<UpdateEdit>("action", getString("sync.server.update.list.edit"),
                            UpdateEdit.class,  new PageParameters("update_id=" + update.getId())));
            }
        };
        filterForm.add(dataView);

        //Сортировка
        addOrderByBorder(filterForm, "order_id", UpdateBean.OrderBy.ID.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_version", UpdateBean.OrderBy.VERSION.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_type", UpdateBean.OrderBy.TYPE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_active", UpdateBean.OrderBy.ACTIVE.name(), dataProvider, dataView);
        addOrderByBorder(filterForm, "order_created", UpdateBean.OrderBy.CREATED.name(), dataProvider, dataView);

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

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        if (hasAnyRole(SecurityRoles.USER_EDIT)) {
            return Arrays.asList((ToolbarButton) new AddItemButton(id) {

                @Override
                protected void onClick() {
                    setResponsePage(UpdateEdit.class);
                }
            });
        }

        return null;
    }
}
