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
import org.vetcontrol.report.entity.RegionalControlReport;
import org.vetcontrol.report.entity.RegionalControlReportParameter;
import org.vetcontrol.report.jasper.regionalcontrol.RegionalControlReportServlet;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.RegionalControlReportDAO;
import org.vetcontrol.report.util.DateConverter;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.report.util.regionalcontrol.CellFormatter;
import org.vetcontrol.report.web.components.PrintButton;
import org.vetcontrol.report.web.components.RowNumberLabel;
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
public final class RegionalControlReportPage extends TemplatePage {

    @EJB(name = "RegionalControlReportDAO")
    private RegionalControlReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "DateConverter")
    private DateConverter dateConverter;
    private static final String PAGE_NUMBER_KEY = RegionalControlReportPage.class.getSimpleName() + "_PAGE_NUMBER";
    private static final String SORT_ORDER_KEY = RegionalControlReportPage.class.getSimpleName() + "_SORT_ORDER_KEY";
    private static final String SORT_PROPERTY_KEY = RegionalControlReportPage.class.getSimpleName() + "_SORT_PROPERTY_KEY";

    public RegionalControlReportPage() {
        init();
    }

    private void init() {
        Date start = getSession().getMetaData(RegionalControlReportForm.START_DATE_KEY);
        if (start == null) {
            throw new IllegalArgumentException("Start date must be specified.");
        }
        Date end = getSession().getMetaData(RegionalControlReportForm.END_DATE_KEY);
        if (end == null) {
            throw new IllegalArgumentException("End date must be specified.");
        }

        final Date startDate = DateUtil.getBeginOfDay(start);
        final Date endDate = DateUtil.getEndOfDay(end);
        final Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        final Locale reportLocale = localeService.getReportLocale();
        final UIPreferences preferences = getPreferences();

        add(new Label("title", new ResourceModel("title")));
        add(new Label("report.name", new StringResourceModel("report.name", null,
                new Object[]{departmentDAO.getDepartmentName(departmentId, reportLocale), startDate, endDate})));

        SortableDataProvider<RegionalControlReport> dataProvider = new SortableDataProvider<RegionalControlReport>() {

            private Map<String, Object> daoParams = new HashMap<String, Object>();

            {
                daoParams.put(RegionalControlReportParameter.START_DATE, startDate);
                daoParams.put(RegionalControlReportParameter.END_DATE, endDate);
                daoParams.put(RegionalControlReportParameter.DEPARTMENT, departmentId);
            }
            private IModel<Integer> sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return reportDAO.size(daoParams);
                }
            };

            @Override
            public Iterator<? extends RegionalControlReport> iterator(int first, int count) {
                SortParam sortParam = getSort();
                preferences.putPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, sortParam.isAscending());
                preferences.putPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, sortParam.getProperty());

                return reportDAO.getAll(daoParams, reportLocale, first, count, sortParam.getProperty(), sortParam.isAscending()).iterator();
            }

            @Override
            public int size() {
                return sizeModel.getObject();
            }

            @Override
            public IModel<RegionalControlReport> model(RegionalControlReport object) {
                return new Model<RegionalControlReport>(object);
            }
        };
        //sort property and ordering
        String sortPropertyFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_PROPERTY, SORT_PROPERTY_KEY, String.class);
        String sortProperty = sortPropertyFromPreferences != null ? sortPropertyFromPreferences : RegionalControlReportDAO.OrderBy.CARGO_ARRIVED.getName();

        Boolean sortOrderFromPreferences = preferences.getPreference(UIPreferences.PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
        dataProvider.setSort(sortProperty, asc);

        final DataView<RegionalControlReport> list = new DataView<RegionalControlReport>("list", dataProvider, 1) {

            @Override
            protected void populateItem(Item<RegionalControlReport> item) {
                RegionalControlReport report = item.getModelObject();

                item.add(new RowNumberLabel("rowNumber", item));

                item.add(new Label("cargoArrived", CellFormatter.formatCargoArrived(report.getCargoArrived(), reportLocale)));
                item.add(new Label("cargoProducerName", report.getCargoProducerName()));
                item.add(new Label("cargoReceiverName", report.getCargoReceiverName()));
                item.add(new Label("cargoTypeName", CellFormatter.formatCargoType(report.getCargoTypeName(), report.getCargoTypeCode())));
                item.add(new Label("count", CellFormatter.formatCount(report.getCount(), report.getUnitTypeName())));
                item.add(new Label("movementType", report.getMovementTypeName()));
            }
        };

        addOrderByLink(this, "report.header.cargoArrived", RegionalControlReportDAO.OrderBy.CARGO_ARRIVED.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_producer", RegionalControlReportDAO.OrderBy.CARGO_PRODUCER.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_receiver", RegionalControlReportDAO.OrderBy.CARGO_RECEIVER.getName(), dataProvider, list);
        addOrderByLink(this, "report.header.cargo_type", RegionalControlReportDAO.OrderBy.CARGO_TYPE.getName(), dataProvider, list);

        add(list);
        add(new PagingNavigator("navigator", list, "itemsPerPage", preferences, PAGE_NUMBER_KEY));

        IBehavior startDateAttribute = new SimpleAttributeModifier("name", RegionalControlReportServlet.START_DATE_KEY);
        IBehavior endDateAttribute = new SimpleAttributeModifier("name", RegionalControlReportServlet.END_DATE_KEY);

        //pdf parameters
        HiddenField<String> pdfStartDate = new HiddenField<String>("pdfStartDate", new Model<String>(dateConverter.toString(start)));
        pdfStartDate.add(startDateAttribute);
        add(pdfStartDate);
        HiddenField<String> pdfEndDate = new HiddenField<String>("pdfEndDate", new Model<String>(dateConverter.toString(end)));
        pdfEndDate.add(endDateAttribute);
        add(pdfEndDate);

        //text parameters
        HiddenField<String> textStartDate = new HiddenField<String>("textStartDate", new Model<String>(dateConverter.toString(start)));
        textStartDate.add(startDateAttribute);
        add(textStartDate);
        HiddenField<String> textEndDate = new HiddenField<String>("textEndDate", new Model<String>(dateConverter.toString(end)));
        textEndDate.add(endDateAttribute);
        add(textEndDate);
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

