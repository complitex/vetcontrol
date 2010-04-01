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
    private static List<Class> allBookTypes = Collections.unmodifiableList((List) Arrays.asList(
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
            CountryWithBadEpizooticSituation.class,
            CargoMode.class));

    @SuppressWarnings({"unchecked"})
    public static List<Class> all() {
        return allBookTypes;
    }
}
