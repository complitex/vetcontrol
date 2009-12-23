/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Artem
 */
public abstract class AbstractGenericDAO<T extends Serializable, ID extends Serializable> implements GenericDAO<T, ID> {

    private Class<T> persistentClass;
    private EntityManager entityManager;

    public AbstractGenericDAO() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

//    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public T findById(ID id) {
        return getEntityManager().find(getPersistentClass(), id);
    }

    public T saveOrUpdate(T entity) {
        return getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(entity);
    }

//    protected List<T> findByCriteria(Criterion... criterions) {
//        Criteria criteria = ((Session)(getEntityManager().getDelegate())).createCriteria(getPersistentClass());
//        for (Criterion criterion : criterions) {
//            criteria.add(criterion);
//        }
//
//        return criteria.list();
//    }

    public List<T> findAll() {
        CriteriaQuery query = getEntityManager().getCriteriaBuilder().createQuery();
        Root root = query.from(getPersistentClass());
        query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }


}
