/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.db.populate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import org.vetcontrol.db.populate.util.GenerateUtil;

/**
 *
 * @author Artem
 */
public abstract class AbstractPopulator {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public AbstractPopulator() {
        entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected int count(Class entity) {
        return entityManager.createQuery("SELECT COUNT(DISTINCT e) FROM " + entity.getSimpleName() + " e", Long.class).getSingleResult().intValue();
    }

    protected <T> T findAny(Class<T> entityClass) {
        int count = count(entityClass);
        if (count == 0) {
            throw new RuntimeException("Count is equal to 0.");
        }
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).
                setFirstResult(GenerateUtil.generateInt(count)).
                setMaxResults(1).
                getResultList().get(0);
    }

    protected void startTransaction() {
        entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
    }

    protected void endTransaction() {
        try {
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    protected abstract void populate();

    protected abstract String getPersistenceUnitName();
}
