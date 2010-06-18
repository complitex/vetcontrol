/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.user.util.change.comparator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.util.change.comparator.PropertyComparator;
import org.vetcontrol.util.resource.ResourceUtil;
import static org.vetcontrol.entity.Change.CollectionModificationStatus.*;

/**
 *
 * @author Artem
 */
public class UserGroupListComparator extends PropertyComparator<List<UserGroup>> {

    private static final String RESOURCE_BUNDLE = "org.vetcontrol.user.web.pages.package";

    private Locale systemLocale;

    public UserGroupListComparator(String propertyName, Locale systemLocale) {
        super(propertyName);
        this.systemLocale = systemLocale;
    }

    @Override
    public Set<Change> compare(List<UserGroup> oldValue, List<UserGroup> newValue) {
        if (oldValue == null) {
            oldValue = new ArrayList<UserGroup>();
        }

        if (newValue == null) {
            newValue = new ArrayList<UserGroup>();
        }

        Set<Change> changes = new HashSet<Change>();

        //the both old value and new value are not null.
        for (UserGroup oldGroup : oldValue) {
            boolean removed = true;
            for (UserGroup newGroup : newValue) {
                //UserGroup.securityGroup association is not nullable.
                if (oldGroup.getSecurityGroup().equals(newGroup.getSecurityGroup())) {
                    removed = false;
                    break;
                }
            }

            if (removed) {
                //removed reference to UserGroup
                Change change = new Change();
                change.setCollectionModificationStatus(REMOVAL);
                change.setCollectionProperty(getPropertyName());
                change.setCollectionObjectId(getString(oldGroup.getSecurityGroup().name()));
                changes.add(change);
            }
        }

        for (UserGroup newGroup : newValue) {
            boolean added = true;
            for (UserGroup oldGroup : oldValue) {
                if (oldGroup.getSecurityGroup().equals(newGroup.getSecurityGroup())) {
                    added = false;
                    break;
                }
            }

            if (added) {
                //added reference to UserGroup
                Change change = new Change();
                change.setCollectionProperty(getPropertyName());
                change.setCollectionModificationStatus(ADDITION);
                change.setCollectionObjectId(getString(newGroup.getSecurityGroup().name()));
                changes.add(change);
            }
        }

        return changes;
    }

    private String getString(String key) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, key, systemLocale);
    }
}
