/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.hibernate.util;

import javax.persistence.EntityManager;
import org.hibernate.Session;

/**
 *
 * @author Artem
 */
public final class HibernateSessionTransformer {

    private HibernateSessionTransformer() {
    }

//    public static Session getSession(EntityManager em) {
//        if (em instanceof EntityManagerImpl) {
//            return ((EntityManagerImpl) em).getSession();
//        } else if ((em.getDelegate()) instanceof EntityManagerImpl) {
//            return ((EntityManagerImpl) em.getDelegate()).getSession();
//        }
//        throw new RuntimeException("Can't transform entity manager to hibernate session.");
//    }
    /**
     * New version of transformation using EntityManager's unwrap method of JPA2 API.
     */
    public static Session getSession(EntityManager entityManager) {
        return entityManager.unwrap(Session.class);
    }
}
