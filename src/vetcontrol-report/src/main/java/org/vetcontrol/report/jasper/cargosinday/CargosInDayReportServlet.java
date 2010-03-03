/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.cargosinday;

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
import org.vetcontrol.report.entity.CargosInDayReportParameter;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.CargosInDayReportDAO;
import org.vetcontrol.report.util.DateConverter;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.report.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.util.jasper.JRCacheableDataSource;
import org.vetcontrol.report.util.jasper.TextExporterConstants;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "CargosInDayReportServlet", urlPatterns = {"/CargosInDayReportServlet"})
@RolesAllowed({SecurityRoles.LOCAL_AND_REGIONAL_REPORT})
public class CargosInDayReportServlet extends HttpServlet {

    public static final String DAY_KEY = "day";
    public static final String DEPARTMENT_KEY = "department";
    @EJB
    private CargosInDayReportDAO reportDAO;
    @EJB
    private LocaleService localeService;
    @EJB
    private DateConverter dateConverter;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);
            if (exportType == null) {
                return;
            }
            Date day = getDay(request);
            Date startDate = DateUtil.getBeginOfDay(day);
            Date endDate = DateUtil.getEndOfDay(day);
            Long departmentId = getDepartment(request);
            Locale reportLocale = localeService.getReportLocale();

            ServletOutputStream servletOutputStream = response.getOutputStream();
            InputStream reportStream = null;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("date", day);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            Map<String, Object> daoParams = new HashMap<String, Object>();
            daoParams.put(CargosInDayReportParameter.START_DATE, startDate);
            daoParams.put(CargosInDayReportParameter.END_DATE, endDate);
            daoParams.put(CargosInDayReportParameter.DEPARTMENT, departmentId);
            JRDataSource dataSource = new JRCacheableDataSource(reportDAO, daoParams, reportLocale,
                    CargosInDayReportDAO.OrderBy.CARGO_TYPE.getName(), true);
            switch (exportType) {
                case PDF:
                    reportStream = getClass().getResourceAsStream("pdf/cargos_in_day_report.jasper");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    response.setContentType("application/pdf");
                    servletOutputStream.flush();
                    servletOutputStream.close();
                    break;
                case TEXT:
                    reportStream = getClass().getResourceAsStream("text/cargos_in_day_report.jasper");
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

    private Date getDay(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(DAY_KEY).trim());
    }

    private Long getDepartment(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(DEPARTMENT_KEY).trim());
    }
}
