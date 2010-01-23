package org.vetcontrol.service;

import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.UnitType;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.01.2010 23:46:19
 */
@Stateless(name = "CargoTypeBean")
public class CargoTypeBean {
    @PersistenceContext
    private EntityManager entityManager;

    public List<CargoType> getCargoTypesByName(String filter, int count){
        return entityManager
                .createQuery("select distinct(ct) from CargoType ct left join ct.namesMap n where n.value like :filter order by n.value", CargoType.class)
                .setParameter("filter", "%" + filter + "%")
                .setMaxResults(count)
                .getResultList();
    }

     public List<CargoType> getCargoTypesByCode(String filter, int count){
        return entityManager
                .createQuery("select distinct(ct) from CargoType ct where ct.code like :filter order by ct.code", CargoType.class)
                .setParameter("filter", "%" + filter + "%")
                .setMaxResults(count)
                .getResultList();
    }

     public CargoType getCargoType(String code){
        try {
            return entityManager.createQuery("select ct from CargoType ct where ct.code = :code", CargoType.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<UnitType> getUnitTypes(CargoType cargoType){
        return entityManager.createQuery("select ut from CargoType ct, CargoMode cm left join cm.unitType ut " +
                "where ct = :cargoType and ct.code = cm.code", UnitType.class)
                .setParameter("cargoType", cargoType)
                .getResultList();
    }

}
