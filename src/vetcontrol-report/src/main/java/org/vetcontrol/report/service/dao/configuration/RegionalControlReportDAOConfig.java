/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.report.entity.RegionalControlReportParameter;

/**
 *
 * @author Artem
 */
public final class RegionalControlReportDAOConfig {

    private RegionalControlReportDAOConfig() {
    }

    public static Map<String, Object> configure(Date startDate, Date endDate, Long departmentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RegionalControlReportParameter.START_DATE, startDate);
        params.put(RegionalControlReportParameter.END_DATE, endDate);
        params.put(RegionalControlReportParameter.DEPARTMENT, departmentId);
        return params;
    }
}
