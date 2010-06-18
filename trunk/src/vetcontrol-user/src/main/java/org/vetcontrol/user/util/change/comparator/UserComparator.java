/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.user.util.change.comparator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.vetcontrol.entity.User;
import org.vetcontrol.user.web.pages.UserEdit;
import org.vetcontrol.util.change.comparator.AbstractEntityComparator;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.util.change.comparator.LocalizableBookReferenceComparator;
import org.vetcontrol.util.change.comparator.PassingBorderPointComparator;
import org.vetcontrol.util.change.comparator.SimpleComparator;
import org.vetcontrol.util.resource.ResourceUtil;

/**
 *
 * @author Artem
 */
public class UserComparator extends AbstractEntityComparator<User> {

    private static final String RESOURCE_BUNDLE = UserEdit.class.getName();

    public UserComparator(Locale systemLocale) {
        super(User.class, systemLocale);
    }

    @Override
    protected Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = new HashMap<String, Comparator>();
        propertyComparators.put("login", new SimpleComparator<String>(getString("user.login")));
        propertyComparators.put("password", new SimpleComparator<String>(getString("user.password")) {

            @Override
            protected String displayPropertyValue(String value) {
                return getString("user_comparator.inaccesible");
            }
        });
        propertyComparators.put("firstName", new SimpleComparator<String>(getString("user.first_name")));
        propertyComparators.put("middleName", new SimpleComparator<String>(getString("user.middle_name")));
        propertyComparators.put("lastName", new SimpleComparator<String>(getString("user.last_name")));
        propertyComparators.put("job", new LocalizableBookReferenceComparator(getString("user.job"), getSystemLocale()));
        propertyComparators.put("department", new LocalizableBookReferenceComparator(getString("user.department"), getSystemLocale()));
        propertyComparators.put("passingBorderPoint", new PassingBorderPointComparator(getString("user.passing_border_point")));
        propertyComparators.put("userGroups", new UserGroupListComparator(getString("user.usergroup"), getSystemLocale()));

        return propertyComparators;
    }

    private String getString(String key) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, key, getSystemLocale());
    }
}
