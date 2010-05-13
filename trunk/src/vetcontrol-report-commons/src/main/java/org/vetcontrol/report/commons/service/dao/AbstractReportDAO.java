/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.service.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.vetcontrol.report.commons.entity.Ordered;
import org.vetcontrol.report.commons.entity.ReportParameter;
import org.vetcontrol.report.commons.service.LocaleService;
import org.vetcontrol.report.commons.util.SqlQueryLoader;
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
public abstract class AbstractReportDAO<T extends Serializable> {

    private static final String ALL_KEY = "all";
    private static final String SIZE_KEY = "size";
    private Class<T> reportEntityType;
    @EJB(name = "LocaleService")
    private LocaleService localeService;
    @PersistenceContext
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public AbstractReportDAO(Class<T> reportEntityType) {
        this.reportEntityType = reportEntityType;
    }

    protected Locale getReportLocale() {
        return localeService.getReportLocale();
    }

    public List<T> getAll(Map<String, Object> parameters, int first, int count, String sortProperty, boolean isAscending) {
        try {
            Session session = HibernateSessionTransformer.getSession(getEntityManager());
            String pattern = SqlQueryLoader.getQuery(getReportEntityClass(), getKeyForAll());
            String sql = prepareAllSQL(pattern, parameters, getReportLocale(), sortProperty, isAscending);
            Query query = session.createSQLQuery(sql);
            query.setParameter(ReportParameter.REPORT_LOCALE, getReportLocale());
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (entry.getValue() instanceof Date) {
                    query.setParameter(entry.getKey(), entry.getValue(), Hibernate.TIMESTAMP);
                } else {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            List<T> results = query.setFirstResult(first).
                    setMaxResults(count).
                    setResultTransformer(Transformers.aliasToBean(getReportEntityClass())).
                    list();

            if (Ordered.class.isAssignableFrom(getReportEntityClass())) {
                int order = first + 1;
                for (T result : results) {
                    ((Ordered) result).setOrder(order++);
                }
            }

            results = afterLoad(results);

            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getAll(Map<String, Object> parameters) {
        try {
            Session session = HibernateSessionTransformer.getSession(getEntityManager());
            String pattern = SqlQueryLoader.getQuery(getReportEntityClass(), getKeyForAll());
            String sql = prepareAllSQL(pattern, parameters, getReportLocale(), null, null);
            Query query = session.createSQLQuery(sql);
            query.setParameter(ReportParameter.REPORT_LOCALE, getReportLocale());
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (entry.getValue() instanceof Date) {
                    query.setParameter(entry.getKey(), entry.getValue(), Hibernate.TIMESTAMP);
                } else {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            List<T> results = query.setResultTransformer(Transformers.aliasToBean(getReportEntityClass())).
                    list();

            results = afterLoad(results);

            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int size(Map<String, Object> parameters) {
        try {
            Session session = HibernateSessionTransformer.getSession(getEntityManager());
            String pattern = SqlQueryLoader.getQuery(getReportEntityClass(), getKeyForSize());
            String sql = prepareSizeSQL(pattern, parameters);
            Query query = session.createSQLQuery(sql);
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (entry.getValue() instanceof Date) {
                    query.setParameter(entry.getKey(), entry.getValue(), Hibernate.TIMESTAMP);
                } else {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            return ((Number) query.uniqueResult()).intValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String prepareSizeSQL(String sqlPattern, Map<String, Object> parameters) {
        return sqlPattern;
    }

    protected String prepareAllSQL(String sqlPattern, Map<String, Object> parameters, Locale reportLocale,
            String sortProperty, Boolean isAscending) {
        return sqlPattern;
    }

    protected String getKeyForAll() {
        return ALL_KEY;
    }

    protected String getKeyForSize() {
        return SIZE_KEY;
    }

    protected Class<T> getReportEntityClass() {
        return reportEntityType;
    }

    protected List<T> afterLoad(List<T> results) {
        return results;
    }
}
