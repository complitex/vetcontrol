/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.meatinday;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.commons.util.jasper.TextExporterConstants;
import org.vetcontrol.report.commons.util.servlet.ServletUtil;
import org.vetcontrol.report.entity.MeatInDayReport;
import org.vetcontrol.report.entity.MeatInDayReportParameter;
import org.vetcontrol.report.service.dao.MeatInDayReportDAO;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MeatInDayReportServlet", urlPatterns = {"/MeatInDayReportServlet"})
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public final class MeatInDayReportServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MeatInDayReportServlet.class);
    @EJB
    private MeatInDayReportDAO reportDAO;
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
        ServletOutputStream servletOutputStream = null;
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);

            Date currentDate = getCurrentDate(request);
            Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
            Locale reportLocale = localeService.getReportLocale();
            String departmentName = departmentDAO.getDepartmentName(departmentId, reportLocale);

            servletOutputStream = response.getOutputStream();

            Map<String, Object> daoParams = new HashMap<String, Object>();
            daoParams.put(MeatInDayReportParameter.CURRENT_DATE, currentDate);
            daoParams.put(MeatInDayReportParameter.DEPARTMENT, departmentId);
            List<MeatInDayReport> results = reportDAO.getAll(daoParams, reportLocale);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(MeatInDayReportParameter.CURRENT_DATE, currentDate);
            params.put(MeatInDayReportParameter.DEPARTMENT, departmentName);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);
            JRDataSource dataSource = new JRBeanCollectionDataSource(results);
            InputStream reportStream = null;
            switch (exportType) {
                case PDF:
                    reportStream = getClass().getResourceAsStream("pdf/meat_in_day_report.jasper");
                    response.setContentType("application/pdf");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    break;
                case TEXT:
                    reportStream = getClass().getResourceAsStream("text/meat_in_day_report.jasper");
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

    private Date getCurrentDate(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(MeatInDayReportParameter.CURRENT_DATE).trim());
    }
}