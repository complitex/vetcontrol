/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.vetcontrol.entity.Department;
import org.vetcontrol.report.entity.MovementTypesReport;
import org.vetcontrol.report.util.QueryLoader;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MovementTypesReportDAO {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private UserProfileBean userProfileBean;
    private static final String LOCALE = "locale";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String DEPARTMENT = "department";

    public List<MovementTypesReport> getAll(Long departmentId, Locale locale, Date startDate, Date endDate, int first, int count,
            boolean isAscending) {
        try {
            Session session = HibernateSessionTransformer.getSession(em);
            String sql = QueryLoader.getQuery(MovementTypesReport.class, "query");
            sql += isAscending ? " ASC" : " DESC";
            return session.createSQLQuery(sql).
                    setParameter(LOCALE, locale.getLanguage()).
                    setParameter(START_DATE, startDate, Hibernate.TIMESTAMP).
                    setParameter(END_DATE, endDate, Hibernate.TIMESTAMP).
                    setParameter(DEPARTMENT, departmentId).
                    setFirstResult(first).
                    setMaxResults(count).
                    setResultTransformer(Transformers.aliasToBean(MovementTypesReport.class)).
                    list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int size(Long departmentId, Locale locale, Date startDate, Date endDate) {
        try {
            Session session = HibernateSessionTransformer.getSession(em);
            String sql = QueryLoader.getQuery(MovementTypesReport.class, "size");
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

    public List<Department> getAvailableDepartments() {
        Department currentDepartment = userProfileBean.getCurrentUser().getDepartment();
        List<Department> availableDepartments = new ArrayList<Department>();
        children(currentDepartment, availableDepartments);
        return availableDepartments;
    }

    private void children(Department parent, List<Department> availableDepartments) {
        List<Department> children = em.createQuery("SELECT d FROM Department d WHERE d.parent = :parent", Department.class).
                setParameter("parent", parent).getResultList();
        if (children.isEmpty()) {
            availableDepartments.add(parent);
        } else {
            for (Department child : children) {
                children(child, availableDepartments);
            }
        }
    }
}
