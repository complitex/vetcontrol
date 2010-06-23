/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator.documentcargo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.vetcontrol.document.util.change.comparator.documentcargo.vehicle.VehicleComparator;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.Vehicle;
import org.vetcontrol.util.change.comparator.PropertyComparator;
import static org.vetcontrol.entity.Change.CollectionModificationStatus.*;

/**
 *
 * @author Artem
 */
public class VehicleListComparator extends PropertyComparator<List<Vehicle>> {

    private final VehicleComparator vehicleComparator;

    public VehicleListComparator(String propertyName, Locale systemLocale) {
        super(propertyName);
        vehicleComparator = new VehicleComparator(systemLocale);
    }

    @Override
    public Set<Change> compare(List<Vehicle> oldValue, List<Vehicle> newValue) {
        if (oldValue == null) {
            oldValue = new ArrayList<Vehicle>();
        }

        if (newValue == null) {
            newValue = new ArrayList<Vehicle>();
        }

        Set<Change> changes = new HashSet<Change>();

        //the both old value and new value are not null.
        for (Vehicle oldVehicle : oldValue) {
            boolean removed = true;
            for (Vehicle newVehicle : newValue) {
                //oldVehicle always has not null id.
                if (oldVehicle.getId().equals(newVehicle.getId())) {
                    removed = false;

                    Set<Change> currentCargoChanges = vehicleComparator.compare(oldVehicle, newVehicle);
                    //setting collection related properties.
                    for (Change change : currentCargoChanges) {
                        change.setCollectionModificationStatus(MODIFICATION);
                        change.setCollectionProperty(getPropertyName());
                        change.setCollectionObjectId(newVehicle.getVehicleDetails());
                    }
                    changes.addAll(currentCargoChanges);
                    break;
                }
            }

            if (removed) {
                //removed vehicle
                Change change = new Change();
                change.setCollectionModificationStatus(REMOVAL);
                change.setCollectionProperty(getPropertyName());
                change.setCollectionObjectId(oldVehicle.getVehicleDetails());
                changes.add(change);
            }
        }

        for (Vehicle newVehicle : newValue) {
            boolean added = true;
            for (Vehicle oldVehicle : oldValue) {
                if (oldVehicle.getId().equals(newVehicle.getId())) {
                    added = false;
                    break;
                }
            }

            if (added) {
                //added vehicle
                Change change = new Change();
                change.setCollectionProperty(getPropertyName());
                change.setCollectionModificationStatus(ADDITION);
                change.setCollectionObjectId(newVehicle.getVehicleDetails());
                changes.add(change);
            }
        }

        return changes;
    }
}
