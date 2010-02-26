/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.vetcontrol.report.entity.RegionalControlReport;
import org.vetcontrol.report.util.QueryLoader;
import org.vetcontrol.report.util.regionalcontrol.CellFormatter;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public class RegionalControlReportDAO {

    public static enum OrderBy {

        CARGO_ARRIVED("cargoArrived"),
        CARGO_TYPE("cargoTypeName + '" + CellFormatter.CARGO_TYPE_DELIMETER + "' + cargoTypeCode"),
        CARGO_RECEIVER("cargoReceiverName"), CARGO_PRODUCER("cargoProducerName");
        private String name;

        private OrderBy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    @PersistenceContext
    private EntityManager em;
    private static final String LOCALE = "locale";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String DEPARTMENT = "department";

    public List<RegionalControlReport> getAll(Long departmentId, Locale locale, Date startDate, Date endDate, int first, int count,
            String sortProperty, boolean isAscending) {
        try {
            Session session = HibernateSessionTransformer.getSession(em);
            String pattern = QueryLoader.getQuery(RegionalControlReport.class, "query");
            String sql = MessageFormat.format(pattern, sortProperty, isAscending ? "ASC" : "DESC");
            return session.createSQLQuery(sql).
                    setParameter(LOCALE, locale.getLanguage()).
                    setParameter(START_DATE, startDate, Hibernate.TIMESTAMP).
                    setParameter(END_DATE, endDate, Hibernate.TIMESTAMP).
                    setParameter(DEPARTMENT, departmentId).
                    setFirstResult(first).
                    setMaxResults(count).
                    setResultTransformer(Transformers.aliasToBean(RegionalControlReport.class)).
                    list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int size(Long departmentId, Locale locale, Date startDate, Date endDate) {
        try {
            Session session = HibernateSessionTransformer.getSession(em);
            String sql = QueryLoader.getQuery(RegionalControlReport.class, "size");
            BigInteger size = (BigInteger) session.createSQLQuery(sql).
                    setParameter(START_DATE, startDate, Hibernate.TIMESTAMP).
                    setParameter(END_DATE, endDate, Hibernate.TIMESTAMP).
                    setParameter(DEPARTMENT, departmentId).
                    uniqueResult();
            return size != null ? size.intValue() : 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
