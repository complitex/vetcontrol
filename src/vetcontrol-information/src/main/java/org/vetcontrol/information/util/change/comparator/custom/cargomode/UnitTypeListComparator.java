/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change.comparator.custom.cargomode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.vetcontrol.entity.CargoModeUnitType;
import org.vetcontrol.entity.Change;
import org.vetcontrol.util.change.comparator.PropertyComparator;
import static org.vetcontrol.entity.Change.CollectionModificationStatus.*;

/**
 *
 * @author Artem
 */
public class UnitTypeListComparator extends PropertyComparator<List<CargoModeUnitType>> {

    private Locale systemLocale;

    public UnitTypeListComparator(String propertyName, Locale systemLocale) {
        super(propertyName);
        this.systemLocale = systemLocale;
    }

    @Override
    public Set<Change> compare(List<CargoModeUnitType> oldValue, List<CargoModeUnitType> newValue) {
        if (oldValue == null) {
            oldValue = new ArrayList<CargoModeUnitType>();
        }
        if (newValue == null) {
            newValue = new ArrayList<CargoModeUnitType>();
        }

        Set<Change> changes = new HashSet<Change>();

        //the both old value and new value are not null.
        for (CargoModeUnitType oldCmut : oldValue) {
            boolean removed = true;
            for (CargoModeUnitType newCmut : newValue) {
                if (oldCmut.getId().getUnitTypeId().equals(newCmut.getId().getUnitTypeId())) {
                    removed = false;
                }
            }

            if (removed) {
                //removed reference to CargoModeUnitType
                Change change = new Change();
                change.setCollectionModificationStatus(REMOVAL);
                change.setCollectionProperty(getPropertyName());
                change.setCollectionObjectId(oldCmut.getUnitType().getDisplayName(systemLocale, systemLocale));
                changes.add(change);
            }
        }

        for (CargoModeUnitType newCmut : newValue) {
            boolean added = true;
            for (CargoModeUnitType oldCmut : oldValue) {
                if (oldCmut.getId().getUnitTypeId().equals(newCmut.getId().getUnitTypeId())) {
                    added = false;
                }
            }

            if (added) {
                //added reference to CargoModeUnitType
                Change change = new Change();
                change.setCollectionProperty(getPropertyName());
                change.setCollectionModificationStatus(ADDITION);
                change.setCollectionObjectId(newCmut.getUnitType().getDisplayName(systemLocale, systemLocale));
                changes.add(change);
            }
        }

        return changes;
    }
}
