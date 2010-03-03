/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.vetcontrol.report.util.jasper.TextExporterConstants;

/**
 *
 * @author Artem
 */
public class TextExportDemo {

    public static void main(String[] args) {
        try {
            HashMap parameterMap = new HashMap();
            parameterMap.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
            parameterMap.put("date", new Date());
            parameterMap.put("endDate", new Date());
            parameterMap.put("month", "январь");
            parameterMap.put("year", "2010");
            parameterMap.put("department", "Кодимский ПДВСКН");

            parameterMap.put("startDate", new Date());
            parameterMap.put("endDate", new Date());

            System.out.println("Filling report..." + System.getProperty("user.dir"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    "target/classes/org/vetcontrol/report/jasper/movementtypes/text/movement_types_report.jasper",
                    parameterMap, new JREmptyDataSource());

            JRTextExporter textExporter = new JRTextExporter();
            textExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            textExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "report.txt");
            textExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, TextExporterConstants.PAGE_WIDTH);
            textExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, TextExporterConstants.PAGE_HEIGHT);

            textExporter.exportReport();
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
