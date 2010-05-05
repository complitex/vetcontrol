/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.document.jasper.arrest;

import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.vetcontrol.entity.ArrestDocument;
import org.vetcontrol.report.document.entity.ArrestDocumentReport;
import org.vetcontrol.report.commons.web.servlet.AbstractReportServlet;
import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 *
 * @author Artem
 */
@WebServlet(name = "ArrestDocumentReportServlet", urlPatterns = {"/ArrestDocumentReportServlet"})
@RolesAllowed({DOCUMENT_CREATE, DOCUMENT_DEP_EDIT, DOCUMENT_DEP_CHILD_EDIT})
public final class ArrestDocumentReportServlet extends AbstractReportServlet {

    public static final String ARREST_DOCUMENT_KEY = "ARREST_DOCUMENT_KEY";
    private static final String ARREST_REPORT_KEY = "ARREST_REPORT_KEY";

    private ArrestDocument getArrestDocument(HttpServletRequest request) throws ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (ArrestDocument) session.getAttribute(ARREST_DOCUMENT_KEY);
        }
        throw new ServletException("Session is fresh but it must contain arrest document.");
    }

    @Override
    protected String getReportName(HttpServletRequest request) {
        return "arrest_document_report";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams)
            throws ServletException {
        daoParams.put(ARREST_REPORT_KEY, new ArrestDocumentReport(getArrestDocument(request), getReportLocale()));
    }

    @Override
    protected JRDataSource configureJRDataSource(Map<String, Object> daoParams) {
        ArrestDocumentReport source = (ArrestDocumentReport) daoParams.get(ARREST_REPORT_KEY);
        return new JRBeanArrayDataSource(new ArrestDocumentReport[]{source});
    }
}
