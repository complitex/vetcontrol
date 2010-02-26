/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.pages;

import java.util.Collections;
import java.util.Comparator;
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
import org.vetcontrol.entity.Department;
import org.vetcontrol.report.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.MovementTypesReportDAO;
import org.vetcontrol.report.util.movementtypes.Month;
import org.vetcontrol.report.web.components.DepartmentPicker;
import org.vetcontrol.report.web.components.MonthPicker;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.LOCAL_AND_REGIONAL_REPORT)
public final class MovementTypesReportForm extends FormTemplatePage {

    @EJB(name = "MovementTypesReportDAO")
    private MovementTypesReportDAO reportDAO;
    @EJB(name = "DepartmentDAO")
    private DepartmentDAO departmentDAO;
    static final MetaDataKey<Integer> MONTH_KEY = new MetaDataKey<Integer>() {
    };
    static final MetaDataKey<Long> DEPARTMENT_KEY = new MetaDataKey<Long>() {
    };

    public MovementTypesReportForm() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        final IModel<Month> month = new Model<Month>() {

            @Override
            public void setObject(Month object) {
                getSession().setMetaData(MONTH_KEY, object.getNumber());
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
                setResponsePage(MovementTypesReportPage.class);
            }
        };
        form.add(new MonthPicker("monthPicker", month));

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
        add(form);

    }
}

