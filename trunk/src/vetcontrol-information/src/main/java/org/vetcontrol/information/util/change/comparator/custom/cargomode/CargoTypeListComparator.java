/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change.comparator.custom.cargomode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.vetcontrol.entity.CargoModeCargoType;
import org.vetcontrol.entity.Change;
import org.vetcontrol.util.change.comparator.PropertyComparator;
import static org.vetcontrol.entity.Change.CollectionModificationStatus.*;

/**
 *
 * @author Artem
 */
public class CargoTypeListComparator extends PropertyComparator<List<CargoModeCargoType>> {

    public CargoTypeListComparator(String propertyName) {
        super(propertyName);
    }

    @Override
    public Set<Change> compare(List<CargoModeCargoType> oldValue, List<CargoModeCargoType> newValue) {
        if (oldValue == null) {
            oldValue = new ArrayList<CargoModeCargoType>();
        }
        if (newValue == null) {
            newValue = new ArrayList<CargoModeCargoType>();
        }

        Set<Change> changes = new HashSet<Change>();

        //the both old value and new value are not null.
        for (CargoModeCargoType oldCmct : oldValue) {
            boolean removed = true;
            for (CargoModeCargoType newCmct : newValue) {
                if (oldCmct.getId().getCargoTypeId().equals(newCmct.getId().getCargoTypeId())) {
                    removed = false;
                }
            }

            if (removed) {
                //removed reference to CargoModeCargoType
                Change change = new Change();
                change.setCollectionModificationStatus(REMOVAL);
                change.setCollectionProperty(getPropertyName());
                change.setCollectionObjectId(oldCmct.getCargoType().getCode());
                changes.add(change);
            }
        }

        for (CargoModeCargoType newCmct : newValue) {
            boolean added = true;
            for (CargoModeCargoType oldCmct : oldValue) {
                if (oldCmct.getId().getCargoTypeId().equals(newCmct.getId().getCargoTypeId())) {
                    added = false;
                    break;
                }
            }

            if (added) {
                //added reference to CargoModeCargoType
                Change change = new Change();
                change.setCollectionProperty(getPropertyName());
                change.setCollectionModificationStatus(ADDITION);
                change.setCollectionObjectId(newCmct.getCargoType().getCode());
                changes.add(change);
            }
        }

        return changes;
    }
}
