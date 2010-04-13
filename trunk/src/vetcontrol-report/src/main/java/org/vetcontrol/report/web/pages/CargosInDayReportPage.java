/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.report.entity.CargosInDayReport;
import org.vetcontrol.report.entity.CargosInDayReportParameter;
import org.vetcontrol.report.jasper.cargosinday.CargosInDayReportServlet;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.CargosInDayReportDAO;
import org.vetcontrol.report.service.dao.DepartmentDAO;
import org.vetcontrol.report.util.cargosinday.Formatter;
import org.vetcontrol.report.util.DateConverter;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.report.web.component.PrintButton;
import org.vetcontrol.service.UIPreferences;
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
@AuthorizeInstantiation(SecurityRoles.LOCAL_AND_REGIONAL_REPORT)
public final class CargosInDayReportPage extends TemplatePage {

    @EJB(name = "CargosInDayReportDAO")
    private CargosInDayReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
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
        final Long departmentId = getSession().getMetaData(CargosInDayReportForm.DEPARTMENT_KEY);
        if (departmentId == null) {
            throw new IllegalArgumentException("Department must be specified.");
        }

        final Date startDate = DateUtil.getBeginOfDay(day);
        final Date endDate = DateUtil.getEndOfDay(day);
        final Locale reportLocale = localeService.getReportLocale();
        final UIPreferences preferences = getPreferences();

        add(new Label("title", new ResourceModel("title")));
        add(new Label("report.name", new StringResourceModel("report.name", null,
                new Object[]{departmentDAO.getDepartmentName(departmentId, reportLocale), Formatter.formatReportTitleDate(day)})));

        SortableDataProvider<CargosInDayReport> dataProvider = new SortableDataProvider<CargosInDayReport>() {

            private Map<String, Object> daoParams = new HashMap<String, Object>();

            {
                daoParams.put(CargosInDayReportParameter.START_DATE, startDate);
                daoParams.put(CargosInDayReportParameter.END_DATE, endDate);
                daoParams.put(CargosInDayReportParameter.DEPARTMENT, departmentId);
            }
            private IModel<Integer> sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return reportDAO.size(daoParams);
                }
            };

            @Override
            public Iterator<? extends CargosInDayReport> iterator(int first, int count) {
                SortParam sortParam = getSort();
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, sortParam.isAscending());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, sortParam.getProperty());

                return reportDAO.getAll(daoParams, reportLocale, first, count, sortParam.getProperty(),
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

                item.add(new Label("rowNumber", String.valueOf(report.getOrder())));

                item.add(new Label("cargoTypeName", report.getCargoTypeName()));
                item.add(new Label("cargoProducerName", report.getCargoProducerName()));
                item.add(new Label("cargoReceiverName", report.getCargoReceiverName()));
                item.add(new Label("cargoSenderName", report.getCargoSenderName()));
                item.add(new Label("isCar", Formatter.formatExistenceData(report.getVehicleType(), VehicleType.CAR)));
                item.add(new Label("isShip", Formatter.formatExistenceData(report.getVehicleType(), VehicleType.SHIP)));
                item.add(new Label("isContainer", Formatter.formatExistenceData(report.getVehicleType(), VehicleType.CONTAINER)));
                item.add(new Label("isCarriage", Formatter.formatExistenceData(report.getVehicleType(), VehicleType.CARRIAGE)));
                item.add(new Label("isAircraft", Formatter.formatExistenceData(report.getVehicleType(), VehicleType.AIRCRAFT)));
                item.add(new Label("count", Formatter.formatCountData(report.getCount(), report.getUnitTypeName(), localeService.getReportLocale())));
            }
        };

        addOrderByLink(this, "report.header.cargo_type", CargosInDayReportDAO.OrderBy.CARGO_TYPE.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_producer", CargosInDayReportDAO.OrderBy.CARGO_PRODUCER.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_receiver", CargosInDayReportDAO.OrderBy.CARGO_RECEIVER.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_sender", CargosInDayReportDAO.OrderBy.CARGO_SENDER.getName(), dataProvider, list);

        add(list);
        add(new PagingNavigator("navigator", list, "itemsPerPage", preferences, PAGE_NUMBER_KEY));

        IBehavior dayAttribute = new SimpleAttributeModifier("name", CargosInDayReportServlet.DAY_KEY);
        IBehavior departmentAttribute = new SimpleAttributeModifier("name", CargosInDayReportServlet.DEPARTMENT_KEY);

        //pdf parameters
        HiddenField<String> pdfDay = new HiddenField<String>("pdfDay", new Model<String>(dateConverter.toString(day)));
        pdfDay.add(dayAttribute);
        add(pdfDay);
        HiddenField<Long> pdfDepartment = new HiddenField<Long>("pdfDepartment", new Model<Long>(departmentId));
        pdfDepartment.add(departmentAttribute);
        add(pdfDepartment);

        //text parameters
        HiddenField<String> textDay = new HiddenField<String>("textDay", new Model<String>(dateConverter.toString(day)));
        textDay.add(dayAttribute);
        add(textDay);
        HiddenField<Long> textDepartment = new HiddenField<Long>("textDepartment", new Model<Long>(departmentId));
        textDepartment.add(departmentAttribute);
        add(textDepartment);
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

