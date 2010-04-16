/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.jasper.arrest;

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
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.ArrestDocument;
import org.vetcontrol.report.commons.entity.ArrestReport;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.jasper.ExportType;
import org.vetcontrol.report.commons.util.jasper.ExportTypeUtil;
import org.vetcontrol.report.commons.util.jasper.TextExporterConstants;
import org.vetcontrol.report.commons.util.servlet.ServletUtil;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.DateUtil;
import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 *
 * @author Artem
 */
@WebServlet(name = "ArrestDocumentReportServlet", urlPatterns = {"/ArrestDocumentReportServlet"})
@RolesAllowed({DOCUMENT_CREATE, DOCUMENT_DEP_EDIT, DOCUMENT_DEP_CHILD_EDIT})
public final class ArrestDocumentReportServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ArrestDocumentReportServlet.class);
    public static final MetaDataKey<ArrestDocument> ARREST_DOCUMENT_KEY = new MetaDataKey<ArrestDocument>() {
    };
    @EJB
    private UserProfileBean userProfileBean;
    @EJB
    private DepartmentDAO departmentDAO;
    @EJB
    private ILocaleDAO localeDAO;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream servletOutputStream = null;
        try {
            ExportType exportType = ExportTypeUtil.getExportType(request);
            Date reportDate = DateUtil.getCurrentDate();
            Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
            Locale reportLocale = request.getLocale();
            Locale systemLocale = localeDAO.systemLocale();
            String departmentName = departmentDAO.getDepartmentName(departmentId, reportLocale);

            servletOutputStream = response.getOutputStream();
            InputStream reportStream = null;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("startDate", reportDate);
            params.put("endDate", reportDate);
            params.put("department", departmentName);
            params.put(JRParameter.REPORT_LOCALE, reportLocale);

            ArrestReport source = new ArrestReport(getArrestDocument(), reportLocale, systemLocale);

            JRDataSource dataSource = new JRBeanArrayDataSource(new ArrestReport[]{source});
            switch (exportType) {
                case PDF:
                    reportStream = getClass().getResourceAsStream("pdf/arrest_report.jasper");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, params, dataSource);
                    response.setContentType("application/pdf");
                    break;
                case TEXT:
                    reportStream = getClass().getResourceAsStream("text/arrest_report.jasper");
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

    private ArrestDocument getArrestDocument() {
        return Session.get().getMetaData(ARREST_DOCUMENT_KEY);
    }
}
