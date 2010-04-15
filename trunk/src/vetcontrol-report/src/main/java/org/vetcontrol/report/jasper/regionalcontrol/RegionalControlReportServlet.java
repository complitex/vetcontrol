/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.regionalcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.vetcontrol.report.entity.RegionalControlReportParameter;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.RegionalControlReportDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.commons.util.jasper.JRCacheableDataSource;
import org.vetcontrol.report.commons.util.jasper.TextExporterConstants;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "RegionalControlReportServlet", urlPatterns = {"/RegionalControlReportServlet"})
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public class RegionalControlReportServlet extends HttpServlet {

    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";
    @EJB
    private RegionalControlReportDAO reportDAO;
    @EJB
    private LocaleService localeService;
    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;
    @EJB
    private DepartmentDAO departmentDAO;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);
            if (exportType == null) {
                return;
            }
            Date start = getStart(request);
            Date end = getEnd(request);
            Date startDate = DateUtil.getBeginOfDay(start);
            Date endDate = DateUtil.getEndOfDay(end);
            Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
            Locale reportLocale = localeService.getReportLocale();
            String departmentName = departmentDAO.getDepartmentName(departmentId, reportLocale);

            ServletOutputStream servletOutputStream = response.getOutputStream();
            InputStream reportStream = null;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("department", departmentName);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            Map<String, Object> daoParams = new HashMap<String, Object>();
            daoParams.put(RegionalControlReportParameter.START_DATE, startDate);
            daoParams.put(RegionalControlReportParameter.END_DATE, endDate);
            daoParams.put(RegionalControlReportParameter.DEPARTMENT, departmentId);
            JRDataSource dataSource = new JRCacheableDataSource(reportDAO, daoParams, reportLocale,
                    RegionalControlReportDAO.OrderBy.CARGO_ARRIVED.getName(), true);
            switch (exportType) {
                case PDF:
                    reportStream = getClass().getResourceAsStream("pdf/regional_control_report.jasper");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    response.setContentType("application/pdf");
                    servletOutputStream.flush();
                    servletOutputStream.close();
                    break;
                case TEXT:
                    reportStream = getClass().getResourceAsStream("text/regional_control_report.jasper");
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, dataSource);

                    JRTextExporter textExporter = new JRTextExporter();
                    textExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    textExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    textExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, TextExporterConstants.PAGE_WIDTH);
                    textExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, TextExporterConstants.PAGE_HEIGHT);

                    textExporter.exportReport();

                    response.setContentType("text/plain");
                    response.setCharacterEncoding("UTF-8");
                    servletOutputStream.flush();
                    servletOutputStream.close();
                    break;
            }
        } catch (Exception e) {
            // display stack trace in the browser
            //TODO: remove
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            response.setContentType("text/plain");
            response.getOutputStream().print(stringWriter.toString());
        }
    }

    private Date getStart(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(START_DATE_KEY).trim());
    }

    private Date getEnd(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(END_DATE_KEY).trim());
    }
}
