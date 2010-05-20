/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.wicket.util.string.Strings;
import org.hibernate.Session;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoModeCargoType;
import org.vetcontrol.entity.CargoModeUnitType;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.DeletedEmbeddedId;
import org.vetcontrol.entity.UnitType;
import org.vetcontrol.information.entity.filter.CargoModeFilter;
import org.vetcontrol.util.DateUtil;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.book.ShowBooksMode;
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed(SecurityRoles.INFORMATION_VIEW)
public class CargoModeDAO {

    private static final String DELETED_PRIMARY_KEY_SEPARATOR = ":";

    @EJB
    private IBookDAO bookDAO;

    public static enum OrderBy {

        ID("id"), NAME("name"), UKTZED("uktzed"), PARENT_NAME("parent.name");

        private String propertyName;

        private OrderBy(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public static OrderBy valueByProperty(String propertyName) {
            for (OrderBy orderBy : values()) {
                if (orderBy.getPropertyName().equals(propertyName)) {
                    return orderBy;
                }
            }
            return null;
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, Object> newParams() {
        return new HashMap<String, Object>();
    }

    public List<CargoMode> getAll(CargoModeFilter filter, int first, int count, OrderBy orderBy, boolean asc, Locale currentLocale,
            ShowBooksMode showBooksMode) {
        Map<String, Object> params = newParams();
        StringBuilder queryBuilder = select(false, params, orderBy, currentLocale);
        queryBuilder.append(where(filter, params, showBooksMode));
        queryBuilder.append(orderBy(orderBy, asc));

        Query query = entityManager.createQuery(queryBuilder.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        List<Object[]> results = query.setFirstResult(first).setMaxResults(count).getResultList();
        List<CargoMode> cargoModes = new ArrayList<CargoMode>(results.size());
        for (Object[] result : results) {
            cargoModes.add((CargoMode) result[0]);
        }
        return cargoModes;
    }

    private StringBuilder select(boolean forSize, Map<String, Object> params, OrderBy orderBy, Locale currentLocale) {
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        if (forSize) {
            queryBuilder.append(" COUNT(DISTINCT cm) ");
        } else {
            queryBuilder.append(" DISTINCT(cm), ");
            String property = null;
            switch (orderBy) {
                case NAME:
                case PARENT_NAME:
                    property = orderBy.getPropertyName();
                    break;
            }
            if (property != null) {
                queryBuilder.append("(SELECT sc.value FROM StringCulture sc WHERE cm.").
                        append(property).
                        append(" = sc.id.id AND sc.id.locale = :locale) ");
                params.put("locale", currentLocale.getLanguage());
            } else {
                queryBuilder.append("'1' ");
            }
        }
        queryBuilder.append(" FROM CargoMode cm LEFT JOIN cm.cargoModeCargoTypes cmct LEFT JOIN cmct.cargoType ct ");
        return queryBuilder;
    }

    private StringBuilder where(CargoModeFilter filter, Map<String, Object> params, ShowBooksMode showBooksMode) {
        StringBuilder where = new StringBuilder(" WHERE (1=1) ");
        switch (showBooksMode) {
            case ALL:
                break;
            default:
                where.append(" AND cm." + getDisabledPropertyName() + " = :disabled ");
                params.put("disabled", showBooksMode.equals(ShowBooksMode.ENABLED) ? Boolean.FALSE : Boolean.TRUE);
                break;
        }
        if (filter != null) {
            if (!Strings.isEmpty(filter.getName())) {
                where.append(" AND cm.name IN (SELECT sc_name.id.id FROM StringCulture sc_name WHERE sc_name.value LIKE :name) ");
                params.put("name", "%" + filter.getName() + "%");
            }
            if (!Strings.isEmpty(filter.getUktzed())) {
                where.append(" AND ct.code LIKE :uktzed ");
                params.put("uktzed", "%" + filter.getUktzed() + "%");
            }
            if (filter.getParent() != null) {
                where.append(" AND cm.parent = :parent");
                params.put("parent", filter.getParent());
            }
        }
        return where;
    }

    private StringBuilder orderBy(OrderBy orderBy, boolean asc) {
        StringBuilder order = new StringBuilder(" ORDER BY ");
        switch (orderBy) {
            case ID:
                order.append(" cm.id ");
                break;
            case NAME:
                order.append(" 2 ");
                break;
            case UKTZED:
                order.append(" ct.code ");
                break;
            case PARENT_NAME:
                order.append(" 2 ");
                break;
        }
        order.append(asc ? " ASC" : " DESC");
        return order;
    }

    public int size(CargoModeFilter filter, Locale currentLocale, ShowBooksMode showBooksMode) {
        Map<String, Object> params = newParams();
        StringBuilder query = select(true, params, null, currentLocale);
        query.append(where(filter, params, showBooksMode));

        TypedQuery<Long> typedQuery = entityManager.createQuery(query.toString(), Long.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            typedQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return ((Long) typedQuery.getSingleResult()).intValue();
    }

    public void saveAsNew(CargoMode cargoMode) {
        Long oldId = cargoMode.getId();
        clearBook(cargoMode);
        updateVersionIfNecessary(cargoMode, null);
        setNeedToUpdateReferences(cargoMode);
        updateReferences(cargoMode);
        saveOrUpdate(cargoMode, oldId);
        disable(oldId);
    }

    public void saveOrUpdate(CargoMode cargoMode, Long oldId) {
        if (cargoMode.getId() != null) {
            //cargo types
            List<Long> toRemove = new ArrayList<Long>();
            List<Long> dbCargoTypes =
                    entityManager.createQuery("SELECT cmct.cargoType.id FROM CargoModeCargoType cmct WHERE cmct.cargoMode = :cargoMode", Long.class).
                    setParameter("cargoMode", cargoMode).
                    getResultList();

            for (Long db : dbCargoTypes) {
                boolean needToRemove = true;

                for (CargoModeCargoType ui : cargoMode.getCargoModeCargoTypes()) {
                    if (db.equals(ui.getCargoType().getId())) {
                        needToRemove = false;
                        break;
                    }
                }

                if (needToRemove) {
                    toRemove.add(db);
                }
            }
            if (!toRemove.isEmpty()) {
                StringBuilder query = new StringBuilder("DELETE CargoModeCargoType cmct WHERE cmct.cargoMode = :cargoMode AND cmct.cargoType.id IN (");
                for (int i = 0; i < toRemove.size(); i++) {
                    query.append(toRemove.get(i));
                    if (i < toRemove.size() - 1) {
                        query.append(", ");
                    }

                    DeletedEmbeddedId deleted = createDeletedEntry(cargoMode.getId(), CargoModeCargoType.class, toRemove.get(i));
                    entityManager.merge(deleted);
                }
                query.append(")");

                entityManager.createQuery(query.toString()).setParameter("cargoMode", cargoMode).executeUpdate();
            }

            //unit types
            toRemove = new ArrayList<Long>();
            List<Long> dbUnitTypes =
                    entityManager.createQuery("SELECT cmut.unitType.id FROM CargoModeUnitType cmut WHERE cmut.cargoMode = :cargoMode", Long.class).
                    setParameter("cargoMode", cargoMode).
                    getResultList();

            for (Long db : dbUnitTypes) {
                boolean needToRemove = true;

                for (CargoModeUnitType ui : cargoMode.getCargoModeUnitTypes()) {
                    if (db.equals(ui.getUnitType().getId())) {
                        needToRemove = false;
                        break;
                    }
                }

                if (needToRemove) {
                    toRemove.add(db);
                }
            }
            if (!toRemove.isEmpty()) {
                StringBuilder query = new StringBuilder("DELETE CargoModeUnitType cmut WHERE cmut.cargoMode = :cargoMode AND cmut.unitType.id IN (");
                for (int i = 0; i < toRemove.size(); i++) {
                    query.append(toRemove.get(i));
                    if (i < toRemove.size() - 1) {
                        query.append(", ");
                    }
                    DeletedEmbeddedId deleted = createDeletedEntry(cargoMode.getId(), CargoModeUnitType.class, toRemove.get(i));
                    entityManager.merge(deleted);
                }
                query.append(")");
                entityManager.createQuery(query.toString()).setParameter("cargoMode", cargoMode).executeUpdate();
            }
        }

        bookDAO.saveOrUpdate(cargoMode);

        Session session = HibernateSessionTransformer.getSession(entityManager);
        for (CargoModeCargoType cmct : cargoMode.getCargoModeCargoTypes()) {
            cmct.getId().setCargoModeId(cargoMode.getId());
            session.saveOrUpdate(cmct);
        }
        for (CargoModeUnitType cmut : cargoMode.getCargoModeUnitTypes()) {
            cmut.getId().setCargoModeId(cargoMode.getId());
            session.saveOrUpdate(cmut);
        }

        if (oldId != null) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT cm FROM CargoMode cm WHERE cm.parent.id = :parentId AND cm.").
                    append(getDisabledPropertyName()).append(" = FALSE");
            List<CargoMode> children = entityManager.createQuery(queryBuilder.toString(), CargoMode.class).
                    setParameter("parentId", oldId).
                    getResultList();
            if (!children.isEmpty()) {
                bookDAO.addLocalizationSupport(children);
                entityManager.flush();
                entityManager.clear();

                for (CargoMode child : children) {
                    clearBook(child);
                    child.setParent(cargoMode);
                    updateVersionIfNecessary(child, null);
                    setNeedToUpdateReferences(child);
                    updateReferences(child);
                    saveOrUpdate(child, null);
                }
            }
        }
    }

    public void setNeedToUpdateReferences(CargoMode cargoMode) {
        for (CargoModeCargoType cmct : cargoMode.getCargoModeCargoTypes()) {
            cmct.setNeedToUpdateVersion(true);
        }
        for (CargoModeUnitType cmut : cargoMode.getCargoModeUnitTypes()) {
            cmut.setNeedToUpdateVersion(true);
        }
    }

    public void updateReferences(CargoMode cargoMode) {
        Date newVersion = DateUtil.getCurrentDate();
        for (CargoModeCargoType cmct : cargoMode.getCargoModeCargoTypes()) {
            if (cmct.isNeedToUpdateVersion()) {
                cmct.setUpdated(newVersion);
            }
        }
        for (CargoModeUnitType cmut : cargoMode.getCargoModeUnitTypes()) {
            if (cmut.isNeedToUpdateVersion()) {
                cmut.setUpdated(newVersion);
            }
        }
    }

    private DeletedEmbeddedId createDeletedEntry(Long cargoModeId, Class entityType, Long id) {
        DeletedEmbeddedId.Id ID =
                new DeletedEmbeddedId.Id(String.valueOf(cargoModeId) + DELETED_PRIMARY_KEY_SEPARATOR + String.valueOf(id),
                entityType.getName());
        DeletedEmbeddedId deleted = new DeletedEmbeddedId(ID, DateUtil.getCurrentDate());
        return deleted;
    }

    public List<CargoType> getAvailableCargoTypes(String search, int count, List<CargoType> exclude) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT DISTINCT ct FROM CargoType ct WHERE ct.").
                append(getDisabledPropertyName()).
                append(" = FALSE AND ct.code LIKE :search ");

        if (exclude != null && !exclude.isEmpty()) {
            queryBuilder.append(" AND ct.id NOT IN (");
            for (int i = 0; i < exclude.size(); i++) {
                CargoType cargoTypeToExclude = exclude.get(i);
                queryBuilder.append(cargoTypeToExclude.getId());
                if (i < exclude.size() - 1) {
                    queryBuilder.append(", ");
                }
            }
            queryBuilder.append(")");
        }
        queryBuilder.append(" ORDER BY ct.code");

        TypedQuery<CargoType> typedQuery = entityManager.createQuery(queryBuilder.toString(), CargoType.class).
                setParameter("search", "%" + search + "%").
                setMaxResults(count);
        return typedQuery.getResultList();
    }

    public List<UnitType> getAvailableUnitTypes(List<UnitType> exclude) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ut FROM UnitType ut ");

        if (exclude != null && !exclude.isEmpty()) {
            query.append(" WHERE ut.id NOT IN (");
            for (int i = 0; i < exclude.size(); i++) {
                UnitType unitTypeToExclude = exclude.get(i);
                query.append(unitTypeToExclude.getId());
                if (i < exclude.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
        }
        query.append(" ORDER BY ut.id");

        return entityManager.createQuery(query.toString(), UnitType.class).getResultList();
    }

    public List<CargoMode> getRootCargoModes() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT cm FROM CargoMode cm WHERE cm.parent IS NULL");
        List<CargoMode> rootCargoModes = entityManager.createQuery(queryBuilder.toString(), CargoMode.class).getResultList();
        bookDAO.addLocalizationSupport(rootCargoModes);
        return rootCargoModes;
    }

    public void disable(Long cargoModeId) {
        bookDAO.disable(cargoModeId, CargoMode.class);
        changeChildrenActivity(cargoModeId, true);
    }

    public void enable(Long cargoModeId) {
        bookDAO.enable(cargoModeId, CargoMode.class);
        changeChildrenActivity(cargoModeId, false);
    }

    private void changeChildrenActivity(Long parentId, boolean disabled) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE CargoMode cm SET cm.").append(getDisabledPropertyName()).append(" = :disabled, ").
                append("cm.").append(getVersionPropertyName()).append(" = :updated ").
                append("WHERE cm.parent.id = :parentId");
        entityManager.createQuery(queryBuilder.toString()).
                setParameter("disabled", disabled).
                setParameter("parentId", parentId).
                setParameter("updated", DateUtil.getCurrentDate()).
                executeUpdate();
    }
}
