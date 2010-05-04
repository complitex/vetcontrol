/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.jasper.ExportType;
import org.vetcontrol.report.commons.jasper.TextExporterConstants;

/**
 *
 * @author Artem
 */
public abstract class AbstractReportServlet extends HttpServlet {

    @EJB
    private LocaleService localeService;
    private Locale reportLocale;
    protected static final String PDF_DIRECTORY = "pdf";
    protected static final String TEXT_DIRECTORY = "text";
    protected static final String SEPARATOR = "/";
    private static final Logger log = LoggerFactory.getLogger(AbstractReportServlet.class);
    private static final String ERROR_RESOURCE_BUNDLE_NAME = AbstractReportServlet.class.getPackage().getName() + ".servlet_errors";
    private static final ResourceBundle ERROR_RESOURCE_BUNDLE = ResourceBundle.getBundle(ERROR_RESOURCE_BUNDLE_NAME);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream servletOutputStream = null;
        InputStream reportStream = null;
        try {
            ExportType exportType = getExportType(request);

            Map<String, Object> daoParams = newDaoParams();
            Map<String, Object> reportParams = newReportParams();
            configureParameters(request, daoParams, reportParams);

            JRDataSource reportDataSource = configureJRDataSource(daoParams);

            servletOutputStream = response.getOutputStream();
            switch (exportType) {
                case PDF:
                    reportStream = getReportStream(request, PDF_DIRECTORY);
                    response.setContentType("application/pdf");
                    JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, reportParams, reportDataSource);
                    break;
                case TEXT:
                    reportStream = getReportStream(request, TEXT_DIRECTORY);
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, reportParams, reportDataSource);
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
        } catch (Exception e) {
            String errorMessage = error(e);
            log.error(errorMessage, e);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            try {
                if (servletOutputStream == null) {
                    servletOutputStream = response.getOutputStream();
                }
                servletOutputStream.print(errorMessage);
            } catch (Exception t) {
                log.error("", t);
            }
        } finally {
            if (servletOutputStream != null) {
                try {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                } catch (Exception e) {
                    log.error("Couldn't close servlet output stream", e);
                }
            }
            try {
                reportStream.close();
            } catch (Exception e) {
                log.error("Couldn't close report input stream", e);
            }
        }
    }

    protected abstract String getReportName(HttpServletRequest request) throws ServletException;

    protected String getReportTemplatePath() throws ServletException {
        return getClass().getPackage().getName().replace(".", SEPARATOR) + SEPARATOR;
    }

    protected InputStream getReportStream(HttpServletRequest request, String formatDirectory) throws ServletException {
        String resourceName = getReportTemplatePath() + formatDirectory + SEPARATOR + getReportName(request);
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    protected abstract void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) throws ServletException;

    protected abstract JRDataSource configureJRDataSource(Map<String, Object> daoParams);

    protected Map<String, Object> newDaoParams() {
        return new HashMap<String, Object>();
    }

    protected Map<String, Object> newReportParams() {
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put(JRParameter.REPORT_LOCALE, getReportLocale());
        return reportParams;
    }

    protected Locale getReportLocale() {
        if (reportLocale == null) {
            reportLocale = localeService.getReportLocale();
        }
        return reportLocale;
    }

    private ExportType getExportType(HttpServletRequest request) throws ServletException {
        String exportType = request.getParameter(ExportType.class.getSimpleName());
        if (Strings.isEmpty(exportType)) {
            throw new ServletException("Export type parameter must be specified.");
        }
        exportType = exportType.trim().toUpperCase();
        return ExportType.valueOf(exportType);
    }

    protected String getString(String key) {
        return ERROR_RESOURCE_BUNDLE.getString(key);
    }

    protected String error(Throwable e) {
        return getString("error");
    }
}
