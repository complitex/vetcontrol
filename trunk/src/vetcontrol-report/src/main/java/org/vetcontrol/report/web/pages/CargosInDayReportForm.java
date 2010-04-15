/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.odlabs.wiquery.ui.datepicker.DateOption;
import org.vetcontrol.entity.Department;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.web.components.DepartmentPicker;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.DatePicker;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.LOCAL_AND_REGIONAL_REPORT)
public final class CargosInDayReportForm extends FormTemplatePage {

    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    static final MetaDataKey<Date> DAY_KEY = new MetaDataKey<Date>() {
    };
    static final MetaDataKey<Long> DEPARTMENT_KEY = new MetaDataKey<Long>() {
    };

    public CargosInDayReportForm() {
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
        final IModel<Department> department = new Model<Department>() {

            @Override
            public void setObject(Department object) {
                getSession().setMetaData(DEPARTMENT_KEY, object.getId());
            }
        };

        Form form = new Form("form") {

            @Override
            protected void onSubmit() {
                setResponsePage(CargosInDayReportPage.class);
            }
        };

        DatePicker<Date> day = new DatePicker<Date>("day", dayModel, Date.class);
        day.setMaxDate(new DateOption((short) 0));
        day.setRequired(true);

        form.add(day);

        WebMarkupContainer departmentPanel = new WebMarkupContainer("departmentPanel");
        form.add(departmentPanel);
        List<Department> availableDepartments = departmentDAO.getAvailableDepartments();
        Collections.sort(availableDepartments, new Comparator<Department>() {

            @Override
            public int compare(Department d1, Department d2) {
                return d1.getId().compareTo(d2.getId());
            }
        });
        departmentPanel.add(new DepartmentPicker("departmentPicker", department, availableDepartments));

        if (availableDepartments.size() == 1) {
            department.setObject(availableDepartments.get(0));
            departmentPanel.setVisible(false);
        }
        form.add(departmentPanel);
        form.add(new Spacer("spacer"));

        add(form);
    }
}

