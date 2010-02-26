/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.vetcontrol.entity.Department;
import org.vetcontrol.service.UserProfileBean;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DepartmentDAO {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private UserProfileBean userProfileBean;

    public String getDepartmentName(Long departmentId, Locale locale) {
        return em.createQuery("SELECT sc.value FROM StringCulture sc WHERE sc.id.id = "
                + "(SELECT d.name FROM Department d WHERE d.id = :departmentId) AND sc.id.locale = :locale", String.class).
                setParameter("departmentId", departmentId).
                setParameter("locale", locale.getLanguage()).getSingleResult();
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
