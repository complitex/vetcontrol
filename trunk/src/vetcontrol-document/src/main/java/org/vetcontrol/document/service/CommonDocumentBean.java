package org.vetcontrol.document.service;

import java.util.Collections;
import org.vetcontrol.entity.CountryBook;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import javax.persistence.Query;
import org.vetcontrol.book.BeanPropertyUtil;
import org.vetcontrol.book.ShowBooksMode;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoProducer;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.UnitType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.04.2010 7:16:14
 */
@Stateless(name = "CommonDocumentBean")
@RolesAllowed({SecurityRoles.DOCUMENT_CREATE, SecurityRoles.DOCUMENT_EDIT, SecurityRoles.DOCUMENT_DEP_VIEW})
public class CommonDocumentBean {

    @PersistenceContext
    private EntityManager em;

    public <T> List<T> getBookList(Class<T> bookClass, ShowBooksMode show) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT book FROM ").append(bookClass.getSimpleName()).append(" book ");
        Boolean isDisabled = null;
        switch (show) {
            case ALL:
                break;
            case DISABLED:
            case ENABLED:
                queryBuilder.append(" WHERE book.").append(BeanPropertyUtil.getDisabledPropertyName()).append(" = :disabled");
                isDisabled = show == ShowBooksMode.ENABLED ? false : true;
                break;
        }
        Query query = em.createQuery(queryBuilder.toString(), bookClass);
        if (isDisabled != null) {
            query.setParameter("disabled", isDisabled);
        }
        return query.getResultList();
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

    public List<PassingBorderPoint> getPassingBorderPoints(Department department, ShowBooksMode show) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT pbp FROM PassingBorderPoint pbp WHERE pbp.department = :department");
        Boolean isDisabled = null;
        switch (show) {
            case ALL:
                break;
            case DISABLED:
            case ENABLED:
                queryBuilder.append(" AND pbp.").append(BeanPropertyUtil.getDisabledPropertyName()).append(" = :disabled");
                isDisabled = show == ShowBooksMode.ENABLED ? false : true;
                break;
        }
        Query query = em.createQuery(queryBuilder.toString(), PassingBorderPoint.class);
        query.setParameter("department", department);
        if (isDisabled != null) {
            query.setParameter("disabled", isDisabled);
        }
        return query.getResultList();
    }

    public List<UnitType> getUnitTypes(CargoMode cargoMode, ShowBooksMode show) {
        if (cargoMode == null) {
            return Collections.emptyList();
        } else {
            StringBuilder queryBuilder = new StringBuilder();
            Boolean isDisabled = null;
            queryBuilder.append("SELECT DISTINCT ut FROM UnitType ut ");
            queryBuilder.append("WHERE ut.id IN (SELECT cmut.id.unitTypeId FROM CargoModeUnitType cmut WHERE cmut.cargoMode = :cargoMode) ");
            switch (show) {
                case ALL:
                    break;
                case DISABLED:
                case ENABLED:
                    queryBuilder.append(" AND ut.").append(BeanPropertyUtil.getDisabledPropertyName()).append(" = :disabled");
                    isDisabled = show == ShowBooksMode.ENABLED ? false : true;
                    break;
            }
            Query query = em.createQuery(queryBuilder.toString(), UnitType.class);
            query.setParameter("cargoMode", cargoMode);
            if (isDisabled != null) {
                query.setParameter("disabled", isDisabled);
            }
            return query.getResultList();
        }
    }

    public List<CargoProducer> getCargoProducers(CountryBook country, ShowBooksMode show) {
        StringBuilder queryBuilder = new StringBuilder();
        Boolean isDisabled = null;
        queryBuilder.append("SELECT DISTINCT cp FROM CargoProducer cp ");
        queryBuilder.append("WHERE cp.country = :country");
        switch (show) {
            case ALL:
                break;
            case DISABLED:
            case ENABLED:
                queryBuilder.append(" AND cp.").append(BeanPropertyUtil.getDisabledPropertyName()).append(" = :disabled");
                isDisabled = show == ShowBooksMode.ENABLED ? false : true;
                break;
        }
        Query query = em.createQuery(queryBuilder.toString(), CargoProducer.class);
        query.setParameter("country", country);
        if (isDisabled != null) {
            query.setParameter("disabled", isDisabled);
        }
        return query.getResultList();
    }

    public List<CargoMode> getCargoModes(CargoType cargoType, ShowBooksMode show) {
        StringBuilder queryBuilder = new StringBuilder();
        Boolean isDisabled = null;
        queryBuilder.append("SELECT DISTINCT cm FROM CargoMode cm WHERE cm.parent IS NOT NULL ").
                append("AND cm.id IN (SELECT cmct.cargoMode.id FROM CargoModeCargoType cmct WHERE cmct.cargoType = :cargoType)");
        switch (show) {
            case ALL:
                break;
            case DISABLED:
            case ENABLED:
                queryBuilder.append(" AND cm.").append(BeanPropertyUtil.getDisabledPropertyName()).append(" = :disabled");
                isDisabled = show == ShowBooksMode.ENABLED ? false : true;
                break;
        }
        Query query = em.createQuery(queryBuilder.toString(), CargoMode.class);
        query.setParameter("cargoType", cargoType);
        if (isDisabled != null) {
            query.setParameter("disabled", isDisabled);
        }
        return query.getResultList();
    }

}
