/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.generator;

import java.sql.Connection;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import org.hibernate.ejb.EntityManagerImpl;
import org.vetcontrol.information.model.Generator;

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
        Generator g = em.find(Generator.class, BOOK_GENERATOR);
        em.lock(g, LockModeType.READ);
        g.setGeneratorValue(g.getGeneratorValue() + 1);
        g = em.merge(g);
        return g.getGeneratorValue();
    }
}
