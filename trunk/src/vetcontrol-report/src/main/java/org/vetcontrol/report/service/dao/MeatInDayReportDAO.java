/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.vetcontrol.report.commons.entity.ReportParameter;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.commons.util.SqlQueryLoader;
import org.vetcontrol.report.entity.MeatInDayReport;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public class MeatInDayReportDAO extends AbstractReportDAO<MeatInDayReport> {

    public MeatInDayReportDAO() {
        super(MeatInDayReport.class);
    }

    public List<MeatInDayReport> getAll(Map<String, Object> daoParameters, Locale reportLocale) {
        try {
            Session session = HibernateSessionTransformer.getSession(getEntityManager());
            String pattern = SqlQueryLoader.getQuery(getReportEntityClass(), getKeyForAll());
            String sql = prepareAllSQL(pattern, daoParameters, reportLocale, null, null);
            Query query = session.createSQLQuery(sql);
            query.setParameter(ReportParameter.REPORT_LOCALE, reportLocale.getLanguage());

            for (Map.Entry<String, Object> entry : daoParameters.entrySet()) {
                if (entry.getValue() instanceof Date) {
                    query.setParameter(entry.getKey(), entry.getValue(), Hibernate.TIMESTAMP);
                } else {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            List<MeatInDayReport> results = query.setResultTransformer(Transformers.aliasToBean(getReportEntityClass())).
                    list();

            results = afterLoad(results);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<MeatInDayReport> afterLoad(List<MeatInDayReport> results) {
        boolean previousCargoModeIsRoot = false;
        for (MeatInDayReport report : results) {
            if (!report.isTotalEntry()) {
                if (report.isRootCargoMode()) {
                    previousCargoModeIsRoot = true;
                } else {
                    if (previousCargoModeIsRoot) {
                        report.setFirstSubCargoMode(true);
                        previousCargoModeIsRoot = false;
                    }
                }
            }
        }
        return results;
    }
}
