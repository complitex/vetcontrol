/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.support;

import org.vetcontrol.entity.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeList;
import org.vetcontrol.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
public class BookTypes {

    public static List<Class<? extends Serializable>> getList() {
        return Collections.unmodifiableList(Arrays.asList(
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
                PassingBorderPoint.class));
    }

    public static Map<Class, Class<? extends TemplatePage>> getCustomBooks(){
        Map<Class, Class<? extends TemplatePage>> customBooks = new HashMap<Class, Class<? extends TemplatePage>>();
        customBooks.put(CargoMode.class, CargoModeList.class);

        return customBooks;
    }
}
