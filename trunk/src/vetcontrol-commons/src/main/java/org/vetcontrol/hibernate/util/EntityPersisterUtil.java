/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.hibernate.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

import javax.persistence.EntityManager;

/**
 *
 * @author Artem
 */
public final class EntityPersisterUtil {

    private EntityPersisterUtil() {
    }

    /**
     * Inserts entity.
     * @param entityManager EntityManager
     * @param entity Entity Object
     * @throws HibernateException Hibernate wrapped exception
     */
    public static void insert(EntityManager entityManager, Object entity) throws HibernateException {
        Session session = getSession(entityManager);
        SessionImplementor sessionImplementor = getSessionImplementor(session);
        String entityName = getEntityName(session.getSessionFactory(), entity.getClass());
        EntityPersister persister = getEntityPersister(sessionImplementor, entityName, entity);
        persister.insert(persister.getIdentifier(entity, sessionImplementor), persister.getPropertyValues(entity, sessionImplementor.getEntityMode()),
                entity, sessionImplementor);
    }

    /**
     * Updates entity.
     * @param entityManager EntityManager
     * @param entity Entity Object
     * @throws HibernateException Hibernate wrapped exception
     */
    public static void update(EntityManager entityManager, Object entity) throws HibernateException {
        Session session = getSession(entityManager);
        SessionImplementor sessionImplementor = getSessionImplementor(session);
        String entityName = getEntityName(session.getSessionFactory(), entity.getClass());
        EntityPersister persister = getEntityPersister(sessionImplementor, entityName, entity);
        persister.update(persister.getIdentifier(entity, sessionImplementor), persister.getPropertyValues(entity, sessionImplementor.getEntityMode()),
                null, false, null, null, entity, null, sessionImplementor);
    }

    /**
     * Executes sql statements batch. MUST be invoked right before transaction commit.
     * @param entityManager EntityManager
     * @throws org.hibernate.HibernateException Hibernate wrapped exception
     */
    public static void executeBatch(EntityManager entityManager) throws HibernateException {
        Session session = getSession(entityManager);
        SessionImplementor sessionImplementor = getSessionImplementor(session);
        sessionImplementor.getBatcher().executeBatch();
    }

    /**
     * Single insert with batch execution 
     * @param entityManager EntityManager
     * @param entity Entity Object
     */
    public static void executeInsert(EntityManager entityManager, Object entity){
        insert(entityManager, entity);
        executeBatch(entityManager);
    }

    /**
     * Single update with batch execution
     * @param entityManager EntityManager
     * @param entity Entity Object
     */
    public static void executeUpdate(EntityManager entityManager, Object entity){
        update(entityManager, entity);
        executeBatch(entityManager);
    }

    private static Session getSession(EntityManager entityManager){
        return HibernateSessionTransformer.getSession(entityManager);
    }

    private static SessionImplementor getSessionImplementor(Session session){
        if (!(session instanceof SessionImplementor)) {
            throw new RuntimeException("Internal hibernate session implementation doesn't implement SessionImplementor interface anymore.");
        }
        return (SessionImplementor) session;
    }

    private static String getEntityName(SessionFactory sessionFactory, Class entityClazz){
        return sessionFactory.getClassMetadata(entityClazz).getEntityName();
    }

    private static EntityPersister getEntityPersister(SessionImplementor sessionImplementor, String entityName, Object entity) throws HibernateException{
        return sessionImplementor.getEntityPersister(entityName, entity);
    }

}