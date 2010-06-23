/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.document.util.change.comparator.documentcargo.vehicle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.vetcontrol.document.util.change.comparator.DocumentComparator;
import org.vetcontrol.document.web.pages.DocumentCargoEdit;
import org.vetcontrol.entity.Vehicle;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.util.change.comparator.SimpleComparator;

/**
 *
 * @author Artem
 */
public class VehicleComparator extends DocumentComparator<Vehicle>{

     private static final String RESOURCE_BUNDLE = DocumentCargoEdit.class.getName();

    public VehicleComparator(Locale systemLocale) {
        super(Vehicle.class, systemLocale);
    }

    @Override
    protected String getResourceBundle() {
        return RESOURCE_BUNDLE;
    }

    @Override
    protected Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = new HashMap<String, Comparator>();
        propertyComparators.put("vehicleDetails", new SimpleComparator<String>(getString("document.cargo.vehicle_details")));
        return propertyComparators;
    }

}
