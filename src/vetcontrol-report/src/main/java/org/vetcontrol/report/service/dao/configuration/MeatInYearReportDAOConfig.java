/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.report.entity.MeatInYearReportParameter;

/**
 *
 * @author Artem
 */
public final class MeatInYearReportDAOConfig {

    private MeatInYearReportDAOConfig() {
    }

    public static Map<String, Object> configure(Date startDate, Date endDate, Long departmentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MeatInYearReportParameter.START_DATE, startDate);
        params.put(MeatInYearReportParameter.END_DATE, endDate);
        params.put(MeatInYearReportParameter.DEPARTMENT, departmentId);
        return params;
    }
}
