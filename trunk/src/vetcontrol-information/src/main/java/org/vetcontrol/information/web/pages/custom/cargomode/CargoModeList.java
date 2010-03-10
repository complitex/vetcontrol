/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages.custom.cargomode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.information.service.dao.CargoModeDAO;
import org.vetcontrol.information.service.dao.CargoModeDAO.OrderBy;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.information.util.web.cargomode.CargoModeFilterBean;
import org.vetcontrol.information.web.component.list.EditPanel;
import org.vetcontrol.information.web.component.list.ShowBooksModePanel;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UIPreferences.PreferenceType;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.book.entity.ShowBooksMode;
import org.vetcontrol.web.component.datatable.ArrowOrderByBorder;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.AddItemButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public class CargoModeList extends TemplatePage {

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "CargoModeDAO")
    private CargoModeDAO cargoModeDAO;
    private static final String PAGE_NUMBER_KEY = CargoModeList.class.getSimpleName() + "_PAGE_NUMBER";
    private static final String SORT_PROPERTY_KEY = CargoModeList.class.getSimpleName() + "_SORT_PROPERTY";
    private static final String SORT_ORDER_KEY = CargoModeList.class.getSimpleName() + "_SORT_ORDER";
    private static final String FILTER_KEY = CargoModeList.class.getSimpleName() + "_FILTER";
    private static final String SHOW_BOOKS_MODE_KEY = CargoModeList.class.getSimpleName() + "_SHOW_BOOKS_MODE";
    static final MetaDataKey<CargoMode> SELECTED_BOOK_ENTRY = new MetaDataKey<CargoMode>() {
    };

    public CargoModeList() {
        init();
    }

    private void init() {
        try {

            //title
            add(new Label("title", new DisplayBookClassModel(CargoMode.class)));
            Label bookName = new Label("bookName", new DisplayBookClassModel(CargoMode.class));

            final UIPreferences preferences = getPreferences();

            ShowBooksMode showBooksModeFromPreferences = preferences.getPreference(PreferenceType.SHOW_BOOKS_MODE,
                    SHOW_BOOKS_MODE_KEY, ShowBooksMode.class);
            final IModel<ShowBooksMode> showBooksModeModel = new Model(showBooksModeFromPreferences != null ? showBooksModeFromPreferences
                    : ShowBooksMode.ENABLED);

            Panel showBooksModePanel = new ShowBooksModePanel("showBooksModePanel", showBooksModeModel);

            //Фильтр
            CargoModeFilterBean filterObject = preferences.getPreference(PreferenceType.FILTER,
                    FILTER_KEY, CargoModeFilterBean.class);
            if (filterObject == null) {
                filterObject = newCargoModeFilterBean();
            }
            final CompoundPropertyModel<CargoModeFilterBean> filterModel = new CompoundPropertyModel<CargoModeFilterBean>(filterObject);
            final Form<CargoModeFilterBean> filterForm = new Form<CargoModeFilterBean>("filterForm", filterModel);
            Button clear = new Button("clear") {

                @Override
                public void onSubmit() {
                    filterForm.clearInput();
                    filterModel.setObject(newCargoModeFilterBean());
                }
            };

            clear.setDefaultFormProcessing(false);
            filterForm.add(bookName);
            filterForm.add(clear);
            filterForm.add(new TextField<String>("name"));
            filterForm.add(new TextField<String>("uktzed"));


            final SortableDataProvider<CargoMode> dataProvider = new SortableDataProvider<CargoMode>() {

                @Override
                public Iterator<CargoMode> iterator(int first, int count) {
                    CargoModeDAO.OrderBy sort = OrderBy.valueOf(getSort().getProperty());
                    boolean asc = getSort().isAscending();

                    preferences.putPreference(PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, getSort().getProperty());
                    preferences.putPreference(PreferenceType.SORT_ORDER, SORT_ORDER_KEY, asc);
                    preferences.putPreference(PreferenceType.FILTER, FILTER_KEY, filterModel.getObject());
                    preferences.putPreference(PreferenceType.SHOW_BOOKS_MODE, SHOW_BOOKS_MODE_KEY, showBooksModeModel.getObject());

                    return cargoModeDAO.getAll(filterModel.getObject(), first, count, sort, asc, getLocale(), showBooksModeModel.getObject()).iterator();
                }

                @Override
                public int size() {
                    return cargoModeDAO.size(filterModel.getObject(), getLocale(), showBooksModeModel.getObject());
                }

                @Override
                public IModel<CargoMode> model(CargoMode object) {
                    return new Model<CargoMode>(object);
                }
            };
            String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
            Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
            String sortProp = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : OrderBy.ID.name();
            boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
            dataProvider.setSort(sortProp, asc);

            DataView<CargoMode> cargoModes = new DataView<CargoMode>("cargoModes", dataProvider, 1) {

                @Override
                protected void populateItem(Item<CargoMode> item) {
                    CargoMode cargoMode = item.getModelObject();
                    item.add(getNameLabel(cargoMode));
                    item.add(getUktzedLabel(cargoMode));
                    item.add(new EditPanel("edit", item.getModel()) {

                        @Override
                        protected void selected(Serializable bean) {
                            goToEditPage(bean);
                        }
                    });
                }
            };
            filterForm.add(bookName);
            filterForm.add(showBooksModePanel);
            filterForm.add(cargoModes);

            addOrderByBorder(filterForm, "nameHeader", OrderBy.NAME.name(), dataProvider, cargoModes);
            addOrderByBorder(filterForm, "uktzedHeader", OrderBy.UKTZED.name(), dataProvider, cargoModes);

            add(filterForm);
            add(new PagingNavigator("navigator", cargoModes, "itemsPerPage", getPreferences(), PAGE_NUMBER_KEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Label getUktzedLabel(CargoMode cargoMode) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < cargoMode.getCargoModeCargoTypes().size(); i++) {
            value.append(cargoMode.getCargoModeCargoTypes().get(i).getCargoType().getCode());
            if (i < cargoMode.getCargoModeCargoTypes().size() - 1) {
                value.append(", ");
            }
        }
        return getLabel("uktzedValue", value.toString() != null ? value.toString() : "");
    }

    private Label getNameLabel(CargoMode cargoMode) {
        String value = cargoMode.getDisplayName(getLocale(), localeDAO.systemLocale());
        return getLabel("nameValue", value);
    }

    private Label getLabel(String id, String text) {
        String title = null;
        if (text.length() > Constants.TEXT_LIMIT) {
            title = text;
            text = text.substring(0, Constants.TEXT_LIMIT);
            text += Constants.CONTINUE;
        }

        Label label = new Label(id, text);
        if (title != null) {
            label.add(new SimpleAttributeModifier("title", title));
        }
        return label;
    }

    private void addOrderByBorder(MarkupContainer container, String id, String property, ISortStateLocator stateLocator, final DataView dateView) {
        container.add(new ArrowOrderByBorder(id, property, stateLocator) {

            @Override
            protected void onSortChanged() {
                dateView.setCurrentPage(0);
            }
        });
    }

    private CargoModeFilterBean newCargoModeFilterBean() {
        return new CargoModeFilterBean();
    }

    private void goToEditPage(Serializable entry) {
        getSession().setMetaData(SELECTED_BOOK_ENTRY, entry);
        setResponsePage(CargoModeEdit.class);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        if (hasAnyRole(SecurityRoles.INFORMATION_EDIT)) {
            List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
            toolbarButtons.add(new AddItemButton(id) {

                @Override
                protected void onClick() {
                    try {
                        CargoMode entry = new CargoMode();
                        goToEditPage(entry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return toolbarButtons;
        } else {
            return null;
        }
    }
}
