/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.movementtypes;

import java.util.Date;
import java.util.Map;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.entity.MovementTypesReportParameter;
import org.vetcontrol.report.service.dao.configuration.MovementTypesReportDAOConfig;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MovementTypesReportServlet", urlPatterns = {"/MovementTypesReportServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {SecurityRoles.LOCAL_AND_REGIONAL_REPORT}))
public final class MovementTypesReportServlet extends DefaultReportServlet {

    @Override
    protected String getReportDAOName() {
        return "MovementTypesReportDAO";
    }

    @Override
    protected String getSortProperty() {
        return null;
    }

    @Override
    protected boolean isAscending() {
        return true;
    }

    @Override
    protected String getReportName(HttpServletRequest request) {
        return "movement_types_report";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        int month = getMonth(request);
        Long departmentId = getDepartment(request);
        Date startDate = DateUtil.getFirstDateOfYear();
        Date endDate = DateUtil.getLastDateOfMonth(month);
        String monthAsString = DateUtil.getDisplayMonth(month, getReportLocale()).toLowerCase();
        String year = String.valueOf(DateUtil.getCurrentYear());
        String departmentName = getDepartmentName(departmentId, getReportLocale());

        reportParams.put(MovementTypesReportParameter.END_DATE, endDate);
        reportParams.put(MovementTypesReportParameter.MONTH, monthAsString);
        reportParams.put(MovementTypesReportParameter.YEAR, year);
        reportParams.put(MovementTypesReportParameter.DEPARTMENT, departmentName);

        daoParams.putAll(MovementTypesReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private int getMonth(HttpServletRequest request) {
        return Integer.valueOf(request.getParameter(MovementTypesReportParameter.MONTH).trim());
    }

    private Long getDepartment(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(MovementTypesReportParameter.DEPARTMENT).trim());
    }
}
