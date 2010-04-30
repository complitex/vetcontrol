/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.web.components.PrintButton;
import org.vetcontrol.report.entity.MeatInDayReport;
import org.vetcontrol.report.entity.MeatInDayReportParameter;
import org.vetcontrol.report.service.dao.MeatInDayReportDAO;
import org.vetcontrol.report.util.meatinday.Formatter;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.REGIONAL_REPORT)
public final class MeatInDayReportPage extends TemplatePage {

    @EJB(name = "MeatInDayReportDAO")
    private MeatInDayReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "DateConverter")
    private DateConverter dateConverter;

    public MeatInDayReportPage() {
        init();
    }

    private void init() {
        Date day = getSession().getMetaData(MeatInDayReportForm.DAY_KEY);
        if (day == null) {
            throw new IllegalArgumentException("Day must be specified.");
        }

        final Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        final Locale reportLocale = localeService.getReportLocale();

        add(new Label("title", new ResourceModel("report.title")));
        add(new Label("report.name", new StringResourceModel("report.name", null,
                new Object[]{departmentDAO.getDepartmentName(departmentId, reportLocale), Formatter.formatReportTitleDate(day, reportLocale)})));
        add(new Label("report.header.previousMonth", new StringResourceModel("report.header.previousMonth", null,
                new Object[]{Formatter.formatPreviousMonth(day, reportLocale)})));
        add(new Label("report.header.allCurrentMonth", new StringResourceModel("report.header.allCurrentMonth", null,
                new Object[]{Formatter.formatMonth(day, reportLocale)})));
        add(new Label("report.header.currentDate", new StringResourceModel("report.header.currentDate", null,
                new Object[]{Formatter.formatCurrentDate(day, reportLocale)})));

        Map<String, Object> daoParams = new HashMap<String, Object>();
        daoParams.put(MeatInDayReportParameter.CURRENT_DATE, day);
        daoParams.put(MeatInDayReportParameter.DEPARTMENT, departmentId);
        ListView<MeatInDayReport> list = new ListView<MeatInDayReport>("list", reportDAO.getAll(daoParams, reportLocale)) {

            @Override
            protected void populateItem(ListItem<MeatInDayReport> item) {
                MeatInDayReport report = item.getModelObject();

                if (report.isRootCargoMode() || report.isTotalEntry()) {
                    item.add(new SimpleAttributeModifier("class", "meatInDayBold"));
                }

                item.add(new Label("cargoMode", Formatter.formatCargoMode(report.getCargoModeName(), report.isTotalEntry(),
                        report.isRootCargoMode(), report.isFirstSubCargoMode())));
                item.add(new Label("inPreviousMonth", Formatter.formatCount(report.getInPreviousMonth(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("inCurrentMonth", Formatter.formatCount(report.getInCurrentMonth(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("inCurrentDate", Formatter.formatCount(report.getInCurrentDate(), report.getUnitTypeName(), reportLocale)));
                item.add(new Label("total", Formatter.formatCount(report.getTotal(), report.getUnitTypeName(), reportLocale)));
            }
        };
        add(list);

        IBehavior currentDateAttribute = new SimpleAttributeModifier("name", MeatInDayReportParameter.CURRENT_DATE);

        //pdf parameters
        HiddenField<String> pdfCurrentDate = new HiddenField<String>("pdfCurrentDate", new Model<String>(dateConverter.toString(day)));
        pdfCurrentDate.add(currentDateAttribute);
        add(pdfCurrentDate);

        //text parameters
        HiddenField<String> textCurrentDate = new HiddenField<String>("textCurrentDate", new Model<String>(dateConverter.toString(day)));
        textCurrentDate.add(currentDateAttribute);
        add(textCurrentDate);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new PrintButton(id, ExportType.PDF, "pdfForm"));
        toolbarButtons.add(new PrintButton(id, ExportType.TEXT, "textForm"));
        return toolbarButtons;
    }
}

