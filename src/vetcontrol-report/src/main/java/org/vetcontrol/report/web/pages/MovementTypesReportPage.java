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
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.vetcontrol.report.entity.MovementTypesReport;
import org.vetcontrol.report.entity.MovementTypesReportParameter;
import org.vetcontrol.report.jasper.movementtypes.MovementTypesReportServlet;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.MovementTypesReportDAO;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.report.util.movementtypes.CellFormatter;
import org.vetcontrol.report.web.components.PrintButton;
import org.vetcontrol.report.web.components.RowNumberLabel;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.service.UIPreferences.PreferenceType;
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
public final class MovementTypesReportPage extends TemplatePage {

    @EJB(name = "MovementTypesReportDAO")
    private MovementTypesReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    private static final String PAGE_NUMBER_KEY = MovementTypesReportPage.class.getSimpleName() + "_PAGE_NUMBER";
    private static final String SORT_ORDER_KEY = MovementTypesReportPage.class.getSimpleName() + "_SORT_ORDER_KEY";

    public MovementTypesReportPage() {
        init();
    }

    private void init() {
        final Integer month = getSession().getMetaData(MovementTypesReportForm.MONTH_KEY);
        if (month == null) {
            throw new IllegalArgumentException("Month must be specified.");
        }
        final Long departmentId = getSession().getMetaData(MovementTypesReportForm.DEPARTMENT_KEY);
        if (departmentId == null) {
            throw new IllegalArgumentException("Department must be specified.");
        }

        final UIPreferences preferences = getPreferences();
        final Locale reportLocale = localeService.getReportLocale();

        final Date startDate = DateUtil.getFirstDateOfYear();
        final Date endDate = DateUtil.getLastDateOfMonth(month);

        add(new Label("title", new ResourceModel("title")));
        add(new Label("report.name", new StringResourceModel("report.name", null,
                new Object[]{DateUtil.getDisplayMonth(month, reportLocale).toLowerCase(), String.valueOf(DateUtil.getCurrentYear()),
                    departmentDAO.getDepartmentName(departmentId, reportLocale)})));
        add(new Label("report.header.all", new StringResourceModel("report.header.all", null, new Object[]{endDate})));
        add(new Label("report.header.inCurrentMonth", new StringResourceModel("report.header.inCurrentMonth", null,
                new Object[]{DateUtil.getDisplayMonth(month, reportLocale).toLowerCase()})));

        SortableDataProvider<MovementTypesReport> dataProvider = new SortableDataProvider<MovementTypesReport>() {

            private Map<String, Object> daoParams = new HashMap<String, Object>();

            {
                daoParams.put(MovementTypesReportParameter.START_DATE, startDate);
                daoParams.put(MovementTypesReportParameter.END_DATE, endDate);
                daoParams.put(MovementTypesReportParameter.DEPARTMENT, departmentId);
            }
            private IModel<Integer> sizeModel = new LoadableDetachableModel<Integer>() {

                @Override
                protected Integer load() {
                    return reportDAO.size(daoParams);
                }
            };

            @Override
            public Iterator<? extends MovementTypesReport> iterator(int first, int count) {
                SortParam sortParam = getSort();
                preferences.putPreference(PreferenceType.SORT_ORDER, SORT_ORDER_KEY, sortParam.isAscending());

                return reportDAO.getAll(daoParams, reportLocale, first, count, null, sortParam.isAscending()).iterator();
            }

            @Override
            public int size() {
                return sizeModel.getObject();
            }

            @Override
            public IModel<MovementTypesReport> model(MovementTypesReport object) {
                return new Model<MovementTypesReport>(object);
            }
        };
        //sort property and ordering
        Boolean sortOrderFromPreferences = preferences.getPreference(PreferenceType.SORT_ORDER, SORT_ORDER_KEY, Boolean.class);
        boolean asc = sortOrderFromPreferences != null ? sortOrderFromPreferences : true;
        dataProvider.setSort("cargoModeName", asc);

        final DataView<MovementTypesReport> list = new DataView<MovementTypesReport>("list", dataProvider, 1) {

            @Override
            protected void populateItem(Item<MovementTypesReport> item) {
                MovementTypesReport report = item.getModelObject();

                item.add(new RowNumberLabel("rowNumber", item));

                item.add(new Label("cargoModeName", report.getCargoModeName()));

                item.add(new Label("export", getFormattedReportData(report.getExport(), report.getUnitTypeName())));
                item.add(new Label("import", getFormattedReportData(report.getImprt(), report.getUnitTypeName())));
                item.add(new Label("transit", getFormattedReportData(report.getTransit(), report.getUnitTypeName())));
                item.add(new Label("importTransit", getFormattedReportData(report.getImportTransit(), report.getUnitTypeName())));

                item.add(new Label("exportInCurrentMonth", getFormattedReportData(report.getExportInCurrentMonth(), report.getUnitTypeName())));
                item.add(new Label("importInCurrentMonth", getFormattedReportData(report.getImprtInCurrentMonth(), report.getUnitTypeName())));
                item.add(new Label("transitInCurrentMonth", getFormattedReportData(report.getTransitInCurrentMonth(), report.getUnitTypeName())));
                item.add(new Label("importTransitInCurrentMonth",
                        getFormattedReportData(report.getImportTransitInCurrentMonth(), report.getUnitTypeName())));
            }
        };

        add(new ArrowOrderByBorder("report.header.cargoMode", "cargoModeName", dataProvider) {

            @Override
            protected void onSortChanged() {
                list.setCurrentPage(0);
            }
        });

        add(list);
        add(new PagingNavigator("navigator", list, "itemsPerPage", preferences, PAGE_NUMBER_KEY));

        IBehavior monthAttribute = new SimpleAttributeModifier("name", MovementTypesReportServlet.MONTH_KEY);
        IBehavior departmentAttribute = new SimpleAttributeModifier("name", MovementTypesReportServlet.DEPARTMENT_KEY);

        //pdf parameters
        HiddenField<Integer> pdfMonth = new HiddenField<Integer>("pdfMonth", new Model<Integer>(month));
        pdfMonth.add(monthAttribute);
        add(pdfMonth);
        HiddenField<Long> pdfDepartment = new HiddenField<Long>("pdfDepartment", new Model<Long>(departmentId));
        pdfDepartment.add(departmentAttribute);
        add(pdfDepartment);

        //text parameters
        HiddenField<Integer> textMonth = new HiddenField<Integer>("textMonth", new Model<Integer>(month));
        textMonth.add(monthAttribute);
        add(textMonth);
        HiddenField<Long> textDepartment = new HiddenField<Long>("textDepartment", new Model<Long>(departmentId));
        textDepartment.add(departmentAttribute);
        add(textDepartment);

    }

    private String getFormattedReportData(Number data, String unitTypeName) {
        return CellFormatter.format(data, unitTypeName);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new PrintButton(id, ExportType.PDF, "pdfForm"));
        toolbarButtons.add(new PrintButton(id, ExportType.TEXT, "textForm"));
        return toolbarButtons;
    }
}

