/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.cargosinday;

import java.util.Date;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.service.dao.CargosInDayReportDAO;
import org.vetcontrol.report.commons.service.dao.DepartmentDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.service.dao.configuration.CargosInDayReportDAOConfig;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "CargosInDayReportServlet", urlPatterns = {"/CargosInDayReportServlet"})
@RolesAllowed({SecurityRoles.LOCAL_AND_REGIONAL_REPORT})
public final class CargosInDayReportServlet extends DefaultReportServlet {

    public static final String DAY_KEY = "day";
    public static final String DEPARTMENT_KEY = "department";
    @EJB
    private CargosInDayReportDAO reportDAO;
    @EJB
    private DepartmentDAO departmentDAO;
    @EJB
    private DateConverter dateConverter;

    @Override
    protected AbstractReportDAO<?> getReportDAO() {
        return reportDAO;
    }

    @Override
    protected String getSortProperty() {
        return CargosInDayReportDAO.OrderBy.CARGO_TYPE.getName();
    }

    @Override
    protected boolean isAscending() {
        return true;
    }

    @Override
    protected String getReportName(HttpServletRequest request) {
        return "cargos_in_day_report.jasper";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        Date day = getDay(request);
        Date startDate = DateUtil.getBeginOfDay(day);
        Date endDate = DateUtil.getEndOfDay(day);
        Long departmentId = getDepartmentId(request);

        reportParams.put(DAY_KEY, day);
        reportParams.put(DEPARTMENT_KEY, departmentDAO.getDepartmentName(departmentId, getReportLocale()));

        daoParams.putAll(CargosInDayReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private Date getDay(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(DAY_KEY).trim());
    }

    private Long getDepartmentId(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(DEPARTMENT_KEY).trim());
    }
}
