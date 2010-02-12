/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.wicket.util.string.Strings;
import org.hibernate.Session;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoModeCargoType;
import org.vetcontrol.entity.CargoModeUnitType;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.UnitType;
import org.vetcontrol.information.util.web.cargomode.CargoModeFilterBean;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CargoModeDAO {

    @EJB
    private IBookDAO bookDAO;

    public static enum OrderBy {

        ID, NAME, UKTZED
    }
    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, Object> newParams() {
        return new HashMap<String, Object>();
    }

    public List<CargoMode> getAll(CargoModeFilterBean filter, int first, int count, OrderBy orderBy, boolean asc, Locale currentLocale) {
        Map<String, Object> params = newParams();
        String query = select(false, params, currentLocale);
        query += where(filter, params);
        query += orderBy(orderBy, asc);

        TypedQuery<CargoMode> typedQuery = entityManager.createQuery(query, CargoMode.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            typedQuery.setParameter(entry.getKey(), entry.getValue());
        }
        List<CargoMode> cargoModes = typedQuery.setFirstResult(first).setMaxResults(count).getResultList();
        return cargoModes;
    }

    private String select(boolean forSize, Map<String, Object> params, Locale currentLocale) {
        params.put("locale", currentLocale.getLanguage());
        String prefix = "SELECT ";
        String suffix = " FROM CargoMode cm, StringCulture sc LEFT JOIN cm.cargoModeCargoTypes cmct LEFT JOIN cmct.cargoType ct "
                + "WHERE cm.name = sc.id.id AND sc.id.locale = :locale ";
        if (forSize) {
            return prefix + " COUNT(DISTINCT cm) " + suffix;
        } else {
            return prefix + " DISTINCT cm " + suffix;
        }
    }

    private String where(CargoModeFilterBean filter, Map<String, Object> params) {
        String where = "";
        if (filter != null) {
            if (!Strings.isEmpty(filter.getName())) {
                where += " AND cm.name IN (SELECT sc_name.id.id FROM StringCulture sc_name WHERE sc_name.value LIKE :name) ";
                params.put("name", "%" + filter.getName() + "%");
            }
            if (!Strings.isEmpty(filter.getUktzed())) {
                where += " AND ct.code LIKE :uktzed ";
                params.put("uktzed", "%" + filter.getUktzed() + "%");
            }
        }
        return where;
    }

    private String orderBy(OrderBy orderBy, boolean asc) {
        String order = " ORDER BY ";
        switch (orderBy) {
            case ID:
                order += " cm.id ";
                break;
            case NAME:
                order += " sc.value ";
                break;
            case UKTZED:
                order += " ct.code ";
                break;
        }
        order += asc ? " ASC" : " DESC";
        return order;
    }

    public int size(CargoModeFilterBean filter, Locale currentLocale) {
        Map<String, Object> params = newParams();
        String query = select(true, params, currentLocale);
        query += where(filter, params);

        TypedQuery<Long> typedQuery = entityManager.createQuery(query, Long.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            typedQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return ((Long) typedQuery.getSingleResult()).intValue();
    }

    public void saveOrUpdate(CargoMode cargoMode) {
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
                StringBuilder query = new StringBuilder("DELETE CargoModeCargoType cmct WHERE cmct.cargoType.id IN (");
                for (int i = 0; i < toRemove.size(); i++) {
                    query.append(toRemove.get(i));
                    if (i < toRemove.size() - 1) {
                        query.append(", ");
                    }
                }
                query.append(")");
                entityManager.createQuery(query.toString()).executeUpdate();
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
                StringBuilder query = new StringBuilder("DELETE CargoModeUnitType cmut WHERE cmut.unitType.id IN (");
                for (int i = 0; i < toRemove.size(); i++) {
                    query.append(toRemove.get(i));
                    if (i < toRemove.size() - 1) {
                        query.append(", ");
                    }
                }
                query.append(")");
                entityManager.createQuery(query.toString()).executeUpdate();
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
    }

    public List<CargoType> getAvailableCargoTypes(String search, int count, Long cargoModeId, List<CargoType> exclude) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT ct FROM CargoType ct "
                + "WHERE ct.code LIKE :search AND ct.id NOT IN (SELECT cmct.id.cargoTypeId FROM CargoModeCargoType cmct ");
        if (cargoModeId != null) {
            query.append("WHERE cmct.id.cargoModeId != :cargoModeId");
        }
        query.append(")");

        if (exclude != null && !exclude.isEmpty()) {
            query.append(" AND ct.id NOT IN (");
            for (int i = 0; i < exclude.size(); i++) {
                CargoType cargoTypeToExclude = exclude.get(i);
                query.append(cargoTypeToExclude.getId());
                if (i < exclude.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
        }
        query.append(" ORDER BY ct.code");

        TypedQuery<CargoType> typedQuery = entityManager.createQuery(query.toString(), CargoType.class).setParameter("search", "%" + search + "%").
                setMaxResults(count);
        if (cargoModeId != null) {
            typedQuery.setParameter("cargoModeId", cargoModeId);
        }
        return typedQuery.getResultList();
    }

    public List<UnitType> getAvailableUnitTypes(List<UnitType> exclude) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT ut FROM UnitType ut");
        
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

        TypedQuery<UnitType> typedQuery = entityManager.createQuery(query.toString(), UnitType.class);
        return typedQuery.getResultList();
    }
}
