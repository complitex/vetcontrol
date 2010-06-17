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
import org.vetcontrol.document.util.change.comparator.documentcargo.cargo.CargoComparator;
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.Change;
import org.vetcontrol.util.change.comparator.PropertyComparator;
import static org.vetcontrol.entity.Change.CollectionModificationStatus.*;

/**
 *
 * @author Artem
 */
public class CargoListComparator extends PropertyComparator<List<Cargo>> {

    private final CargoComparator cargoComparator;

    private final Locale systemLocale;

    public CargoListComparator(String propertyName, Locale systemLocale) {
        super(propertyName);
        this.systemLocale = systemLocale;
        this.cargoComparator = new CargoComparator(systemLocale);
    }

    @Override
    public Set<Change> compare(List<Cargo> oldValue, List<Cargo> newValue) {
        if (oldValue == null) {
            oldValue = new ArrayList<Cargo>();
        }

        if (newValue == null) {
            newValue = new ArrayList<Cargo>();
        }

        Set<Change> changes = new HashSet<Change>();

        //the both old value and new value are not null.
        for (Cargo oldCargo : oldValue) {
            boolean removed = true;
            for (Cargo newCargo : newValue) {
                if (oldCargo.getId().equals(newCargo.getId())) {
                    removed = false;

                    Set<Change> currentCargoChanges = cargoComparator.compare(oldCargo, newCargo);
                    //setting collection related properties.
                    for (Change change : currentCargoChanges) {
                        change.setCollectionModificationStatus(MODIFICATION);
                        change.setCollectionProperty(getPropertyName());
                        change.setCollectionObjectId(newCargo.display(systemLocale, systemLocale));
                    }
                    changes.addAll(currentCargoChanges);
                    break;
                }
            }

            if (removed) {
                //removed cargo
                Change change = new Change();
                change.setCollectionModificationStatus(REMOVAL);
                change.setCollectionProperty(getPropertyName());
                change.setCollectionObjectId(oldCargo.display(systemLocale, systemLocale));
                changes.add(change);
            }
        }

        for (Cargo newCargo : newValue) {
            boolean added = true;
            for (Cargo oldCargo : oldValue) {
                if (oldCargo.getId().equals(newCargo.getId())) {
                    added = false;
                    break;
                }
            }

            if (added) {
                //added cargo
                Change change = new Change();
                change.setCollectionProperty(getPropertyName());
                change.setCollectionModificationStatus(ADDITION);
                change.setCollectionObjectId(newCargo.display(systemLocale, systemLocale));
                changes.add(change);
            }
        }

        return changes;
    }
}
