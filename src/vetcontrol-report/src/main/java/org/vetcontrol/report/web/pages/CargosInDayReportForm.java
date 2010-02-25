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
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.LOCAL_REPORT)
public final class CargosInDayReportForm extends FormTemplatePage {

    static final MetaDataKey<Date> DAY_KEY = new MetaDataKey<Date>() {
    };

    public CargosInDayReportForm() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));
        
        final IModel<Date> dayModel = new Model<Date>() {

            @Override
            public void setObject(Date day) {
                getSession().setMetaData(DAY_KEY, day);
            }

        };

        Form form = new Form("form"){

            @Override
            protected void onSubmit() {
                setResponsePage(CargosInDayReportPage.class);
            }

        };

        DatePicker<Date> day = new DatePicker<Date>("day", dayModel, Date.class);
        day.setMaxDate(new DateOption((short)0));
        day.setButtonImage("images/calendar.gif");
        day.setButtonImageOnly(true);
        day.setShowOn(DatePicker.ShowOnEnum.BOTH);
        day.setRequired(true);

        form.add(day);
        add(form);

    }
}

