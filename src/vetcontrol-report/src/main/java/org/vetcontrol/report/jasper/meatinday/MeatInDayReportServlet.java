/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.jasper.meatinday;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.vetcontrol.report.commons.util.DateConverter;
import org.vetcontrol.report.commons.web.servlet.AbstractReportServlet;
import org.vetcontrol.report.entity.MeatInDayReport;
import org.vetcontrol.report.entity.MeatInDayReportParameter;
import org.vetcontrol.report.service.dao.MeatInDayReportDAO;
import org.vetcontrol.report.service.dao.configuration.MeatInDayReportDAOConfig;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@WebServlet(name = "MeatInDayReportServlet", urlPatterns = {"/MeatInDayReportServlet"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {SecurityRoles.REGIONAL_REPORT}))
public final class MeatInDayReportServlet extends AbstractReportServlet {

    @EJB
    private MeatInDayReportDAO reportDAO;
    @EJB
    private DateConverter dateConverter;
    @EJB
    private UserProfileBean userProfileBean;

    @Override
    protected String getReportName(HttpServletRequest request) {
        return "meat_in_day_report";
    }

    @Override
    protected void configureParameters(HttpServletRequest request, Map<String, Object> daoParams, Map<String, Object> reportParams) {
        Date currentDate = getCurrentDate(request);
        Long departmentId = userProfileBean.getCurrentUser().getDepartment().getId();
        String departmentName = getDepartmentName(departmentId, getReportLocale());

        reportParams.put(MeatInDayReportParameter.CURRENT_DATE, currentDate);
        reportParams.put(MeatInDayReportParameter.DEPARTMENT, departmentName);

        daoParams.putAll(MeatInDayReportDAOConfig.configure(currentDate, departmentId));
    }

    @Override
    protected JRDataSource configureJRDataSource(Map<String, Object> daoParams) {
        List<MeatInDayReport> results = reportDAO.getAll(daoParams);
        return new JRBeanCollectionDataSource(results);
    }

    private Date getCurrentDate(HttpServletRequest request) {
        return dateConverter.toDate(request.getParameter(MeatInDayReportParameter.CURRENT_DATE).trim());
    }
}
