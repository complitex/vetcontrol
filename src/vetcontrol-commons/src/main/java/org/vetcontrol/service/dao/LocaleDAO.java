/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Artem
 */
@Stateless
public class LocaleDAO implements ILocaleDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Locale> all() {
        List<org.vetcontrol.entity.Locale> all = em.createQuery("select l from Locale l").getResultList();
        return convertAll(all);

        //TODO: to sort out server start problem!!!
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<org.vetcontrol.entity.Locale> cq = cb.createQuery(org.vetcontrol.entity.Locale.class);
//        Root<org.vetcontrol.entity.Locale> root = cq.from(org.vetcontrol.entity.Locale.class);
//        cq.select(root);
//        return convertAll(em.createQuery(cq).getResultList());
    }

    @Override
    public Locale systemLocale() {
        org.vetcontrol.entity.Locale locale = em.createQuery("select l from Locale l where l.system is true", org.vetcontrol.entity.Locale.class).
                getSingleResult();
        return convertLocale(locale);

        //TODO: to sort out server start problem!!!
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<org.vetcontrol.entity.Locale> cq = cb.createQuery(org.vetcontrol.entity.Locale.class);
//        Root<org.vetcontrol.entity.Locale> root = cq.from(org.vetcontrol.entity.Locale.class);
//        cq.where(cb.equal(root.get("system"), true));
//        cq.select(root);
//        return convertLocale(em.createQuery(cq).getSingleResult());
    }

    private Locale convertLocale(org.vetcontrol.entity.Locale l){
        return new Locale(l.getLanguage());
    }

    private List<Locale> convertAll(List<org.vetcontrol.entity.Locale> locales){
        List<Locale> result = new ArrayList<Locale>();
        for (org.vetcontrol.entity.Locale locale : locales) {
            result.add(convertLocale(locale));
        }
        return result;
    }
}
