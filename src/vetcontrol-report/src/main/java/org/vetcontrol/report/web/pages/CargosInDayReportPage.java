/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.vetcontrol.report.entity.CargosInDayReport;
import org.vetcontrol.report.jasper.cargosinday.CargosInDayReportServlet;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.CargosInDayReportDAO;
import org.vetcontrol.report.util.cargosinday.CellFormatter;
import org.vetcontrol.report.util.cargosinday.DateConverter;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.report.web.components.PrintButton;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.datatable.ArrowOrderByBorder;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.LOCAL_REPORT)
public final class CargosInDayReportPage extends TemplatePage {

    @EJB(name = "CargosInDayReportDAO")
    private CargosInDayReportDAO reportDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "DateConverter")
    private DateConverter dateConverter;
    private static final String PAGE_NUMBER_KEY = CargosInDayReportPage.class.getSimpleName() + "_PAGE_NUMBER";
    private static final String SORT_ORDER_KEY = CargosInDayReportPage.class.getSimpleName() + "_SORT_ORDER_KEY";
    private static final String SORT_PROPERTY_KEY = CargosInDayReportPage.class.getSimpleName() + "_SORT_PROPERTY_KEY";

    public CargosInDayReportPage() {
        init();
    }

    private void init() {
        Date day = getSession().getMetaData(CargosInDayReportForm.DAY_KEY);
        if (day == null) {
            throw new IllegalArgumentException("Day must be specified.");
        }

        final Date startDate = DateUtil.getBeginOfDay(day);
        final Date endDate = DateUtil.getEndOfDay(day);
        final Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        final Locale reportLocale = localeService.getReportLocale();
        final UIPreferences preferences = getPreferences();

        add(new Label("title", new ResourceModel("title")));
        add(new Label("report.name", new StringResourceModel("report.name", null, new Object[]{day})));

        SortableDataProvider<CargosInDayReport> dataProvider = new SortableDataProvider<CargosInDayReport>() {

            private IModel<Integer> sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return reportDAO.size(departmentId, reportLocale, startDate, endDate);
                }
            };

            @Override
            public Iterator<? extends CargosInDayReport> iterator(int first, int count) {
                SortParam sortParam = getSort();
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, sortParam.isAscending());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, sortParam.getProperty());

                return reportDAO.getAll(departmentId, reportLocale, startDate, endDate, first, count, sortParam.getProperty(),
                        sortParam.isAscending()).iterator();
            }

            @Override
            public int size() {
                return sizeModel.getObject();
            }

            @Override
            public IModel<CargosInDayReport> model(CargosInDayReport object) {
                return new Model<CargosInDayReport>(object);
            }
        };
        //sort property and ordering
        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        String sortProperty = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : CargosInDayReportDAO.OrderBy.CARGO_TYPE.getName();

        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
        dataProvider.setSort(sortProperty, asc);

        final DataView<CargosInDayReport> list = new DataView<CargosInDayReport>("list", dataProvider, 1) {

            @Override
            protected void populateItem(Item<CargosInDayReport> item) {
                CargosInDayReport report = item.getModelObject();

                item.add(new Label("cargoTypeName", report.getCargoTypeName()));
                item.add(new Label("cargoProducerName", report.getCargoProducerName()));
                item.add(new Label("cargoReceiverName", report.getCargoReceiverName()));
                item.add(new Label("cargoSenderName", report.getCargoSenderName()));
                item.add(new Label("isCar", CellFormatter.formatExistenceData(report.getCar())));
                item.add(new Label("isShip", CellFormatter.formatExistenceData(report.getShip())));
                item.add(new Label("isContainer", CellFormatter.formatExistenceData(report.getContainer())));
                item.add(new Label("isCarriage", CellFormatter.formatExistenceData(report.getCarriage())));
                item.add(new Label("isAircraft", CellFormatter.formatExistenceData(report.getAircraft())));
                item.add(new Label("count", CellFormatter.formatCountData(report.getCount(), report.getUnitTypeName())));
            }
        };

        addOrderByLink(this, "report.header.cargo_type", CargosInDayReportDAO.OrderBy.CARGO_TYPE.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_producer", CargosInDayReportDAO.OrderBy.CARGO_PRODUCER.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_receiver", CargosInDayReportDAO.OrderBy.CARGO_RECEIVER.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_sender", CargosInDayReportDAO.OrderBy.CARGO_SENDER.getName(), dataProvider, list);

        add(list);
        add(new PagingNavigator("navigator", list, "itemsPerPage", preferences, PAGE_NUMBER_KEY));

        IBehavior dayAttribute = new SimpleAttributeModifier("name", CargosInDayReportServlet.DAY_KEY);

        //pdf parameters
        HiddenField<String> pdfDay = new HiddenField<String>("pdfDay", new Model<String>(dateConverter.toString(day)));
        pdfDay.add(dayAttribute);
        add(pdfDay);

        //text parameters
        HiddenField<String> textDay = new HiddenField<String>("textDay", new Model<String>(dateConverter.toString(day)));
        textDay.add(dayAttribute);
        add(textDay);
    }

    private void addOrderByLink(MarkupContainer parent, String id, String sortProperty, ISortStateLocator sortStateLocator, final IPageable list) {
        parent.add(new ArrowOrderByBorder(id, sortProperty, sortStateLocator) {

            @Override
            protected void onSortChanged() {
                list.setCurrentPage(0);
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new PrintButton(id, ExportType.PDF, "pdfForm"));
        toolbarButtons.add(new PrintButton(id, ExportType.TEXT, "textForm"));
        return toolbarButtons;
    }


}

