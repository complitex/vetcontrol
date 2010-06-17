/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change;

import org.vetcontrol.information.util.change.comparator.BookComparator;
import java.util.Locale;
import java.util.Set;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.Department;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;
import org.vetcontrol.information.util.change.comparator.custom.cargomode.CargoModeComparator;
import org.vetcontrol.information.util.change.comparator.custom.department.DepartmentComparator;

/**
 *
 * @author Artem
 */
public final class BookChangeManager {

    private BookChangeManager() {
    }

    public static <T> Set<Change> getChanges(T oldBook, T newBook, Locale systemLocale) {
        Class<T> bookType = (Class<T>) oldBook.getClass();
        AbstractEntityComparator<T> bookComparator = new BookComparator<T>(bookType, systemLocale);
        return bookComparator.compare(oldBook, newBook);
    }

    public static Set<Change> getChanges(Department oldDepartment, Department newDepartment, Locale systemLocale) {
        AbstractEntityComparator<Department> departmentComparator = new DepartmentComparator(systemLocale);
        return departmentComparator.compare(oldDepartment, newDepartment);
    }

    public static Set<Change> getChanges(CargoMode oldCargoMode, CargoMode newCargoMode, Locale systemLocale) {
        AbstractEntityComparator<CargoMode> cargoModeComparator = new CargoModeComparator(systemLocale);
        return cargoModeComparator.compare(oldCargoMode, newCargoMode);
    }
}
