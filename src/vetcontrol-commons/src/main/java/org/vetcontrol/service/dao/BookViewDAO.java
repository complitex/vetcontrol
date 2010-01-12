/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.util.string.Strings;
import org.hibernate.Criteria;
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
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
@Stateless
public class BookViewDAO implements IBookViewDAO {

    private static final List<Class> SIMPLE_TYPIES = Arrays.asList(new Class[]{
                int.class, byte.class, short.class, long.class, double.class, float.class,
                Integer.class, Byte.class, Short.class, Long.class, Double.class, Float.class,
                String.class, List.class, Set.class, Map.class
            });
    private EntityManager em;

    @PersistenceContext
    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected EntityManager getEntityManager() {
        return em;
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
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        try {
            DetachedCriteria query = query(example);
            List<T> results = query.getExecutableCriteria(session).setFirstResult(first).setMaxResults(count).list();
            addLocalizationSupport(results);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> getContent(Class<T> bookClass) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        try {
            T example = bookClass.newInstance();
            DetachedCriteria query = query(example);
            List<T> results = query.getExecutableCriteria(session).setFirstResult(0).list();
            addLocalizationSupport(results);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLocalizationSupport(Object entity) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        try {
            prepareLocalizableStrings(entity, BeanPropertyUtil.getMappedProperties(entity.getClass()), session, 0, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLocalizationSupport(List<?> entities) {
        for (Object entity : entities) {
            addLocalizationSupport(entity);
        }
    }

    private <T> void prepareLocalizableStrings(T bean, Map<PropertyDescriptor, PropertyDescriptor> mappedProperties, Session session,
            int currentDepth, int availableDepth) throws IllegalArgumentException, HibernateException, IllegalAccessException, InvocationTargetException, IntrospectionException {
        if (currentDepth <= availableDepth) {
            if (bean != null) {
                for (PropertyDescriptor prop : mappedProperties.keySet()) {
                    PropertyDescriptor mappedProperty = mappedProperties.get(prop);
                    Method setter = prop.getWriteMethod();
                    Object id = mappedProperty.getReadMethod().invoke(bean);
                    List<StringCulture> strings = session.createCriteria(StringCulture.class).
                            addOrder(Order.asc("id.locale")).
                            add(Restrictions.eq("id.id", id)).
                            list();
                    for (StringCulture culture : strings) {
                        if (culture.getValue() == null) {
                            culture.setValue("");
                        }
                    }
                    if (strings != null && !strings.isEmpty()) {
                        mappedProperty.getWriteMethod().invoke(bean, strings.get(0).getId().getId());
                    }
                    setter.invoke(bean, strings);
                }

                for (Property prop : BeanPropertyUtil.getProperties(bean.getClass())) {
//                        if (prop.isBeanReference()) {
//                            prepareLocalizableStrings(BeanPropertyUtil.getPropertyValue(currentResult, prop.getName()),
//                                    BeanPropertyUtil.getMappedProperties(prop.getType()), session, currentDepth + 1, availableDepth);
//                        }
                    Class propType = prop.getType();
                    boolean isSuitableType = true;
                    for (Class simpleType : SIMPLE_TYPIES) {
                        if (simpleType.isAssignableFrom(propType)) {
                            isSuitableType = false;
                            break;
                        }
                    }
                    if (isSuitableType) {
                        Object propValue = BeanPropertyUtil.getPropertyValue(bean, prop.getName());
                        prepareLocalizableStrings(propValue, BeanPropertyUtil.getMappedProperties(propType), session, currentDepth + 1, availableDepth);
                    }
                }
            }
        }
    }

    private <T> DetachedCriteria query(T example) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class bookType = example.getClass();
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = BeanPropertyUtil.getMappedProperties(bookType);

        DetachedCriteria query = DetachedCriteria.forClass(bookType, "book").
                setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).
                addOrder(Order.asc("id"));

        final List<Property> properties = BeanPropertyUtil.getProperties(bookType);

        Example exampleBook = Example.create(example).
                ignoreCase().
                enableLike(MatchMode.ANYWHERE).
                setPropertySelector(new Example.PropertySelector() {

            @Override
            public boolean include(Object propertyValue, String propertyName, Type type) {
                if (propertyValue != null) {
                    for (Property property : properties) {

                        if (!property.isLocalizable() && !property.isBeanReference() && property.getName().equals(propertyName)) {
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

        //filter by book referenced info.
        for (Property prop : BeanPropertyUtil.getProperties(bookType)) {
            if (prop.isBeanReference()) {
                Object value = BeanPropertyUtil.getPropertyValue(example, prop.getName());
                if (value != null) {
                    query.add(Restrictions.eq(prop.getName(), value));
                }
            }
        }

        return query;
    }
}
