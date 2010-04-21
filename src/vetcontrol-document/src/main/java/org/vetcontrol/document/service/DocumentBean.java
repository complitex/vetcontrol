package org.vetcontrol.document.service;

import org.vetcontrol.entity.CountryBook;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.IDisabled;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.04.2010 7:16:14
 */
@Stateless
@RolesAllowed({SecurityRoles.DOCUMENT_CREATE, SecurityRoles.DOCUMENT_EDIT, SecurityRoles.DOCUMENT_DEP_VIEW})
public class DocumentBean {
    @PersistenceContext
    private EntityManager em;

    public <T extends IDisabled> List<T> getBookList(Class<T> _class) {
        return em.createQuery("select b from " + _class.getSimpleName() +" b " +
                "where b.disabled = false", _class)
                .getResultList();
    }

     public List<String> getSenderNames(CountryBook country, String filterName) {
        return em.createQuery("select dc.senderName from DocumentCargo dc "
                + "where dc.senderCountry = :country and dc.senderName like :filterName "
                + "group by dc.senderName order by dc.senderName asc", String.class).setParameter("country", country).setParameter("filterName", "%" + filterName + "%").setMaxResults(10).getResultList();
    }

    public List<String> getReceiverNames(String filterName) {
        return em.createQuery("select dc.receiverName from DocumentCargo dc "
                + "where dc.receiverName like :filterName "
                + "group by dc.receiverName order by dc.receiverName asc", String.class).setParameter("filterName", "%" + filterName + "%").setMaxResults(10).getResultList();
    }

    public List<String> getReceiverAddresses(String filterName) {
        return em.createQuery("select dc.receiverAddress from DocumentCargo dc "
                + "where dc.receiverAddress like :filterName "
                + "group by dc.receiverAddress order by dc.receiverAddress asc", String.class).setParameter("filterName", "%" + filterName + "%").setMaxResults(10).getResultList();
    }

    public String getReceiverAddress(String receiverName) {
        try {
            return em.createQuery("select dc.receiverAddress from DocumentCargo dc "
                    + "where dc.receiverName = :receiverName", String.class).setParameter("receiverName", receiverName).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return "";
        }
    }

     public List<Department> getChildDepartments(Department department) {
        List<Department> list = em.createQuery("select d from Department d where d.parent = :department "
                + "or d.parent.parent = :department", Department.class).setParameter("department", department).getResultList();

        list.add(0, department);

        return list;
    }

    public List<PassingBorderPoint> getPassingBorderPoints(Department department){
        return em.createQuery("select pbp from PassingBorderPoint pbp " +
                "where pbp.department = :department and pbp.disabled = false", PassingBorderPoint.class)
                .setParameter("department", department)
                .getResultList();
    }
}
