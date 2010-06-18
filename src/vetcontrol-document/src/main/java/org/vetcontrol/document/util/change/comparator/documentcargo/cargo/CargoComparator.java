/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator.documentcargo.cargo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.vetcontrol.document.web.pages.DocumentCargoEdit;
import org.vetcontrol.document.util.change.comparator.DocumentComparator;
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.Vehicle;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.util.change.comparator.DateComparator;
import org.vetcontrol.util.change.comparator.PropertyComparator;
import org.vetcontrol.util.change.comparator.SimpleComparator;
import org.vetcontrol.util.change.comparator.LocalizableBookReferenceComparator;

/**
 *
 * @author Artem
 */
public class CargoComparator extends DocumentComparator<Cargo> {

    private static final String RESOURCE_BUNDLE = DocumentCargoEdit.class.getName();

    public CargoComparator(Locale systemLocale) {
        super(Cargo.class, systemLocale);
    }

    @Override
    protected String getResourceBundle() {
       return RESOURCE_BUNDLE;
    }

    @Override
    protected Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = new HashMap<String, Comparator>();

        propertyComparators.put("cargoType", new LocalizableBookReferenceComparator(getString("document.cargo.cargo_type"), getSystemLocale()));
        propertyComparators.put("count", new SimpleComparator<Double>(getString("document.cargo.count")));
        propertyComparators.put("unitType", new LocalizableBookReferenceComparator(getString("document.cargo.unit_type"), getSystemLocale()));
        propertyComparators.put("cargoProducer", new LocalizableBookReferenceComparator(getString("document.cargo.producer"), getSystemLocale()));
        propertyComparators.put("certificateDetails", new SimpleComparator<String>(getString("document.cargo.certificate_detail")));
        propertyComparators.put("certificateDate", new DateComparator(getString("document.cargo.certificate_date"), getSystemLocale()));
        propertyComparators.put("vehicle", new PropertyComparator<Vehicle>(getString("document.cargo.vehicle")) {

            @Override
            public Set<Change> compare(Vehicle oldValue, Vehicle newValue) {
                //Cargo.vehicle association can not be null. Therefore the both oldValue and newValue are not null.
                if (!oldValue.getId().equals(newValue.getId())) {
                    String oldValueAsString = oldValue.getVehicleDetails();
                    String newValueAsString = newValue.getVehicleDetails();
                    Change change = new Change();
                    change.setPropertyName(getPropertyName());
                    change.setOldValue(oldValueAsString);
                    change.setNewValue(newValueAsString);
                    return Collections.singleton(change);
                } else {
                    return Collections.emptySet();
                }
            }
        });

        return propertyComparators;
    }
}
