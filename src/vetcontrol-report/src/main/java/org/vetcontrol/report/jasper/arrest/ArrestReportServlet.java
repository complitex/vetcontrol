/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.arrest;

import java.util.Date;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.entity.ArrestReportParameter;
import org.vetcontrol.report.service.dao.ArrestReportDAO;
import org.vetcontrol.report.service.dao.configuration.ArrestReportDAOConfig;
import org.vetcontrol.report.util.arrest.ArrestReportType;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "ArrestReportServlet", urlPatterns = {"/ArrestReportServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {SecurityRoles.REGIONAL_REPORT}))
public final class ArrestReportServlet extends DefaultReportServlet {

    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;
    private static final String SIMPLE_REPORT_NAME = "arrest_report";
    private static final String EXTENDED_REPORT_NAME = "extended_arrest_report";

    @Override
    protected String getReportDAOName() {
        return "ArrestReportDAO";
    }

    @Override
    protected String getSortProperty() {
        return ArrestReportDAO.OrderBy.ARREST_DATE.getName();
    }

    @Override
    protected boolean isAscending() {
        return true;
    }

    @Override
    protected String getReportName(HttpServletRequest request) {
        switch (getReportType(request)) {
            case EXTENDED:
                return EXTENDED_REPORT_NAME;
            case SIMPLE:
            default:
                return SIMPLE_REPORT_NAME;
        }
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        Date start = getStart(request);
        Date end = getEnd(request);
        Date startDate = DateUtil.getBeginOfDay(start);
        Date endDate = DateUtil.getEndOfDay(end);
        Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        String departmentName = getDepartmentName(departmentId, getReportLocale());

        reportParams.put(ArrestReportParameter.START_DATE, startDate);
        reportParams.put(ArrestReportParameter.END_DATE, endDate);
        reportParams.put(ArrestReportParameter.DEPARTMENT, departmentName);

        daoParams.putAll(ArrestReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private Date getStart(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(ArrestReportParameter.START_DATE).trim());
    }

    private Date getEnd(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(ArrestReportParameter.END_DATE).trim());
    }

    private ArrestReportType getReportType(HttpServletRequest request) {
        return ArrestReportType.valueOf(request.getParameter(ArrestReportParameter.REPORT_TYPE).trim());
    }
}
