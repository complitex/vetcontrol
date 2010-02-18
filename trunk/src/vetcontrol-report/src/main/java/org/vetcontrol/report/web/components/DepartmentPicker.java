/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.components;

import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.entity.Department;
import org.vetcontrol.service.dao.ILocaleDAO;

/**
 *
 * @author Artem
 */
public final class DepartmentPicker extends Panel {

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    public DepartmentPicker(String id, IModel<Department> model, List<Department> availableDeprtments) {
        super(id);

        final Locale systemLocale = localeDAO.systemLocale();
        IChoiceRenderer<Department> renderer = new IChoiceRenderer<Department>() {

            @Override
            public Object getDisplayValue(Department object) {
                return object.getDisplayName(getLocale(), systemLocale);
            }

            @Override
            public String getIdValue(Department object, int index) {
                return String.valueOf(object.getId());
            }
        };
        DropDownChoice<Department> select = new DropDownChoice<Department>("select", model, availableDeprtments, renderer);
        select.setRequired(true);
        add(select);
    }
}
