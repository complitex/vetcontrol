/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.report.entity.MovementTypesReportParameter;

/**
 *
 * @author Artem
 */
public final class MovementTypesReportDAOConfig {

    private MovementTypesReportDAOConfig() {
    }

    public static Map<String, Object> configure(Date startDate, Date endDate, Long departmentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MovementTypesReportParameter.START_DATE, startDate);
        params.put(MovementTypesReportParameter.END_DATE, endDate);
        params.put(MovementTypesReportParameter.DEPARTMENT, departmentId);
        return params;
    }
}
