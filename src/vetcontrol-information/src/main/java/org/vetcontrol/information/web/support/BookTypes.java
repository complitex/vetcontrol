/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.support;

import org.vetcontrol.entity.*;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeList;
import org.vetcontrol.web.template.TemplatePage;

import java.util.*;

/**
 *
 * @author Artem
 */
public class BookTypes {

    @SuppressWarnings({"unchecked"})
    public static List<Class> getList() {
        return Collections.unmodifiableList((List) Arrays.asList(
                Department.class,
                CountryBook.class,
                RegisteredProducts.class,
                VehicleType.class,
                CargoReceiver.class,
                CargoSender.class,
                CustomsPoint.class,
                MovementType.class,
                CargoProducer.class,
                UnitType.class,
                CargoType.class,
                Job.class,
                Prohibition.class,
                Tariff.class,
                ArrestReason.class,
                AddressBook.class,
                PassingBorderPoint.class,
                CountryWithBadEpizooticSituation.class));
    }

    public static Map<Class, Class<? extends TemplatePage>> getCustomBooks() {
        Map<Class, Class<? extends TemplatePage>> customBooks = new HashMap<Class, Class<? extends TemplatePage>>();
        customBooks.put(CargoMode.class, CargoModeList.class);

        return customBooks;
    }
}
