/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util.change;

import org.vetcontrol.information.web.util.change.comparator.BookComparator;
import java.util.Locale;
import java.util.Set;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.Department;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;
import org.vetcontrol.information.web.util.change.comparator.custom.cargomode.CargoModeComparator;
import org.vetcontrol.information.web.util.change.comparator.custom.department.DepartmentComparator;

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
        AbstractEntityComparator<Department> bookComparator = new DepartmentComparator(systemLocale);
        return bookComparator.compare(oldDepartment, newDepartment);
    }

    public static Set<Change> getChanges(CargoMode oldCargoMode, CargoMode newCargoMode, Locale systemLocale) {
        AbstractEntityComparator<CargoMode> cargoModeComparator = new CargoModeComparator(systemLocale);
        return cargoModeComparator.compare(oldCargoMode, newCargoMode);
    }
//    private static Set<Change> interpolateDepartmentChanges(Set<Change> changes) {
//        return ChangeUtil.interpolate(Department.class, changes, new IResourceInterpolator() {
//
//            @Override
//            public Change interpolate(Class rootObjectClass, Change change) {
//                if (change.getCollectionModificationStatus() == Change.CollectionModificationStatus.MODIFICATION) {
//                    //departments have only one collection property - passingBorderPoints list
//                    String lozalizedPropertyName = ResourceUtil.getString(DepartmentEdit.class.getName(),
//                            "passingBorderPoints", Session.get().getLocale(), true);
//                    change.setCollectionProperty(lozalizedPropertyName);
//
//                    if (!Strings.isEmpty(change.getPropertyName())) {
//                        String localizedBorderPointNameProp =
//                                new DisplayPropertyLocalizableModel(getPropertyByName(PassingBorderPoint.class, change.getPropertyName())).getObject();
//                        change.setPropertyName(localizedBorderPointNameProp);
//                    }
//                } else {
//                    if (!Strings.isEmpty(change.getPropertyName())) {
//                        String lozalizedPropertyName =
//                                new DisplayPropertyLocalizableModel(getPropertyByName(rootObjectClass, change.getPropertyName())).getObject();
//                        change.setPropertyName(lozalizedPropertyName);
//                    }
//                }
//                return change;
//            }
//        });
//    }
//
//    private static Set<Change> interpolateBookChanges(Class rootObjectClass, Set<Change> changes) {
//        return ChangeUtil.interpolate(rootObjectClass, changes, new IResourceInterpolator() {
//
//            @Override
//            public Change interpolate(Class rootObjectClass, Change change) {
//                if (!Strings.isEmpty(change.getPropertyName())) {
//                    String lozalizedPropertyName =
//                            new DisplayPropertyLocalizableModel(getPropertyByName(rootObjectClass, change.getPropertyName())).getObject();
//                    change.setPropertyName(lozalizedPropertyName);
//                }
//                return change;
//            }
//        });
//    }
}
