package org.vetcontrol.service;

import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.UnitType;
import org.vetcontrol.book.BeanPropertyUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.01.2010 23:46:19
 */
@Stateless(name = "CargoTypeBean")
public class CargoTypeBean {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<CargoType> getCargoTypesByName(String filter, int count) {
        return entityManager.createQuery("select distinct(ct) from CargoType ct left join ct.namesMap n where n like :filter"
                + " and ct." + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE order by n", CargoType.class).
                setParameter("filter", "%" + filter + "%").
                setMaxResults(count).
                getResultList();
    }

    public List<CargoType> getCargoTypesByName(CargoMode cargoMode, String filter, int count) {
        if (cargoMode == null){
            return getCargoTypesByName(filter, count);
        }

        return entityManager.createQuery("select distinct(c.cargoType) from CargoModeCargoType c " +
                "left join c.cargoType.namesMap n where n like :filter" +
                " and c.cargoMode = :cargoMode" +
                " and c.cargoType." + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE order by n", CargoType.class)
                .setParameter("filter", "%" + filter + "%")
                .setParameter("cargoMode", cargoMode)
                .setMaxResults(count)
                .getResultList();
    }

    public List<CargoType> getCargoTypesByCode(String filter, int count) {
        return entityManager.createQuery("select distinct(ct) from CargoType ct where ct.code like :filter and ct."
                + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE order by ct.code", CargoType.class).
                setParameter("filter", "%" + filter + "%").
                setMaxResults(count).
                getResultList();
    }
    
    public List<CargoType> getCargoTypesByCode(CargoMode cargoMode, String filter, int count) {
        if (cargoMode == null){
            return getCargoTypesByCode(filter, count);
        }

        return entityManager.createQuery("select distinct(c.cargoType) from CargoModeCargoType c " +
                "where c.cargoType.code like :filter and c.cargoMode = :cargoMode and c.cargoType."
                + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE order by c.cargoType.code", CargoType.class)
                .setParameter("filter", "%" + filter + "%")
                .setParameter("cargoMode", cargoMode)
                .setMaxResults(count)
                .getResultList();
    }

    public CargoType getCargoType(String code) {
        try {
            return entityManager.createQuery("select ct from CargoType ct where ct.code = :code and ct."
                    + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE", CargoType.class).
                    setParameter("code", code).
                    getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean hasCargoType(String code){
        return entityManager.createQuery("select count(ct) from CargoType ct where ct.code = :code and ct."
                    + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE", Long.class).
                    setParameter("code", code).
                    getSingleResult() == 1;       
    }

    @Deprecated
    public List<UnitType> getUnitTypes(CargoType cargoType) {
        CargoMode cargoMode;
        try {
            cargoMode = entityManager.createQuery("SELECT cm FROM CargoMode cm WHERE cm.id IN "
                    + "(SELECT cmct.id.cargoModeId FROM CargoModeCargoType cmct WHERE cmct.cargoType = :cargoType) AND cm."
                    + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE", CargoMode.class).
                    setParameter("cargoType", cargoType).
                    getSingleResult();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }

        return entityManager.createQuery("SELECT ut FROM CargoModeUnitType cmut LEFT JOIN cmut.unitType ut WHERE cmut.cargoMode = :cargoMode AND "
                + "ut." + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE", UnitType.class).
                setParameter("cargoMode", cargoMode).
                getResultList();
    }

    public Map<CargoMode, List<CargoMode>> getCargoMode(CargoType cargoType){
        List<CargoMode> list  = entityManager.createQuery("select c.cargoMode from CargoModeCargoType c " +
                "where c.cargoType = :cargoType", CargoMode.class)
                .setParameter("cargoType", cargoType)
                .getResultList();

        Map<CargoMode, List<CargoMode>> map = new HashMap<CargoMode, List<CargoMode>>();

        for (CargoMode cm : list){
            CargoMode parent = cm.getParent();

            if (parent != null){
                if (map.get(parent) == null){
                    map.put(parent, new ArrayList<CargoMode>());
                }
                map.get(parent).add(cm);
            }
        }

        return map;
    }
}
