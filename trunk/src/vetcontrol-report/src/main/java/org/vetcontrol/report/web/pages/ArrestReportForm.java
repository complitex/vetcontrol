/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.Arrays;
import java.util.Date;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.vetcontrol.report.util.arrest.ArrestReportType;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.REGIONAL_REPORT)
public final class ArrestReportForm extends FormTemplatePage {

    static final MetaDataKey<Date> START_DATE_KEY = new MetaDataKey<Date>() {
    };

    static final MetaDataKey<Date> END_DATE_KEY = new MetaDataKey<Date>() {
    };

    static final MetaDataKey<ArrestReportType> REPORT_TYPE = new MetaDataKey<ArrestReportType>() {
    };

    public ArrestReportForm() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        final IModel<Date> startDateModel = new Model<Date>() {

            @Override
            public void setObject(Date date) {
                getSession().setMetaData(START_DATE_KEY, date);
            }
        };
        final IModel<Date> endDateModel = new Model<Date>() {

            @Override
            public void setObject(Date date) {
                getSession().setMetaData(END_DATE_KEY, date);
            }
        };

        IModel<ArrestReportType> reportTypeModel = new Model<ArrestReportType>(ArrestReportType.SIMPLE) {

            @Override
            public ArrestReportType getObject() {
                return getSession().getMetaData(REPORT_TYPE);
            }

            @Override
            public void setObject(ArrestReportType reportType) {
                getSession().setMetaData(REPORT_TYPE, reportType);
            }
        };

        final DatePicker<Date> startDate = new DatePicker<Date>("startDate", startDateModel, Date.class);
        startDate.setRequired(true);

        final DatePicker<Date> endDate = new DatePicker<Date>("endDate", endDateModel, Date.class);
        endDate.setRequired(true);

        DropDownChoice<ArrestReportType> reportType = new DropDownChoice<ArrestReportType>("reportType", reportTypeModel,
                Arrays.asList(ArrestReportType.values()), new EnumChoiceRenderer<ArrestReportType>(this));
        reportType.setRequired(true);

        Form form = new Form("form") {

            @Override
            protected void onValidate() {
                if (endDate.getConvertedInput() != null && startDate.getConvertedInput() != null
                        && endDate.getConvertedInput().before(startDate.getConvertedInput())) {
                    error(getString("endDateBeforStartDate"));
                }
            }

            @Override
            protected void onSubmit() {
                setResponsePage(ArrestReportPage.class);
            }
        };

        form.add(startDate);
        form.add(endDate);
        form.add(reportType);
        form.add(new Spacer("spacer"));
        add(form);
    }
}

