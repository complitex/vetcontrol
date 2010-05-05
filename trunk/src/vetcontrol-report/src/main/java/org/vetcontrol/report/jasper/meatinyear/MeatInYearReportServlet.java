/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.meatinyear;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.AbstractReportServlet;
import org.vetcontrol.report.entity.MeatInYearReport;
import org.vetcontrol.report.entity.MeatInYearReportParameter;
import org.vetcontrol.report.service.dao.MeatInYearReportDAO;
import org.vetcontrol.report.service.dao.configuration.MeatInYearReportDAOConfig;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MeatInYearReportServlet", urlPatterns = {"/MeatInYearReportServlet"})
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public final class MeatInYearReportServlet extends AbstractReportServlet {

    @EJB
    private MeatInYearReportDAO reportDAO;
    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;

    @Override
    protected String getReportName(HttpServletRequest request) throws ServletException {
        return "meat_in_year_report";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) throws ServletException {
        Date startDate = getStart(request);
        Date endDate = getEnd(request);
        Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        String departmentName = getDepartmentName(departmentId, getReportLocale());

        reportParams.put(MeatInYearReportParameter.START_DATE, startDate);
        reportParams.put(MeatInYearReportParameter.END_DATE, endDate);
        reportParams.put(MeatInYearReportParameter.DEPARTMENT, departmentName);

        daoParams.putAll(MeatInYearReportDAOConfig.configure(startDate, endDate, departmentId));
    }

    @Override
    protected JRDataSource configureJRDataSource(Map<String, Object> daoParams) {
        List<MeatInYearReport> results = reportDAO.getAll(daoParams);
        return new JRBeanCollectionDataSource(results);
    }

    private Date getStart(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(MeatInYearReportParameter.START_DATE).trim());
    }

    private Date getEnd(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(MeatInYearReportParameter.END_DATE).trim());
    }
}
