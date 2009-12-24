/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.util.string.Strings;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.Type;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.information.util.model.annotation.MappedProperty;
import org.vetcontrol.information.util.service.dao.HibernateSessionTransformer;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Property;

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
        List<T> results = session.createCriteria(bookType).addOrder(Order.asc("id")).setFirstResult(first).setMaxResults(count).list();

        try {
            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = getMappedProperties(bookType);
            prepareLocalizableStrings(results, mappedProperties, session);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    private <T> void prepareLocalizableStrings(List<T> results, Map<PropertyDescriptor, PropertyDescriptor> mappedProperties, Session session) throws IllegalArgumentException, HibernateException, IllegalAccessException, InvocationTargetException {
        for (T currentResult : results) {
            for (PropertyDescriptor prop : mappedProperties.keySet()) {
                PropertyDescriptor mappedProperty = mappedProperties.get(prop);
                Method setter = prop.getWriteMethod();
                Object id = mappedProperty.getReadMethod().invoke(currentResult);
                List<StringCulture> subResults = session.createCriteria(StringCulture.class).addOrder(Order.asc("id.locale")).add(Restrictions.eq("id.id", id)).list();
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
    }

    private Map<PropertyDescriptor, PropertyDescriptor> getMappedProperties(Class bookType) throws IntrospectionException {
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
        return mappedProperties;
    }

    @Override
    public void saveOrUpdate(Serializable book) {

        try {
            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = getMappedProperties(book.getClass());

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
    public <T> Long size(T example) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        try {
            DetachedCriteria query = query(example);
            return (Long) query.getExecutableCriteria(session).setProjection(Projections.rowCount()).uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> getContent(T example, int first, int count) {
        Class bookType = example.getClass();

        Session session = HibernateSessionTransformer.getSession(em);
        try {
            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = getMappedProperties(bookType);
            DetachedCriteria query = query(example);
            List<T> results = query.getExecutableCriteria(session).setFirstResult(first).setMaxResults(count).list();
            prepareLocalizableStrings(results, mappedProperties, session);

            return results;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> DetachedCriteria query(T example) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class bookType = example.getClass();
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = getMappedProperties(bookType);

        DetachedCriteria query = DetachedCriteria.forClass(bookType, "book").addOrder(Order.asc("id"));

        final List<Property> properties = BeanPropertyUtil.filter(bookType);

        Example exampleBook = Example.create(example).ignoreCase().enableLike(MatchMode.ANYWHERE).setPropertySelector(new Example.PropertySelector() {

            @Override
            public boolean include(Object propertyValue, String propertyName, Type type) {
                if (propertyValue != null) {
                    for (Property property : properties) {
                        if (!property.isLocalizable() && property.getName().equals(propertyName)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        query.add(exampleBook);

        //filter by StringCulture values.
        for (PropertyDescriptor prop : mappedProperties.keySet()) {
            String mappedPropName = mappedProperties.get(prop).getName();
            DetachedCriteria subquery = DetachedCriteria.forClass(StringCulture.class, mappedPropName).setProjection(org.hibernate.criterion.Property.forName("id.id"));
            Method getter = prop.getReadMethod();
            List<StringCulture> localizableStrings = (List<StringCulture>) getter.invoke(example);
            if (!localizableStrings.isEmpty()) {
                String value = localizableStrings.get(0).getValue();
                if (!Strings.isEmpty(value)) {
                    subquery.add(Restrictions.ilike("value", value, MatchMode.ANYWHERE));
                    query.add(Subqueries.propertyIn(mappedPropName, subquery));
                }
            }
        }
        return query;
    }
}
