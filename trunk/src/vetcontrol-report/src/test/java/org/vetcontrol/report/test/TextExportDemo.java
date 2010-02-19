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

/**
 *
 * @author Artem
 */
public class TextExportDemo {

    public static void main(String[] args) {
        try {
            HashMap parameterMap = new HashMap();
            parameterMap.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
            parameterMap.put("endDate", new Date());

            System.out.println("Filling report..." + System.getProperty("user.dir"));
            JasperPrint jasperPrint = JasperFillManager.fillReport("target/classes/org/vetcontrol/report/jasper/movementtypes/movement_types_report.jasper",
                    parameterMap, new JREmptyDataSource());

            JRTextExporter textExporter = new JRTextExporter();
            textExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            textExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "movement_types_report.txt");
            textExporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, 150);
            textExporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, 150);

            textExporter.exportReport();
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
