/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.report.entity.ArrestReportParameter;

/**
 *
 * @author Artem
 */
public final class ArrestReportDAOConfig {

    private ArrestReportDAOConfig() {
    }

    public static Map<String, Object> configure(Date startDate, Date endDate, Long departmentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ArrestReportParameter.START_DATE, startDate);
        params.put(ArrestReportParameter.END_DATE, endDate);
        params.put(ArrestReportParameter.DEPARTMENT, departmentId);
        return params;
    }
}
