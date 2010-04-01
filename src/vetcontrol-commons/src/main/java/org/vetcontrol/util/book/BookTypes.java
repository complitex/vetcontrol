/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.book;

import org.vetcontrol.entity.*;

import java.util.*;

/**
 *
 * @author Artem
 */
public final class BookTypes {

    private BookTypes() {
    }
    private static List<Class> commonBookTypes = Collections.unmodifiableList((List) Arrays.asList(
            Department.class,
            CountryBook.class,
            RegisteredProducts.class,
            CustomsPoint.class,
            MovementType.class,
            CargoProducer.class,
            UnitType.class,
            CargoType.class,
            Job.class,
            ArrestReason.class,
            CountryWithBadEpizooticSituation.class));
    private static List<Class> customBookTypes = Collections.unmodifiableList((List) Arrays.asList(
            CargoMode.class));
    private static List<Class> allBookTypes;

    static {
        List<Class> all = new ArrayList<Class>();
        all.addAll(commonBookTypes);
        all.addAll(customBookTypes);
        allBookTypes = Collections.unmodifiableList(all);
    }

    public static List<Class> all() {
        return allBookTypes;
    }

    public static List<Class> common() {
        return commonBookTypes;
    }

    public static List<Class> custom() {
        return customBookTypes;
    }
}
