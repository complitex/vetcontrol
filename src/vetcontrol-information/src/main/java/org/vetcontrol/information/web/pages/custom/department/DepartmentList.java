/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages.custom.department;

import java.io.Serializable;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.book.Property;
import org.vetcontrol.entity.Department;
import org.vetcontrol.information.service.dao.DepartmentBookDAO;
import org.vetcontrol.information.web.util.TruncateUtil;
import org.vetcontrol.information.web.component.BookChoiceRenderer;
import org.vetcontrol.information.web.component.BookPropertyColumn;
import org.vetcontrol.information.web.pages.BookPage;
import org.vetcontrol.service.dao.IBookViewDAO;

/**
 *
 * @author Artem
 */
public class DepartmentList extends BookPage {

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    @EJB(name = "DepartmentBookDAO")
    private DepartmentBookDAO departmentDAO;

    private class ParentDepartmentColumn extends BookPropertyColumn<Department> {

        public ParentDepartmentColumn(Component resourceComponent, IModel<String> displayPropertyModel, Property departmentParentProperty) {
            super(resourceComponent, displayPropertyModel, departmentParentProperty, bookViewDAO, getSystemLocale());
        }

        @Override
        public Component getFilter(String componentId, FilterForm form) {
            return new ChoiceFilter(componentId, new PropertyModel(form.getDefaultModel(), getPropertyExpression()), form,
                    departmentDAO.getFirstAndSecondLevelDepartments(),
                    new BookChoiceRenderer(getProperty(), getSystemLocale(), TruncateUtil.TRUNCATE_SELECT_VALUE_IN_LIST_PAGE), false);
        }
    }

    public DepartmentList(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected IColumn<? extends Serializable> newColumn(Component resourceComponent, IModel<String> displayPropertyModel, Property property) {
        if (property.getName().equals("parent")) {
            return new ParentDepartmentColumn(resourceComponent, displayPropertyModel, property);
        } else {
            return super.newColumn(resourceComponent, displayPropertyModel, property);
        }
    }
}
