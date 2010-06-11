/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util.change.comparator.custom.department;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.wicket.util.string.Strings;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.entity.Change;
import static org.vetcontrol.entity.Change.CollectionModificationStatus.*;
import org.vetcontrol.util.change.comparator.PropertyComparator;

/**
 *
 * @author Artem
 */
public class PassingBorderPointListComparator extends PropertyComparator<List<PassingBorderPoint>> {

    private static final String PASSING_BORDER_POINT_NAME_PROP = "name";

    public PassingBorderPointListComparator(String propertyName) {
        super(propertyName);
    }

    @Override
    public Set<Change> compare(List<PassingBorderPoint> oldValue, List<PassingBorderPoint> newValue) {
        if ((oldValue == null) && (newValue == null)) {
            //property value wasn't changed.
            return Collections.emptySet();
        }

        Set<Change> changes = new HashSet<Change>();

        if (oldValue == null || newValue == null) {
            //property value was changed
            if (oldValue == null) {
                //new value not null.
                for (PassingBorderPoint newBorderPoint : newValue) {
                    Change change = new Change();
                    change.setCollectionProperty(getPropertyName());
                    change.setCollectionModificationStatus(ADDITION);
                    change.setCollectionObjectId(newBorderPoint.getName());
                    changes.add(change);
                }
            } else {
                //old value not null but new value is null.
                throw new UnsupportedOperationException("Impossible case. New value of Department.passingBorderPoints property can't be null.");
            }
            return changes;
        }

        //the both old value and new value are not null.
        for (PassingBorderPoint oldBorderPoint : oldValue) {
            for (PassingBorderPoint newBorderPoint : newValue) {
                if (oldBorderPoint.getId().equals(newBorderPoint.getId())) {
                    if (newBorderPoint.isDisabled() != oldBorderPoint.isDisabled()) {
                        //enable/disable status was changed.
                        Change change = new Change();
                        change.setCollectionProperty(getPropertyName());
                        if (oldBorderPoint.isDisabled()) {
                            change.setCollectionModificationStatus(ENABLING);
                            change.setCollectionObjectId(newBorderPoint.getName());
                        } else {
                            change.setCollectionModificationStatus(DISABLING);
                            change.setCollectionObjectId(oldBorderPoint.getName());
                        }
                        changes.add(change);
                    }
                    if (!Strings.isEqual(oldBorderPoint.getName(), newBorderPoint.getName())) {
                        //modified border point
                        Change change = new Change();
                        change.setCollectionModificationStatus(MODIFICATION);
                        change.setCollectionProperty(getPropertyName());
                        change.setCollectionObjectId(newBorderPoint.getName());
                        change.setPropertyName(PASSING_BORDER_POINT_NAME_PROP);
                        change.setNewValue(newBorderPoint.getName());
                        change.setOldValue(oldBorderPoint.getName());
                        changes.add(change);
                    }
                }
            }
        }

        for (PassingBorderPoint newBorderPoint : newValue) {
            if (isNewBook(newBorderPoint)) {
                //added border point.
                Change change = new Change();
                change.setCollectionProperty(getPropertyName());
                change.setCollectionModificationStatus(ADDITION);
                change.setCollectionObjectId(newBorderPoint.getName());
                changes.add(change);
            }
        }

        return changes;
    }
}
