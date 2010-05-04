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
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.vetcontrol.report.entity.ArrestReportParameter;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.jasper.ExportType;
import org.vetcontrol.report.commons.web.components.PrintButton;
import org.vetcontrol.report.entity.ExtendedArrestReport;
import org.vetcontrol.report.jasper.arrest.ArrestReportServlet;
import org.vetcontrol.report.service.dao.ArrestReportDAO;
import org.vetcontrol.report.service.dao.configuration.ArrestReportDAOConfig;
import org.vetcontrol.report.util.arrest.ArrestReportType;
import org.vetcontrol.report.util.arrest.ExtendedFormatter;
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
@AuthorizeInstantiation(SecurityRoles.REGIONAL_REPORT)
public final class ArrestReportPage extends TemplatePage {

    @EJB(name = "ArrestReportDAO")
    private ArrestReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "DateConverter")
    private DateConverter dateConverter;
    private static final String PAGE_NUMBER_KEY = ArrestReportPage.class.getSimpleName() + "_PAGE_NUMBER";
    private static final String SORT_ORDER_KEY = ArrestReportPage.class.getSimpleName() + "_SORT_ORDER_KEY";
    private static final String SORT_PROPERTY_KEY = ArrestReportPage.class.getSimpleName() + "_SORT_PROPERTY_KEY";

    public ArrestReportPage() {
        init();
    }

    private void init() {
        Date start = getSession().getMetaData(ArrestReportForm.START_DATE_KEY);
        if (start == null) {
            throw new IllegalArgumentException("Start date must be specified.");
        }
        Date end = getSession().getMetaData(ArrestReportForm.END_DATE_KEY);
        if (end == null) {
            throw new IllegalArgumentException("End date must be specified.");
        }
        final ArrestReportType reportType = getSession().getMetaData(ArrestReportForm.REPORT_TYPE);
        if (reportType == null) {
            throw new IllegalArgumentException("Arrest report type must be specified.");
        }
        final Date startDate = DateUtil.getBeginOfDay(start);
        final Date endDate = DateUtil.getEndOfDay(end);
        final Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        final Locale reportLocale = localeService.getReportLocale();
        final UIPreferences preferences = getPreferences();

        add(new Label("title", new ResourceModel("report.title")));
        boolean isTheSameDay = DateUtil.isTheSameDay(startDate, endDate);
        String reportName = isTheSameDay ? "report.name1" : "report.name2";
        add(new Label("report.name", new StringResourceModel(reportName, null,
                new Object[]{departmentDAO.getDepartmentName(departmentId, reportLocale),
                    ExtendedFormatter.formatReportTitleDate(startDate, reportLocale),
                    ExtendedFormatter.formatReportTitleDate(endDate, reportLocale)})));

        SortableDataProvider<ExtendedArrestReport> dataProvider = new SortableDataProvider<ExtendedArrestReport>() {

            private Map<String, Object> daoParams = ArrestReportDAOConfig.configure(startDate, endDate, departmentId);
            private IModel<Integer> sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return reportDAO.size(daoParams);
                }
            };

            @Override
            public Iterator<? extends ExtendedArrestReport> iterator(int first, int count) {
                SortParam sortParam = getSort();
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, sortParam.isAscending());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, sortParam.getProperty());

                return reportDAO.getAll(daoParams, first, count, sortParam.getProperty(), sortParam.isAscending()).iterator();
            }

            @Override
            public int size() {
                return sizeModel.getObject();
            }

            @Override
            public IModel<ExtendedArrestReport> model(ExtendedArrestReport object) {
                return new Model<ExtendedArrestReport>(object);
            }
        };
        //sort property and ordering
        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        String sortProperty = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : ArrestReportDAO.OrderBy.ARREST_DATE.getName();

        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
        dataProvider.setSort(sortProperty, asc);

        final DataView<ExtendedArrestReport> list = new DataView<ExtendedArrestReport>("list", dataProvider, 1) {

            @Override
            protected void populateItem(Item<ExtendedArrestReport> item) {
                ExtendedArrestReport report = item.getModelObject();

                item.add(new Label("rowNumber", String.valueOf(report.getOrder())));

                item.add(new Label("department", report.getDepartmentName()));
                item.add(new Label("passingBorderPoint", report.getPassingBorderPointName()));
                item.add(new Label("arrestDate", ExtendedFormatter.formatArrestDate(report.getArrestDate(), reportLocale)));
                item.add(new Label("cargoInfo", ExtendedFormatter.formatCargoInfo(report.getCargoTypeName(), report.getCount(),
                        report.getUnitTypeName(), reportLocale)));
                item.add(new Label("cargoSender", ExtendedFormatter.formatCargoSender(report.getCargoSenderName(), report.getCargoSenderCountry())));
                item.add(new Label("cargoReceiver", ExtendedFormatter.formatCargoReceiver(report.getCargoReceiverName(), report.getCargoReceiverAddress())));
                item.add(new Label("arrestReason", ExtendedFormatter.formatArrestReason(report.getArrestReason(), report.getArrestReasonDetails())));
                item.add(new Label("documentCargoCreated", ExtendedFormatter.formatDocumentCargoCreatedDate(report.getDocumentCargoCreated(), reportLocale)));

                WebMarkupContainer bodyVehicleTypeBlock = new WebMarkupContainer("report.body.vehicleTypeBlock");
                bodyVehicleTypeBlock.setVisible(reportType == ArrestReportType.EXTENDED);
                bodyVehicleTypeBlock.add(new Label("vehicleType", ExtendedFormatter.formatVehicleType(report.getVehicleType(), reportLocale)));
                item.add(bodyVehicleTypeBlock);

                WebMarkupContainer bodyVehicleDetailsBlock = new WebMarkupContainer("report.body.vehicleDetailsBlock");
                bodyVehicleDetailsBlock.setVisible(reportType == ArrestReportType.EXTENDED);
                bodyVehicleDetailsBlock.add(new Label("vehicleDetails", report.getVehicleDetails()));
                item.add(bodyVehicleDetailsBlock);
            }
        };

        addOrderByLink(this, "report.header.arrestDate", ArrestReportDAO.OrderBy.ARREST_DATE.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargoInfo", ArrestReportDAO.OrderBy.CARGO_TYPE.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.department", ArrestReportDAO.OrderBy.DEPARTMENT.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.passingBorderPoint", ArrestReportDAO.OrderBy.PASSING_BORDER_POINT.getName(), dataProvider, list);

        WebMarkupContainer headerVehicleTypeBlock = new WebMarkupContainer("report.header.vehicleTypeBlock");
        add(headerVehicleTypeBlock);
        headerVehicleTypeBlock.setVisible(reportType == ArrestReportType.EXTENDED);
        addOrderByLink(headerVehicleTypeBlock, "report.header.vehicleType", ArrestReportDAO.OrderBy.VEHICLE_TYPE.getName(), dataProvider, list);

        WebMarkupContainer headerVehicleDetailsBlock = new WebMarkupContainer("report.header.vehicleDetailsBlock");
        add(headerVehicleDetailsBlock);
        headerVehicleDetailsBlock.setVisible(reportType == ArrestReportType.EXTENDED);

        add(list);
        add(new PagingNavigator("navigator", list, "itemsPerPage", preferences, PAGE_NUMBER_KEY));

        IBehavior startDateAttribute = new SimpleAttributeModifier("name", ArrestReportServlet.START_DATE_KEY);
        IBehavior endDateAttribute = new SimpleAttributeModifier("name", ArrestReportServlet.END_DATE_KEY);
        IBehavior arrestReportTypeAttribute = new SimpleAttributeModifier("name", ArrestReportServlet.REPORT_TYPE);

        //pdf parameters
        HiddenField<String> pdfStartDate = new HiddenField<String>("pdfStartDate", new Model<String>(dateConverter.toString(start)));
        pdfStartDate.add(startDateAttribute);
        add(pdfStartDate);
        HiddenField<String> pdfEndDate = new HiddenField<String>("pdfEndDate", new Model<String>(dateConverter.toString(end)));
        pdfEndDate.add(endDateAttribute);
        add(pdfEndDate);
        HiddenField<String> pdfArrestReportType = new HiddenField<String>("pdfArrestReportType", new Model<String>(reportType.name()));
        pdfArrestReportType.add(arrestReportTypeAttribute);
        add(pdfArrestReportType);

        //text parameters
        HiddenField<String> textStartDate = new HiddenField<String>("textStartDate", new Model<String>(dateConverter.toString(start)));
        textStartDate.add(startDateAttribute);
        add(textStartDate);
        HiddenField<String> textEndDate = new HiddenField<String>("textEndDate", new Model<String>(dateConverter.toString(end)));
        textEndDate.add(endDateAttribute);
        add(textEndDate);
        HiddenField<String> textArrestReportType = new HiddenField<String>("textArrestReportType", new Model<String>(reportType.name()));
        textArrestReportType.add(arrestReportTypeAttribute);
        add(textArrestReportType);
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

