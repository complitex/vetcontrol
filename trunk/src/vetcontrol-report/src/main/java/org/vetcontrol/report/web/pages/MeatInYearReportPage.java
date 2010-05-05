/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.vetcontrol.report.commons.jasper.ExportType;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.components.PrintButton;
import org.vetcontrol.report.entity.MeatInYearReport;
import org.vetcontrol.report.entity.MeatInYearReportParameter;
import org.vetcontrol.report.service.dao.MeatInYearReportDAO;
import org.vetcontrol.report.service.dao.configuration.MeatInYearReportDAOConfig;
import org.vetcontrol.report.util.meat.Formatter;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.REGIONAL_REPORT)
public final class MeatInYearReportPage extends TemplatePage {

    @EJB(name = "MeatInYearReportDAO")
    private MeatInYearReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "DateConverter")
    private DateConverter dateConverter;

    public MeatInYearReportPage() {
        init();
    }

    private void init() {
        Date startDate = getSession().getMetaData(MeatInYearReportForm.START_DATE_KEY);
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must be specified.");
        }
        Date endDate = getSession().getMetaData(MeatInYearReportForm.END_DATE_KEY);
        if (endDate == null) {
            throw new IllegalArgumentException("End date must be specified.");
        }

        final Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        final Locale reportLocale = localeService.getReportLocale();

        add(new Label("title", new ResourceModel("report.title")));
        add(new Label("report.name", new StringResourceModel("report.name", null,
                new Object[]{departmentDAO.getDepartmentName(departmentId, reportLocale), Formatter.formatReportTitleDate(startDate, reportLocale),
        Formatter.formatReportTitleDate(endDate, reportLocale)})));
        add(new Label("report.header.currentDate", new StringResourceModel("report.header.currentDate", null,
                new Object[]{Formatter.formatCurrentDate(endDate, reportLocale)})));

        Map<String, Object> daoParams = MeatInYearReportDAOConfig.configure(startDate, endDate, departmentId);
        ListView<MeatInYearReport> list = new ListView<MeatInYearReport>("list", reportDAO.getAll(daoParams)) {

            @Override
            protected void populateItem(ListItem<MeatInYearReport> item) {
                MeatInYearReport report = item.getModelObject();

                if (report.isRootCargoMode() || report.isTotalEntry()) {
                    item.add(new SimpleAttributeModifier("class", "meatBold"));
                }

                item.add(new Label("cargoMode", Formatter.formatCargoMode(report.getCargoModeName(), report.isTotalEntry(),
                        report.isRootCargoMode(), report.isFirstSubCargoMode())));
                
                item.add(new Label("january", Formatter.formatCount(report.getJanuary(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("february", Formatter.formatCount(report.getFebruary(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("march", Formatter.formatCount(report.getMarch(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("april", Formatter.formatCount(report.getApril(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("may", Formatter.formatCount(report.getMay(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("june", Formatter.formatCount(report.getJune(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("july", Formatter.formatCount(report.getJuly(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("august", Formatter.formatCount(report.getAugust(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("september", Formatter.formatCount(report.getSeptember(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("october", Formatter.formatCount(report.getOctober(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("november", Formatter.formatCount(report.getNovember(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("december", Formatter.formatCount(report.getDecember(), report.getUnitTypeName(), reportLocale)));

                item.add(new Label("currentDate", Formatter.formatCount(report.getCurrentDate(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("total", Formatter.formatCount(report.getTotal(), report.getUnitTypeName(), reportLocale)));
            }
        };
        add(list);

        IBehavior startDateAttribute = new SimpleAttributeModifier("name", MeatInYearReportParameter.START_DATE);
        IBehavior endDateAttribute = new SimpleAttributeModifier("name", MeatInYearReportParameter.END_DATE);

        //pdf parameters
        HiddenField<String> pdfStartDate = new HiddenField<String>("pdfStartDate", new Model<String>(dateConverter.toString(startDate)));
        pdfStartDate.add(startDateAttribute);
        add(pdfStartDate);
        HiddenField<String> pdfEndDate = new HiddenField<String>("pdfEndDate", new Model<String>(dateConverter.toString(endDate)));
        pdfEndDate.add(endDateAttribute);
        add(pdfEndDate);

        //text parameters
        HiddenField<String> textStartDate = new HiddenField<String>("textStartDate", new Model<String>(dateConverter.toString(startDate)));
        textStartDate.add(startDateAttribute);
        add(textStartDate);
        HiddenField<String> textEndDate = new HiddenField<String>("textEndDate", new Model<String>(dateConverter.toString(endDate)));
        textEndDate.add(endDateAttribute);
        add(textEndDate);
    }

     @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new PrintButton(id, ExportType.PDF, "pdfForm"));
        toolbarButtons.add(new PrintButton(id, ExportType.TEXT, "textForm"));
        return toolbarButtons;
    }
}

