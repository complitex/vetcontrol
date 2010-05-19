/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.vetcontrol.entity.CustomsPoint;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;
import static org.vetcontrol.book.BeanPropertyUtil.*;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed(SecurityRoles.INFORMATION_VIEW)
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
        if (!isNewBook(department)) {
            //load all(enabled and disabled) passing border points.
            String queryString = "SELECT DISTINCT pbp FROM PassingBorderPoint pbp WHERE pbp.department  = :department";
            List<PassingBorderPoint> passingBorderPoints = entityManager.createQuery(queryString, PassingBorderPoint.class).
                    setParameter("department", department).
                    getResultList();

            department.setPassingBorderPoints(passingBorderPoints);
        }
    }

    public void saveAsNew(Department department) {
        Long oldId = department.getId();
        clearBook(department);
        updateVersionIfNecessary(department, null);
        setNeedToUpdateReferences(department);
        updateReferences(department);
        saveOrUpdate(department, oldId);
        disable(oldId);
    }

    public void saveOrUpdate(Department department, Long oldId) {
        bookDAO.saveOrUpdate(department);
        for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
            borderPoint.setDepartment(department);
            if (borderPoint.isNeedToUpdate()) {
                bookDAO.saveOrUpdate(borderPoint);
            }
        }

        if (oldId != null) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT d FROM Department d WHERE d.parent.id = :parentId AND d.").
                    append(getDisabledPropertyName()).append(" = FALSE");
            List<Department> children = entityManager.createQuery(queryBuilder.toString(), Department.class).
                    setParameter("parentId", oldId).
                    getResultList();
            if (!children.isEmpty()) {
                bookDAO.addLocalizationSupport(children);
                for (Department child : children) {
                    loadPassingBorderPoints(child);
                }
                entityManager.flush();
                entityManager.clear();

                for (Department child : children) {
                    Long oldChildId = child.getId();
                    clearBook(child);
                    child.setParent(department);
                    updateVersionIfNecessary(child, null);
                    setNeedToUpdateReferences(child);
                    updateReferences(child);
                    saveOrUpdate(child, oldChildId);
                }
            }
        }
    }

    public void updateReferences(Department department) {
        Date newVersion = DateUtil.getCurrentDate();
        for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
            if (borderPoint.isNeedToUpdate()) {
                borderPoint.setUpdated(newVersion);
            }
        }
    }

    public void setNeedToUpdateReferences(Department department) {
        for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
            clearBook(borderPoint);
            borderPoint.setNeedToUpdate(true);
        }
    }

    public void disable(Long departmentId) {
        bookDAO.disable(departmentId, Department.class);
        disablePassingBorderPoints(departmentId);
        changeChildrenActivity(departmentId, true);
    }

    public void changeChildrenActivity(Long parentId, boolean disabled) {
        List<Long> childrenDepartmentIds = getChildrenDepartmentIds(parentId);
        if (!childrenDepartmentIds.isEmpty()) {

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE Department d SET d.").
                    append(getDisabledPropertyName()).
                    append(" = :disabled, ").
                    append("d.").
                    append(getVersionPropertyName()).
                    append(" = :updated ").
                    append("WHERE d.parent.id = :parentId");
            entityManager.createQuery(queryBuilder.toString()).
                    setParameter("parentId", parentId).
                    setParameter("disabled", disabled).
                    setParameter("updated", DateUtil.getCurrentDate()).
                    executeUpdate();

            for (Long childId : childrenDepartmentIds) {
                changeChildrenActivity(childId, disabled);
            }
        }
    }

    public void changeBorderPointActivity(Long departmentId, boolean disabled) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE PassingBorderPoint pbp SET pbp.").
                append(getDisabledPropertyName()).
                append(" = :disabled, ").
                append("pbp.").
                append(getVersionPropertyName()).
                append(" = :updated ").
                append("WHERE pbp.department.id = :departmentId");
        entityManager.createQuery(queryBuilder.toString()).
                setParameter("departmentId", departmentId).
                setParameter("disabled", disabled).
                setParameter("updated", DateUtil.getCurrentDate()).
                executeUpdate();
    }

    public void enable(Long departmentId) {
        bookDAO.enable(departmentId, Department.class);
        enablePassingBorderPoints(departmentId);
        changeChildrenActivity(departmentId, false);
    }

    private void disablePassingBorderPoints(Long departmentId) {
        changeBorderPointActivity(departmentId, true);
    }

    private void enablePassingBorderPoints(Long departmentId) {
        changeBorderPointActivity(departmentId, false);
    }

    private List<Long> getChildrenDepartmentIds(Long parentId) {
        return entityManager.createQuery("SELECT d.id FROM Department d WHERE d.parent.id = :parentId", Long.class).
                setParameter("parentId", parentId).
                getResultList();
    }
}
