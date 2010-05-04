/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.report.entity.MeatInDayReportParameter;

/**
 *
 * @author Artem
 */
public final class MeatInDayReportDAOConfig {

    private MeatInDayReportDAOConfig() {
    }

    public static Map<String, Object> configure(Date currentDate, Long departmentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MeatInDayReportParameter.CURRENT_DATE, currentDate);
        params.put(MeatInDayReportParameter.DEPARTMENT, departmentId);
        return params;
    }
}
