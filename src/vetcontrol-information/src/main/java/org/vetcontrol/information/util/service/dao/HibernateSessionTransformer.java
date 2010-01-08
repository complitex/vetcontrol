/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.util.service.dao;

import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;

/**
 *
 * @author Artem
 */
public class HibernateSessionTransformer {

    public static Session getSession(EntityManager em){
        if(em instanceof EntityManagerImpl){
            return ((EntityManagerImpl)em).getSession();
        }else if((em.getDelegate()) instanceof EntityManagerImpl){
            return ((EntityManagerImpl)em.getDelegate()).getSession();
        }
        throw new RuntimeException("Can't transform entity manager to hibernate session.");
    }

}