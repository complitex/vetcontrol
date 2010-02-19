/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.movementtypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.report.entity.MovementTypesReport;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.MovementTypesReportDAO;
import org.vetcontrol.util.DateUtil;

/**
 *
 * @author Artem
 */
//@WebServlet(name = "MovementTypesReportInPDF", urlPatterns = {"/MovementTypesReportInPDF"})
@Deprecated
public class MovementTypesReportInPDF extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(MovementTypesReportInPDF.class);

    @EJB
    private MovementTypesReportDAO reportDAO;
    @EJB
    private LocaleService localeService;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int month = getMonth(request);
        Long departmentId = getDepartment(request);

        Date startDate = DateUtil.getFirstDateOfYear();
        Date endDate = DateUtil.getLastDateOfMonth(month);
        Locale reportLocale = localeService.getReportLocale();

        ServletOutputStream servletOutputStream = response.getOutputStream();

        try {
            InputStream reportStream = getClass().getResourceAsStream("movement_types_report.jasper");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("endDate", endDate);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, 
                    new JRBeanCollectionDataSource(getAll(departmentId, reportLocale, startDate, endDate)));
            response.setContentType("application/pdf");
            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (Exception e) {
            // display stack trace in the browser
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            response.setContentType("text/plain");
            response.getOutputStream().print(stringWriter.toString());
        }

    }

    private List<MovementTypesReport> getAll(Long departmentId, Locale locale, Date startDate, Date endDate) {
        int size = reportDAO.size(departmentId, locale, startDate, endDate);
        return reportDAO.getAll(departmentId, locale, startDate, endDate, 0, size, true);
    }


    private int getMonth(HttpServletRequest request) {
        return Integer.valueOf(request.getParameter("pdfMonth").trim());
    }

    private Long getDepartment(HttpServletRequest request) {
        return Long.valueOf(request.getParameter("pdfDepartment").trim());
    }

}
