/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.hibernate.util;

import javax.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
public final class EntityPersisterUtil {

    private static final String EXCEPTION_MESSAGE = "Internal hibernate session implementation doesn't implement SessionImplementor interface anymore.";

    private EntityPersisterUtil() {
    }

    /**
     * Inserts entity.
     * @param entityManager
     * @param entity
     * @throws HibernateException
     */
    public static void insert(EntityManager entityManager, Object entity) throws HibernateException {
        Session session = HibernateSessionTransformer.getSession(entityManager);
        if (!(session instanceof SessionImplementor)) {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        String entityName = session.getSessionFactory().getClassMetadata(entity.getClass()).getEntityName();
        EntityPersister persister = sessionImplementor.getEntityPersister(entityName, entity);
        persister.insert(persister.getIdentifier(entity, sessionImplementor), persister.getPropertyValues(entity, sessionImplementor.getEntityMode()),
                entity, sessionImplementor);
    }

    /**
     * Updates entity.
     * @param entityManager
     * @param entity
     * @throws HibernateException
     */
    public static void update(EntityManager entityManager, Object entity) throws HibernateException {
        Session session = HibernateSessionTransformer.getSession(entityManager);
        if (!(session instanceof SessionImplementor)) {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        String entityName = session.getSessionFactory().getClassMetadata(entity.getClass()).getEntityName();
        EntityPersister persister = sessionImplementor.getEntityPersister(entityName, entity);
        persister.update(persister.getIdentifier(entity, sessionImplementor), persister.getPropertyValues(entity, sessionImplementor.getEntityMode()),
                null, false, null, null, entity, null, sessionImplementor);
    }

    /**
     * Executes sql statements batch. MUST be invoked right before transaction commit.
     * @param entityManager
     */
    public static void executeBatch(EntityManager entityManager) throws HibernateException {
        Session session = HibernateSessionTransformer.getSession(entityManager);
        if (!(session instanceof SessionImplementor)) {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        sessionImplementor.getBatcher().executeBatch();
    }
}
