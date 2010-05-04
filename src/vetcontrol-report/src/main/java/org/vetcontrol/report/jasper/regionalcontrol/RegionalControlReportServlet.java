/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.regionalcontrol;

import java.util.Date;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.service.dao.RegionalControlReportDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.service.dao.configuration.RegionalControlReportDAOConfig;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "RegionalControlReportServlet", urlPatterns = {"/RegionalControlReportServlet"})
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public final class RegionalControlReportServlet extends DefaultReportServlet {

    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";
    private static final String DEPARTMENT_KEY = "department";
    @EJB
    private RegionalControlReportDAO reportDAO;
    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;
    @EJB
    private DepartmentDAO departmentDAO;

    @Override
    protected AbstractReportDAO<?> getReportDAO() {
        return reportDAO;
    }

    @Override
    protected String getSortProperty() {
        return RegionalControlReportDAO.OrderBy.CARGO_ARRIVED.getName();
    }

    @Override
    protected boolean isAscending() {
        return true;
    }

    @Override
    protected String getReportName(HttpServletRequest request) {
        return "regional_control_report.jasper";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        Date start = getStart(request);
        Date end = getEnd(request);
        Date startDate = DateUtil.getBeginOfDay(start);
        Date endDate = DateUtil.getEndOfDay(end);
        Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        String departmentName = departmentDAO.getDepartmentName(departmentId, getReportLocale());

        reportParams.put(START_DATE_KEY, startDate);
        reportParams.put(END_DATE_KEY, endDate);
        reportParams.put(DEPARTMENT_KEY, departmentName);

        daoParams.putAll(RegionalControlReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private Date getStart(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(START_DATE_KEY).trim());
    }

    private Date getEnd(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(END_DATE_KEY).trim());
    }
}
