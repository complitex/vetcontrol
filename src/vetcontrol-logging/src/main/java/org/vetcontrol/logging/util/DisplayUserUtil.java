/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.logging.util;

import org.vetcontrol.entity.User;

/**
 *
 * @author Artem
 */
public final class DisplayUserUtil {

    public static final String SYSTEM_USER_LOGIN = "SYSTEM";

    private DisplayUserUtil() {
    }

    public static String displayUser(User user) {
        String userLogin = SYSTEM_USER_LOGIN;
        if (user != null) {
            userLogin = user.getLogin();
        }
        return userLogin;
    }
}
