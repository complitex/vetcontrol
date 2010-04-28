/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.arrest;

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
import org.vetcontrol.report.entity.ArrestReportParameter;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.commons.util.jasper.JRCacheableDataSource;
import org.vetcontrol.report.commons.util.jasper.TextExporterConstants;
import org.vetcontrol.report.commons.util.servlet.ServletUtil;
import org.vetcontrol.report.service.dao.ArrestReportDAO;
import org.vetcontrol.report.util.arrest.ArrestReportType;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "ArrestReportServlet", urlPatterns = {"/ArrestReportServlet"})
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public final class ArrestReportServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ArrestReportServlet.class);
    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";
    public static final String REPORT_TYPE = "reportType";
    private static final String DEPARTMENT_KEY = "department";
    @EJB
    private ArrestReportDAO reportDAO;
    @EJB
    private LocaleService localeService;
    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;
    @EJB
    private DepartmentDAO departmentDAO;
    private static final String REPORT_TEMPLATE_PATH = "org/vetcontrol/report/jasper/arrest/";
    private static final String SIMPLE_REPORT_NAME = "/arrest_report.jasper";
    private static final String EXTENDED_REPORT_NAME = "/extended_arrest_report.jasper";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream servletOutputStream = null;
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);
            Date start = getStart(request);
            Date end = getEnd(request);
            ArrestReportType reportType = getReportType(request);
            String reportName = null;

            switch (reportType) {
                case EXTENDED:
                    reportName = EXTENDED_REPORT_NAME;
                    break;
                case SIMPLE:
                default:
                    reportName = SIMPLE_REPORT_NAME;
                    break;
            }

            Date startDate = DateUtil.getBeginOfDay(start);
            Date endDate = DateUtil.getEndOfDay(end);
            Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
            Locale reportLocale = localeService.getReportLocale();
            String departmentName = departmentDAO.getDepartmentName(departmentId, reportLocale);

            servletOutputStream = response.getOutputStream();
            InputStream reportStream = null;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(START_DATE_KEY, startDate);
            params.put(END_DATE_KEY, endDate);
            params.put(DEPARTMENT_KEY, departmentName);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            Map<String, Object> daoParams = new HashMap<String, Object>();
            daoParams.put(ArrestReportParameter.START_DATE, startDate);
            daoParams.put(ArrestReportParameter.END_DATE, endDate);
            daoParams.put(ArrestReportParameter.DEPARTMENT, departmentId);
            JRDataSource dataSource = new JRCacheableDataSource(reportDAO, daoParams, reportLocale,
                    ArrestReportDAO.OrderBy.ARREST_DATE.getName(), true);
            switch (exportType) {
                case PDF:
                    reportStream = Thread.currentThread().getContextClassLoader().
                            getResourceAsStream(REPORT_TEMPLATE_PATH + "pdf" + reportName);
                    response.setContentType("application/pdf");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    break;
                case TEXT:
                    reportStream = Thread.currentThread().getContextClassLoader().
                            getResourceAsStream(REPORT_TEMPLATE_PATH + "text" + reportName);
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, dataSource);
                    JRTextExporter textExporter = new JRTextExporter();
                    textExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    textExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    textExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, TextExporterConstants.PAGE_WIDTH);
                    textExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, TextExporterConstants.PAGE_HEIGHT);
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    textExporter.exportReport();
                    break;
            }
        } catch (Throwable e) {
            String error = ServletUtil.error(e, log);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            try {
                if (servletOutputStream == null) {
                    servletOutputStream = response.getOutputStream();
                }
                servletOutputStream.print(error);
            } catch (Throwable t) {
                log.error("", t);
            }
        } finally {
            if (servletOutputStream != null) {
                servletOutputStream.flush();
                servletOutputStream.close();
            }
        }
    }

    private Date getStart(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(START_DATE_KEY).trim());
    }

    private Date getEnd(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(END_DATE_KEY).trim());
    }

    private ArrestReportType getReportType(HttpServletRequest request) {
        return ArrestReportType.valueOf(request.getParameter(REPORT_TYPE).trim());
    }
}
