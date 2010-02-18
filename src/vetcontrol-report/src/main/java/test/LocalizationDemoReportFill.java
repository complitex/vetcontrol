/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 *
 * @author Artem
 */
public class LocalizationDemoReportFill {

    public static void main(String[] args) {
        try {
            HashMap parameterMap = new HashMap();
            parameterMap.put(JRParameter.REPORT_LOCALE, Locale.getDefault());
            InputStream reportStream = LocalizationDemoReportFill.class.getResourceAsStream("LocalizationDemoReport.jasper");
//            JasperRunManager.runReportToPdf(reportStream, parameterMap, new JREmptyDataSource());

            System.out.println("Filling report..." + System.getProperty("user.dir"));
            JasperRunManager.runReportToPdfFile(
                    "target/classes/org/vetcontrol/report/jasper/movementtypes/movement_types_report.jasper",
                    parameterMap, new JREmptyDataSource());
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
