/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.document.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.vetcontrol.report.commons.jasper.TextExporterConstants;
import org.vetcontrol.report.document.entity.ArrestDocumentReport;

/**
 *
 * @author Artem
 */
public class ArrestDocumentReportExportDemo {

    public static void main(String[] args) {
        try {
            HashMap parameterMap = new HashMap();
            parameterMap.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
            System.out.println("Filling report...");
            ArrestDocumentReport report = initReportEntity();

            JRRewindableDataSource dataSource = new JRBeanArrayDataSource(new ArrestDocumentReport[]{report});
            pdfExport(parameterMap, dataSource);
            dataSource.moveFirst();
            textExport(parameterMap, dataSource);

            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void pdfExport(Map parameters, JRDataSource dataSource) throws JRException {
        JasperRunManager.runReportToPdfFile(
                "target/classes/org/vetcontrol/report/document/jasper/arrest/pdf/arrest_document_report.jasper",
                "report.pdf",
                parameters, dataSource);
    }

    private static void textExport(Map parameters, JRDataSource dataSource) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                "target/classes/org/vetcontrol/report/document/jasper/arrest/text/arrest_document_report.jasper",
                parameters, dataSource);
        JRTextExporter textExporter = new JRTextExporter();
        textExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        textExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "report.txt");
        textExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, TextExporterConstants.PAGE_WIDTH);
        textExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, TextExporterConstants.PAGE_HEIGHT);
        textExporter.exportReport();
    }

    private static ArrestDocumentReport initReportEntity() {
        ArrestDocumentReport report = new ArrestDocumentReport();
        report.setArrestDate(new Date());
        report.setArrestReason("Причина задержания №1");
        report.setArrestReasonDetails("Детали задержания. Детали задержания. Детали задержания. Детали задержания. Детали задержания. Детали задержания." +
                " Детали задержания. Детали задержания. Детали задержания. Детали задержания. Детали задержания. Детали задержания. AAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB");
        report.setCargoReceiverAddress("Адрес получателя 1");
        report.setCargoReceiverName("Получатель 1");
        report.setCargoSenderCountry("Аргентина");
        report.setCargoSenderName("Отправитель 1");
        report.setCargoTypeName("Категория груза 1");
        report.setCertificateDate(new Date());
        report.setCertificateDetails("№ 12345678");
        report.setCount(12.47);
        report.setDepartmentName("Кодимский ПДВСКН");
        report.setId("2");
        report.setPassingBorderPointName("Пункт пропуска №1 при Кодимском ПДВСКН");
        report.setUnitTypeName("кг.");
        report.setVehicleDetails("№ 1234567");
        report.setVehicleType("контейнер");
        return report;
    }
}
