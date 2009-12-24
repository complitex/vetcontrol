/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vetcontrol.entity.Locale;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class LocaleDAO implements ILocaleDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Locale> all() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Locale> cq = cb.createQuery(Locale.class);
        Root<Locale> root = cq.from(Locale.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public Locale systemLocale() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Locale> cq = cb.createQuery(Locale.class);
        Root<Locale> root = cq.from(Locale.class);
        cq.where(cb.equal(root.get("isSystem"), true));
        cq.select(root);
        return em.createQuery(cq).getSingleResult();
    }
}
