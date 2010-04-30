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
import org.odlabs.wiquery.ui.datepicker.DateOption;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.REGIONAL_REPORT)
public final class MeatInDayReportForm extends FormTemplatePage {

    static final MetaDataKey<Date> DAY_KEY = new MetaDataKey<Date>() {
    };

    public MeatInDayReportForm() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        final IModel<Date> dayModel = new Model<Date>(DateUtil.getCurrentDate()) {

            @Override
            public Date getObject() {
                return getSession().getMetaData(DAY_KEY);
            }

            @Override
            public void setObject(Date day) {
                getSession().setMetaData(DAY_KEY, day);
            }
        };

        Form form = new Form("form") {

            @Override
            protected void onSubmit() {
                setResponsePage(MeatInDayReportPage.class);
            }
        };

        DatePicker<Date> day = new DatePicker<Date>("day", dayModel, Date.class);
        day.setMaxDate(new DateOption((short) 0));
        day.setRequired(true);

        form.add(day);
        form.add(new Spacer("spacer"));
        add(form);
    }
}

