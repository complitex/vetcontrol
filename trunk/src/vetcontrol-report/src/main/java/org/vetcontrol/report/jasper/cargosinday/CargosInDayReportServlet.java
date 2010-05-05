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
import org.vetcontrol.report.service.dao.CargosInDayReportDAO;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.DefaultReportServlet;
import org.vetcontrol.report.entity.CargosInDayReportParameter;
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

    @EJB
    private DateConverter dateConverter;

    @Override
    protected String getReportDAOName() {
        return "CargosInDayReportDAO";
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
        return "cargos_in_day_report";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        Date day = getDay(request);
        Date startDate = DateUtil.getBeginOfDay(day);
        Date endDate = DateUtil.getEndOfDay(day);
        Long departmentId = getDepartmentId(request);

        reportParams.put(CargosInDayReportParameter.DAY, day);
        reportParams.put(CargosInDayReportParameter.DEPARTMENT, getDepartmentName(departmentId, getReportLocale()));

        daoParams.putAll(CargosInDayReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    private Date getDay(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(CargosInDayReportParameter.DAY).trim());
    }

    private Long getDepartmentId(HttpServletRequest request) {
        return Long.valueOf(request.getParameter(CargosInDayReportParameter.DEPARTMENT).trim());
    }
}
