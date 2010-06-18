/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.user.util.change;

import java.util.Locale;
import java.util.Set;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.User;
import org.vetcontrol.user.util.change.comparator.UserComparator;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;

/**
 *
 * @author Artem
 */
public final class UserChangeManager {

    private UserChangeManager() {
    }

    public static Set<Change> getChanges(User oldUser, User newUser, Locale systemLocale) {
        AbstractEntityComparator<User> userComparator = new UserComparator(systemLocale);
        return userComparator.compare(oldUser, newUser);
    }
}
