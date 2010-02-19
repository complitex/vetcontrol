/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.test;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 *
 * @author Artem
 */
public class PDFExportDemo {

    public static void main(String[] args) {
        try {
            HashMap parameterMap = new HashMap();
            parameterMap.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
            parameterMap.put("endDate", new Date());
            parameterMap.put("month", "Январь");
            parameterMap.put("year", "2010");
            parameterMap.put("department", "Кодимский ПДВСКН");

            System.out.println("Filling report..." + System.getProperty("user.dir"));
            JasperRunManager.runReportToPdfFile(
                    "target/classes/org/vetcontrol/report/jasper/movementtypes/pdf/movement_types_report.jasper",
                    parameterMap, new JREmptyDataSource());
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
