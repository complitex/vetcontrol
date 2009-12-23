/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.util.string.Strings;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.information.util.model.annotation.MappedProperty;
import org.vetcontrol.information.util.service.dao.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
@Stateless
public class BookDAO implements IBookDAO {

    private Sequence sequence;
    private EntityManager em;

    @EJB
    @Override
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public <T> List<T> getBookContent(Class<T> bookType, int first, int count) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        List<T> results = session.createCriteria(bookType)
                            .addOrder(Order.asc("id"))
                            .setFirstResult(first)
                            .setMaxResults(count)
                            .list();

        try {
            //List prop to long prop
            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = new HashMap<PropertyDescriptor, PropertyDescriptor>();

            BeanInfo beanInfo = Introspector.getBeanInfo(bookType);
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                Method getter = prop.getReadMethod();
                MappedProperty mp = getter.getAnnotation(MappedProperty.class);
                if (mp != null && prop.getPropertyType().equals(List.class)) {
                    String mappedProperty = mp.value();
                    for (PropertyDescriptor prop2 : props) {
                        if (prop2.getName().equals(mappedProperty)) {
                            mappedProperties.put(prop, prop2);
                        }
                    }
                }
            }

            for (T currentResult : results) {
                for (PropertyDescriptor prop : mappedProperties.keySet()) {
                    PropertyDescriptor mappedProperty = mappedProperties.get(prop);
                    Method setter = prop.getWriteMethod();

                    Object id = mappedProperty.getReadMethod().invoke(currentResult);

                    List<StringCulture> subResults = session.createCriteria(StringCulture.class)
                                .addOrder(Order.asc("id.locale")).add(Restrictions.eq("id.id", id)).list();
                    //getEntityManager().createQuery("select s from StringCulture s where s.id.id = :id").setParameter("id", mappedProperty.getReadMethod().invoke(currentResult)).getResultList();
                    for (StringCulture culture : subResults) {
                        if (culture.getValue() == null) {
                            culture.setValue("");
                        }
                    }

                    setter.invoke(currentResult, subResults);
                    if (subResults != null && !subResults.isEmpty()) {
                        mappedProperty.getWriteMethod().invoke(currentResult, subResults.get(0).getId().getId());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    public void saveOrUpdate(Serializable book) {

        try {
            //List prop to long prop
            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = new HashMap<PropertyDescriptor, PropertyDescriptor>();

            BeanInfo beanInfo = Introspector.getBeanInfo(book.getClass());
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                Method getter = prop.getReadMethod();
                MappedProperty mp = getter.getAnnotation(MappedProperty.class);
                if (mp != null) {
                    String mappedProperty = mp.value();
                    for (PropertyDescriptor prop2 : props) {
                        if (prop2.getName().equals(mappedProperty)) {
                            mappedProperties.put(prop, prop2);
                        }
                    }
                }
            }

            for (PropertyDescriptor prop : mappedProperties.keySet()) {
                PropertyDescriptor mappedProperty = mappedProperties.get(prop);
                Method getter = prop.getReadMethod();

                List<StringCulture> localizableStrings = (List<StringCulture>) getter.invoke(book);
                if (!localizableStrings.isEmpty()) {
                    if (localizableStrings.get(0).getId().getId() == 0) {
                        long ID = sequence.next();
                        for (StringCulture culture : localizableStrings) {
                            culture.getId().setId(ID);

                            if (Strings.isEmpty(culture.getValue())) {
                                culture.setValue(null);
                            }

                            getEntityManager().merge(culture);
                        }
                        mappedProperty.getWriteMethod().invoke(book, ID);
                    }
                }
                for (StringCulture culture : localizableStrings) {
                    getEntityManager().merge(culture);
                }
            }
            getEntityManager().merge(book);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EntityManager getEntityManager() {
        return em;
    }

    @PersistenceContext
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Long size(Class bookType) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        return (Long) session.createCriteria(bookType).setProjection(Projections.rowCount()).uniqueResult();
    }
}
