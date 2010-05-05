/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.web.servlet;

import java.util.Map;
import javax.naming.InitialContext;
import net.sf.jasperreports.engine.JRDataSource;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.commons.jasper.JRCacheableDataSource;

/**
 *
 * @author Artem
 */
public abstract class DefaultReportServlet extends AbstractReportServlet {

    @Override
    protected JRDataSource configureJRDataSource(Map<String, Object> daoParams) {
        AbstractReportDAO<?> reportDAO = getReportDAO();
        JRDataSource reportDataSource = new JRCacheableDataSource(reportDAO, daoParams, getSortProperty(), isAscending());
        return reportDataSource;
    }

    protected abstract String getReportDAOName();

    protected AbstractReportDAO<?> getReportDAO() {
        try {
            InitialContext context = new InitialContext();
            return (AbstractReportDAO) context.lookup("java:module/" + getReportDAOName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getSortProperty();

    protected abstract boolean isAscending();
}
