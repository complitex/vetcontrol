/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.util.jasper;

import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Artem
 */
public final class ExportTypeUtil {

    private ExportTypeUtil() {
    }

    public static final ExportType getExportType(HttpServletRequest request) {
        String exportType = request.getParameter(ExportType.class.getSimpleName());
        if (Strings.isEmpty(exportType)) {
            return null;
        }
        exportType = exportType.trim().toUpperCase();
        return ExportType.valueOf(exportType);
    }
}
