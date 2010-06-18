/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change.comparator.custom.department;

import java.util.Locale;
import java.util.Map;
import org.vetcontrol.entity.Department;
import org.vetcontrol.information.web.pages.custom.department.DepartmentEdit;
import org.vetcontrol.information.util.resource.ResourceUtil;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.information.util.change.comparator.BookComparator;

/**
 *
 * @author Artem
 */
public class DepartmentComparator extends BookComparator<Department> {

    private static final String PASSING_BORDER_POINTS_PROP = "passingBorderPoints";

    public DepartmentComparator(Locale systemLocale) {
        super(Department.class, systemLocale);
    }

    @Override
    public Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = super.getPropertyComparators();

        String localizedPassingBorderPointProp = ResourceUtil.getString(DepartmentEdit.class.getName(), "passingBorderPoints", getSystemLocale(), true);
        propertyComparators.put(PASSING_BORDER_POINTS_PROP, new PassingBorderPointListComparator(localizedPassingBorderPointProp));
        return propertyComparators;
    }
}
