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
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.vetcontrol.report.entity.MovementTypesReport;
import org.vetcontrol.report.service.LocaleService;
import org.vetcontrol.report.service.dao.MovementTypesReportDAO;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.report.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.util.jasper.TextExporterConstants;
import org.vetcontrol.util.DateUtil;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MovementTypesReportServlet", urlPatterns = {"/MovementTypesReportServlet"})
public class MovementTypesReportServlet extends HttpServlet {

    public static final String MONTH_KEY = "month";
    public static final String DEPARTMENT_KEY = "department";
    @EJB
    private MovementTypesReportDAO reportDAO;
    @EJB
    private LocaleService localeService;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);
            if (exportType == null) {
                return;
            }
            int month = getMonth(request);
            Long departmentId = getDepartment(request);

            Date startDate = DateUtil.getFirstDateOfYear();
            Date endDate = DateUtil.getLastDateOfMonth(month);
            Locale reportLocale = localeService.getReportLocale();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            InputStream reportStream = getClass().getResourceAsStream("movement_types_report.jasper");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("endDate", endDate);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            JRDataSource dataSource = new JRBeanCollectionDataSource(getAll(departmentId, reportLocale, startDate, endDate));

            switch (exportType) {
                case PDF:
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    response.setContentType("application/pdf");
                    servletOutputStream.flush();
                    servletOutputStream.close();
                    break;
                case TEXT:
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

    private List<MovementTypesReport> getAll(Long departmentId, Locale locale, Date startDate, Date endDate) {
        int size = reportDAO.size(departmentId, locale, startDate, endDate);
        return reportDAO.getAll(departmentId, locale, startDate, endDate, 0, size, true);
    }

    private int getMonth(HttpServletRequest request) {
        return Integer.valueOf(request.getParameter(MONTH_KEY).trim());
    }

    private Long getDepartment(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(DEPARTMENT_KEY).trim());
    }
}
