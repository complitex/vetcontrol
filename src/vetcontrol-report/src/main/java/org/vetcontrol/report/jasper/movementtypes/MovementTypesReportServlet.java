/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.movementtypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.report.entity.MovementTypesReportParameter;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.MovementTypesReportDAO;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.commons.util.jasper.JRCacheableDataSource;
import org.vetcontrol.report.commons.util.jasper.TextExporterConstants;
import org.vetcontrol.report.commons.util.servlet.ServletUtil;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MovementTypesReportServlet", urlPatterns = {"/MovementTypesReportServlet"})
@RolesAllowed({SecurityRoles.LOCAL_AND_REGIONAL_REPORT})
public final class MovementTypesReportServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MovementTypesReportServlet.class);
    public static final String MONTH_KEY = "month";
    public static final String DEPARTMENT_KEY = "department";
    private static final String END_DATE_KEY = "endDate";
    private static final String YEAR_KEY = "year";
    @EJB
    private MovementTypesReportDAO reportDAO;
    @EJB
    private DepartmentDAO departmentDAO;
    @EJB
    private LocaleService localeService;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream servletOutputStream = null;
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);
            int month = getMonth(request);
            Long departmentId = getDepartment(request);

            Date startDate = DateUtil.getFirstDateOfYear();
            Date endDate = DateUtil.getLastDateOfMonth(month);
            Locale reportLocale = localeService.getReportLocale();

            String monthAsString = DateUtil.getDisplayMonth(month, reportLocale).toLowerCase();
            String year = String.valueOf(DateUtil.getCurrentYear());
            String departmentName = departmentDAO.getDepartmentName(departmentId, reportLocale);

            servletOutputStream = response.getOutputStream();
            InputStream reportStream = null;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(END_DATE_KEY, endDate);
            params.put(MONTH_KEY, monthAsString);
            params.put(YEAR_KEY, year);
            params.put(DEPARTMENT_KEY, departmentName);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            Map<String, Object> daoParams = new HashMap<String, Object>();
            daoParams.put(MovementTypesReportParameter.START_DATE, startDate);
            daoParams.put(MovementTypesReportParameter.END_DATE, endDate);
            daoParams.put(MovementTypesReportParameter.DEPARTMENT, departmentId);
            JRDataSource dataSource = new JRCacheableDataSource(reportDAO, daoParams, reportLocale, null, true);

            switch (exportType) {
                case PDF:
                    reportStream = getClass().getResourceAsStream("pdf/movement_types_report.jasper");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    response.setContentType("application/pdf");
                    break;
                case TEXT:
                    reportStream = getClass().getResourceAsStream("text/movement_types_report.jasper");
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, dataSource);

                    JRTextExporter textExporter = new JRTextExporter();
                    textExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    textExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    textExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, TextExporterConstants.PAGE_WIDTH);
                    textExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, TextExporterConstants.PAGE_HEIGHT);

                    textExporter.exportReport();

                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    break;
            }
        } catch (Throwable e) {
            String error = ServletUtil.error(e, log);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            if (servletOutputStream == null) {
                servletOutputStream = response.getOutputStream();
            }
            servletOutputStream.print(error);
        } finally {
            if (servletOutputStream != null) {
                servletOutputStream.flush();
                servletOutputStream.close();
            }
        }
    }

    private int getMonth(HttpServletRequest request) {
        return Integer.valueOf(request.getParameter(MONTH_KEY).trim());
    }

    private Long getDepartment(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(DEPARTMENT_KEY).trim());
    }
}
