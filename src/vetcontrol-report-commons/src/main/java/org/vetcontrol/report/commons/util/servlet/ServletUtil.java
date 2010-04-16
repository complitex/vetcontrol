/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.util.servlet;

import java.util.ResourceBundle;
import org.slf4j.Logger;

/**
 *
 * @author Artem
 */
public final class ServletUtil {

    private static final String ERRORS_BUNDLE_NAME = ServletUtil.class.getPackage().getName() + ".servlet_errors";
    private static final ResourceBundle ERRORS_BUNDLE = ResourceBundle.getBundle(ERRORS_BUNDLE_NAME);

    private ServletUtil() {
    }

    public static String error(Throwable e, Logger log) {
        String errorMessage = ERRORS_BUNDLE.getString("error");
        log.error(errorMessage, e);
        return errorMessage;
    }
}
