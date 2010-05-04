/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.movementtypes;

import java.util.Date;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.MovementTypesReportDAO;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.service.dao.configuration.MovementTypesReportDAOConfig;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MovementTypesReportServlet", urlPatterns = {"/MovementTypesReportServlet"})
@RolesAllowed({SecurityRoles.LOCAL_AND_REGIONAL_REPORT})
public final class MovementTypesReportServlet extends DefaultReportServlet {

    public static final String MONTH_KEY = "month";
    public static final String DEPARTMENT_KEY = "department";
    private static final String END_DATE_KEY = "endDate";
    private static final String YEAR_KEY = "year";
    @EJB
    private MovementTypesReportDAO reportDAO;
    @EJB
    private DepartmentDAO departmentDAO;

    @Override
    protected AbstractReportDAO<?> getReportDAO() {
        return reportDAO;
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
        return "movement_types_report.jasper";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        int month = getMonth(request);
        Long departmentId = getDepartment(request);
        Date startDate = DateUtil.getFirstDateOfYear();
        Date endDate = DateUtil.getLastDateOfMonth(month);
        String monthAsString = DateUtil.getDisplayMonth(month, getReportLocale()).toLowerCase();
        String year = String.valueOf(DateUtil.getCurrentYear());
        String departmentName = departmentDAO.getDepartmentName(departmentId, getReportLocale());

        reportParams.put(END_DATE_KEY, endDate);
        reportParams.put(MONTH_KEY, monthAsString);
        reportParams.put(YEAR_KEY, year);
        reportParams.put(DEPARTMENT_KEY, departmentName);

        daoParams.putAll(MovementTypesReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private int getMonth(HttpServletRequest request) {
        return Integer.valueOf(request.getParameter(MONTH_KEY).trim());
    }

    private Long getDepartment(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(DEPARTMENT_KEY).trim());
    }
}
