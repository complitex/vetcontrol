package org.vetcontrol.service;

import java.util.Collections;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.UnitType;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.util.book.BeanPropertyUtil;

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

    public List<CargoType> getCargoTypesByCode(String filter, int count) {
        return entityManager.createQuery("select distinct(ct) from CargoType ct where ct.code like :filter and ct."
                + BeanPropertyUtil.getDisabledPropertyName() + " = FALSE order by ct.code", CargoType.class).
                setParameter("filter", "%" + filter + "%").
                setMaxResults(count).
                getResultList();
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

    public List<UnitType> getUnitTypes(CargoType cargoType) {
        CargoMode cargoMode = null;
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
}
