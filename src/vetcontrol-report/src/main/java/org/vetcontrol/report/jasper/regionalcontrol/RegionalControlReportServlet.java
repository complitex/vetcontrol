/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.regionalcontrol;

import java.util.Date;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.vetcontrol.report.service.dao.RegionalControlReportDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.entity.RegionalControlReportParameter;
import org.vetcontrol.report.service.dao.configuration.RegionalControlReportDAOConfig;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "RegionalControlReportServlet", urlPatterns = {"/RegionalControlReportServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {SecurityRoles.REGIONAL_REPORT}))
public final class RegionalControlReportServlet extends DefaultReportServlet {

    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;

    @Override
    protected String getReportDAOName() {
        return "RegionalControlReportDAO";
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
        return "regional_control_report";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        Date start = getStart(request);
        Date end = getEnd(request);
        Date startDate = DateUtil.getBeginOfDay(start);
        Date endDate = DateUtil.getEndOfDay(end);
        Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        String departmentName = getDepartmentName(departmentId, getReportLocale());

        reportParams.put(RegionalControlReportParameter.START_DATE, startDate);
        reportParams.put(RegionalControlReportParameter.END_DATE, endDate);
        reportParams.put(RegionalControlReportParameter.DEPARTMENT, departmentName);

        daoParams.putAll(RegionalControlReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private Date getStart(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(RegionalControlReportParameter.START_DATE).trim());
    }

    private Date getEnd(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(RegionalControlReportParameter.END_DATE).trim());
    }
}
