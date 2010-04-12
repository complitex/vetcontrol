/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.vetcontrol.entity.CustomsPoint;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.util.book.BeanPropertyUtil;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DepartmentBookDAO {

    @EJB
    private IBookDAO bookDAO;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Department> getAvailableDepartments(Department department) {
        Set<Long> exclude = new HashSet<Long>();
        getAvailableDepartments(department.getId(), exclude);

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT d FROM Department d WHERE d.level IN (1,2)");

        if (!exclude.isEmpty()) {
            queryBuilder.append(" AND d.id NOT IN (");

            int i = 0;
            for (Long excludeId : exclude) {
                queryBuilder.append(excludeId);
                if (i < exclude.size() - 1) {
                    queryBuilder.append(", ");
                }
                i++;
            }
            queryBuilder.append(")");
        }

        List<Department> availableDepartments = entityManager.createQuery(queryBuilder.toString(), Department.class).getResultList();
        bookDAO.addLocalizationSupport(availableDepartments);
        return availableDepartments;
    }

    private void getAvailableDepartments(Long id, Set<Long> exclude) {
        if (id == null) {
            return;
        }

        exclude.add(id);

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT d.id FROM Department d WHERE d.parent.id = :id");
        List<Long> references = entityManager.createQuery(queryBuilder.toString(), Long.class).
                setParameter("id", id).
                getResultList();
        if (references != null && !references.isEmpty()) {
            for (Long referenceId : references) {
                getAvailableDepartments(referenceId, exclude);
            }
        }
    }

    public List<CustomsPoint> getAvailableCustomsPoint() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT cp FROM CustomsPoint cp");
        List<CustomsPoint> availableCustomsPoint = entityManager.createQuery(queryBuilder.toString(), CustomsPoint.class).getResultList();
        bookDAO.addLocalizationSupport(availableCustomsPoint);
        return availableCustomsPoint;
    }

    public void loadPassingBorderPoints(Department department) {
        if (!BeanPropertyUtil.isNewBook(department)) {
            //load all(enabled and disabled) passing border points.
            String queryString = "SELECT DISTINCT pbp FROM PassingBorderPoint pbp WHERE pbp.department  = :department";
            List<PassingBorderPoint> passingBorderPoints = entityManager.createQuery(queryString, PassingBorderPoint.class).
                    setParameter("department", department).
                    getResultList();

            department.setPassingBorderPoints(passingBorderPoints);
        }
    }

    public void saveOrUpdate(Department department) {
        bookDAO.saveOrUpdate(department);
        for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
            borderPoint.setDepartment(department);
            if (borderPoint.isNeedToUpdate()) {
                bookDAO.saveOrUpdate(borderPoint);
            }
        }
    }
}
