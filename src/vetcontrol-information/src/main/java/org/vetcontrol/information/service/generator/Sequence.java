/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.generator;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.vetcontrol.entity.Generator;
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
@Singleton
public class Sequence {

    private static final String BOOK_GENERATOR = "books";
    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long next() {
        Session session = HibernateSessionTransformer.getSession(em);
        Generator g = (Generator)session.get(Generator.class, BOOK_GENERATOR, LockMode.UPGRADE);
        g.setGeneratorValue(g.getGeneratorValue() + 1);
        g = (Generator)session.merge(g);
        return g.getGeneratorValue();
    }
}
