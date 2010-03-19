/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.Date;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.REGIONAL_REPORT)
public final class RegionalControlReportForm extends FormTemplatePage {

    static final MetaDataKey<Date> START_DATE_KEY = new MetaDataKey<Date>() {
    };
    static final MetaDataKey<Date> END_DATE_KEY = new MetaDataKey<Date>() {
    };

    public RegionalControlReportForm() {
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

        final DatePicker<Date> startDate = new DatePicker<Date>("startDate", startDateModel, Date.class);
        startDate.setButtonImage("images/calendar.gif");
        startDate.setButtonImageOnly(true);
        startDate.setShowOn(DatePicker.ShowOnEnum.BOTH);
        startDate.setRequired(true);

        final DatePicker<Date> endDate = new DatePicker<Date>("endDate", endDateModel, Date.class);
        endDate.setButtonImage("images/calendar.gif");
        endDate.setButtonImageOnly(true);
        endDate.setShowOn(DatePicker.ShowOnEnum.BOTH);
        endDate.setRequired(true);

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
                setResponsePage(RegionalControlReportPage.class);
            }
        };

        form.add(startDate);
        form.add(endDate);
        form.add(new Spacer("spacer"));
        add(form);
    }
}

