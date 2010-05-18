/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao;

import org.apache.wicket.util.string.Strings;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.type.Type;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.book.Property;
import org.vetcontrol.book.annotation.UIType;
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.hibernate.SQLQuery;
import org.vetcontrol.book.ShowBooksMode;

import static org.vetcontrol.book.BeanPropertyUtil.*;

/**
 *
 * @author Artem
 */
@Stateless(name = "BookViewDAO")
@RolesAllowed(SecurityRoles.INFORMATION_VIEW)
public class BookViewDAO implements IBookViewDAO {

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
    public <T> Long size(T example, ShowBooksMode showBooksMode) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        return ((Number) queryForSize(session, example, showBooksMode).uniqueResult()).longValue();
    }

    @Override
    public <T> List<T> getContent(T example, int first, int count, String sortProperty, boolean isAscending, Locale locale, ShowBooksMode showBooksMode) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        List<T> results = queryForContent(session, example, sortProperty, isAscending, locale, showBooksMode).
                setFirstResult(first).setMaxResults(count).list();
        addLocalizationSupport(results);
        return results;
    }

    @Override
    public <T> List<T> getContent(Class<T> bookClass, ShowBooksMode showBooksMode) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        try {
            T example = bookClass.newInstance();
            List<T> results = queryForContent(session, example, null, null, null, showBooksMode).setFirstResult(0).list();
            addLocalizationSupport(results);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLocalizationSupport(Object entity) {
        Session session = HibernateSessionTransformer.getSession(getEntityManager());
        prepareLocalizableStrings(entity, session, 0, 1);
    }

    @Override
    public void addLocalizationSupport(List<?> entities) {
        for (Object entity : entities) {
            addLocalizationSupport(entity);
        }
    }

    private void applyParameters(Query query, QueryAndParameters queryWithParameters) throws HibernateException {
        for (String param : queryWithParameters.parameters.keySet()) {
            Object value = queryWithParameters.parameters.get(param);
            if (value instanceof Date) {
                query.setDate(param, (Date) value);
            } else {
                query.setParameter(param, value);
            }
        }
    }

    private Query createSQLQuery(Session session, QueryAndParameters queryWithParameters, boolean forSize) {
        SQLQuery query = session.createSQLQuery(queryWithParameters.query);
        if (!forSize) {
            query.addEntity(queryWithParameters.alias, queryWithParameters.entityType);
        }
        return query;
    }

    private <T> void prepareLocalizableStrings(T bean, Session session, int currentDepth, int availableDepth) {
        if (currentDepth <= availableDepth) {
            if (bean != null) {
                for (Property prop : getProperties(bean.getClass())) {
                    if (prop.isLocalizable()) {
                        Object id = getPropertyValue(bean, prop.getLocalizationForeignKeyProperty());
                        if (id != null) {
                            List<StringCulture> strings = session.createCriteria(StringCulture.class).
                                    addOrder(Order.asc("id.locale")).
                                    add(Restrictions.eq("id.id", id)).
                                    list();
                            setPropertyValue(bean, prop.getName(), strings);
//                    for (StringCulture culture : strings) {
//                        if (culture.getValue() == null) {
//                            culture.setValue("");
//                        }
//                    }
                        }
                    } else {
                        Class propType = prop.getType();
                        boolean isSuitableType = true;
                        for (Class simpleType : SIMPLE_TYPIES) {
                            if (simpleType.isAssignableFrom(propType)) {
                                isSuitableType = false;
                                break;
                            }
                        }
                        if (isSuitableType) {
                            Object propValue = getPropertyValue(bean, prop.getName());
                            prepareLocalizableStrings(propValue, session, currentDepth + 1, availableDepth);
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    private <T> DetachedCriteria query(T example, Order order) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class bookType = example.getClass();
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = getMappedProperties(bookType);

        DetachedCriteria query = DetachedCriteria.forClass(bookType, "book").
                setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (order != null) {
            query.addOrder(order);
        }

        final List<Property> properties = getProperties(bookType);

        Example exampleBook = Example.create(example).
                ignoreCase().
                enableLike(MatchMode.ANYWHERE).
                setPropertySelector(new Example.PropertySelector() {

            @Override
            public boolean include(Object propertyValue, String propertyName, Type type) {
                if (propertyValue != null) {
                    for (Property property : properties) {

                        if (!property.isLocalizable() && !property.isBookReference() && property.getName().equals(propertyName)) {
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
        for (Property prop : getProperties(bookType)) {
            if (prop.isBookReference()) {
                Object value = getPropertyValue(example, prop.getName());
                if (value != null) {
                    query.add(Restrictions.eq(prop.getName(), value));
                }
            }
        }

        return query;
    }

    private <T> QueryAndParameters query(T example, String sortProperty, Boolean ascending, Locale currentLocale,
            boolean forSize, ShowBooksMode showBooksMode) {
        Class bookType = example.getClass();
        Property sortProp = getPropertyByName(bookType, sortProperty);

        StringBuilder sql = new StringBuilder();

        //select
        String alias = "e";
        sql.append("SELECT");
        if (forSize) {
            sql.append(" COUNT(DISTINCT ").append(alias).append(".id)");
        } else {
            sql.append(" DISTINCT {").append(alias).append(".*}");
        }

        //from
        sql.append(" FROM ").append(getTableName(bookType)).append(" ").append(alias);

        //where
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" WHERE (1=1)");

        //disabed/enabled/all filter
        switch (showBooksMode) {
            case ALL:
                break;
            default:
                sql.append(" AND ").append(alias).append(".").
                        append(getDisabledColumnName()).append(" = :disabled ");
                params.put("disabled", showBooksMode.equals(ShowBooksMode.ENABLED) ? Boolean.FALSE : Boolean.TRUE);
                break;
        }

        //filters
        for (Property p : getProperties(bookType)) {
            Class propType = p.getType();
            Object propValue = getPropertyValue(example, p.getName());
            if (p.isBookReference()) {
                if (propValue != null) {
                    if (p.getUiType().equals(UIType.SELECT)) {
                        sql.append(" AND ").append(alias).append(".").append(p.getColumnName()).
                                append(" = :").append(p.getName());
                        params.put(p.getName(), getId(propValue));
                    } else if (p.getUiType().equals(UIType.AUTO_COMPLETE)) {
                        throw new UnsupportedOperationException("Search by auto complete field yet not implemented.");
                    }
                }
            } else if (p.isLocalizable()) {
                List<StringCulture> strings = (List<StringCulture>) propValue;
                if (strings != null && !strings.isEmpty()) {
                    String value = strings.get(0).getValue();
                    if (!Strings.isEmpty(value)) {
                        String stringCultureAlias = "sc_" + p.getName();
                        StringBuilder subquery = new StringBuilder();
                        subquery.append("SELECT ").append(stringCultureAlias).append(".id FROM ").
                                append(getTableName(StringCulture.class)).
                                append(" ").append(stringCultureAlias).append(" WHERE ").
                                append(stringCultureAlias).append(".value LIKE :").append(p.getName());
                        params.put(p.getName(), "%" + value + "%");

                        sql.append(" AND ").append(alias).append(".").append(p.getColumnName()).append(" IN (").
                                append(subquery).append(")");
                    }
                }
            } else if (isPrimitive(propType)) {
                if (propValue != null) {
                    sql.append(" AND ").append(alias).append(".").append(p.getColumnName());

                    if (!Date.class.isAssignableFrom(propType)) {
                        sql.append(" LIKE :");
                        params.put(p.getName(), "%" + propValue + "%");
                    } else {
                        sql.append(" = :");
                        params.put(p.getName(), propValue);
                    }
                    sql.append(p.getName());
                }
            } else {
                throw new UnsupportedOperationException("Not possible case.");
            }
        }
        sql.append(" ");

        //order by
        if (sortProp != null) {
            sql.append("ORDER BY ");
            if (sortProp.isLocalizable()) {
                sql.append("(").append(localizationOrderBy(currentLocale, params)).
                        append(alias).append(".").append(sortProp.getColumnName()).
                        append(")");
            } else if (sortProp.isBookReference()) {
                Class referencedBeanClass = sortProp.getType();
                String referencedPropName = sortProp.getReferencedField();
                Property referencedProperty = getPropertyByName(referencedBeanClass, referencedPropName);

                StringBuilder subquery = new StringBuilder();
                String referenceTableAlias = "ref";
                subquery.append("SELECT ").append(referenceTableAlias).append(".").append(referencedProperty.getColumnName()).
                        append(" FROM ").append(getTableName(referencedBeanClass)).append(" ").append(referenceTableAlias).append(" WHERE ").
                        append(referenceTableAlias).append(".id = ").append(alias).append(".").append(sortProp.getColumnName());

                if (referencedProperty.isLocalizable()) {
                    sql.append("(").append(localizationOrderBy(currentLocale, params)).
                            append("(").append(subquery).append(")").
                            append(")");
                } else {
                    sql.append("(").append(subquery).append(")");
                }
            } else {
                //simple property
                sql.append(alias).append(".").append(sortProp.getColumnName());
            }
        } else {
            if (sortProperty != null) {
                sql.append("ORDER BY ").append(alias).append(".").append(sortProperty);
            } else {
                //do not sort.
            }
        }
        if (ascending != null) {
            sql.append(" ").append(ascending ? "ASC" : "DESC");
        }

        return new QueryAndParameters(alias, bookType, sql.toString(), params);
    }

    private StringBuilder localizationOrderBy(Locale currentLocale, Map<String, Object> params) {
        StringBuilder orderBuilder = new StringBuilder();
        String stringCultureAlias = "sc";
        orderBuilder.append("SELECT ").append(stringCultureAlias).append(".value FROM ").append(getTableName(StringCulture.class)).
                append(" ").append(stringCultureAlias).append(" WHERE ").
                append(stringCultureAlias).append(".locale = :").append("currentLocale").
                append(" AND ").append(stringCultureAlias).append(".id = ");
        params.put("currentLocale", currentLocale.getLanguage());
        return orderBuilder;
    }

//    private <T> QueryWithParameters createQuery(T example, String sortProperty, Boolean isAscending, Locale locale, boolean size,
//            ShowBooksMode showBooksMode)
//            throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        Class bookType = example.getClass();
//        Property sortProp = BeanPropertyUtil.getPropertyByName(bookType, sortProperty);
//
//        StringBuilder query = new StringBuilder();
//
//        //select expression
//        if (size) {
//            query.append("SELECT COUNT(DISTINCT a) ");
//        } else {
//            query.append("SELECT DISTINCT a ");
//        }
//
//        //from expression
//        query.append("FROM ").append(bookType.getSimpleName()).append(" as a ");
//        if (!size) {
//            if (sortProp != null) {
//                if (sortProp.isLocalizable()) {
//                    query.append(", ").append(StringCulture.class.getSimpleName()).append(" as sc ");
//                } else if (sortProp.isBookReference()) {
//                    Class referencedBeanClass = sortProp.getType();
//                    String referencedPropName = sortProp.getReferencedField();
//                    Property referencedProperty = BeanPropertyUtil.getPropertyByName(referencedBeanClass, referencedPropName);
//                    if (referencedProperty.isLocalizable()) {
//                        query.append(", ").append(StringCulture.class.getSimpleName()).append(" as sc ");
//                    } else if (!referencedProperty.isBookReference()) {
//                        //simple property
//                    }
//                    query.append("LEFT JOIN a.").append(sortProp.getName()).append(" as ").append(sortProp.getName()).append(" ");
//                } else {
//                    // simple property
//                }
//            }
//        }
//
//        //where expression
//        query.append("WHERE (1=1) ");
//        Map<String, Object> queryParameters = new HashMap<String, Object>();
//
//        //disabed/enabled/all filter
//        switch (showBooksMode) {
//            case ALL:
//                break;
//            default:
//                query.append(" AND a.").append(BeanPropertyUtil.getDisabledPropertyName()).append(" = :disabled ");
//                queryParameters.put("disabled", showBooksMode.equals(ShowBooksMode.ENABLED) ? Boolean.FALSE : Boolean.TRUE);
//                break;
//        }
//
//        //join condition
//        String localeParameter = "currentLocale";
//
//        if (sortProp != null) {
//            if (sortProp.isLocalizable()) {
//                String localizationForeignKeyProp = sortProp.getLocalizationForeignKeyProperty();
//                query.append(" AND a.").append(localizationForeignKeyProp).append(" = sc.id.id").
//                        append(" AND sc.id.locale = :").append(localeParameter);
//                queryParameters.put(localeParameter, locale.getLanguage());
//            } else if (sortProp.isBookReference()) {
//                Class referencedBeanClass = sortProp.getType();
//                String referencedPropName = sortProp.getReferencedField();
//                Property referencedProperty = BeanPropertyUtil.getPropertyByName(referencedBeanClass, referencedPropName);
//                if (referencedProperty.isLocalizable()) {
//                    String localizationForeignKeyProp = referencedProperty.getLocalizationForeignKeyProperty();
//                    query.append("AND ").append(sortProp.getName()).append(".").append(localizationForeignKeyProp).append(" = sc.id.id").
//                            append(" AND sc.id.locale = :").append(localeParameter);
//                    queryParameters.put(localeParameter, locale.getLanguage());
//                } else {
//                    //simple property or bean referenced property
//                }
//            }
//        }
//
//        //filters
//        //filter by simple properties
//        for (Property p : BeanPropertyUtil.getProperties(bookType)) {
//            if (!p.isLocalizable() && !p.isBookReference()) {
//                boolean isPrimitive = false;
//                for (Class primitive : BeanPropertyUtil.PRIMITIVES) {
//                    if (primitive.isAssignableFrom(p.getType())) {
//                        isPrimitive = true;
//                        break;
//                    }
//                }
//                if (isPrimitive) {
//                    Object propValue = BeanPropertyUtil.getPropertyValue(example, p.getName());
//                    if (propValue != null) {
//                        query.append(" AND a.").append(p.getName());
//
//                        if (!Date.class.isAssignableFrom(p.getType())) {
//                            query.append(" like :");
//                            queryParameters.put(p.getName(), "%" + propValue + "%");
//                        } else {
//                            query.append(" = :");
//                            queryParameters.put(p.getName(), propValue);
//                        }
//                        query.append(p.getName());
//                    }
//                }
//            }
//        }
//
//        //filter by referenced books
//        for (Property p : BeanPropertyUtil.getProperties(bookType)) {
//            if (p.isBookReference()) {
//                Object propValue = BeanPropertyUtil.getPropertyValue(example, p.getName());
//                if (propValue != null) {
//                    if (p.getUiType().equals(UIType.SELECT)) {
//                        query.append(" AND a.").append(p.getName()).append(" = :").append(p.getName());
//                        queryParameters.put(p.getName(), propValue);
//                    } else if (p.getUiType().equals(UIType.AUTO_COMPLETE)) {
//                        Property referencedProperty = BeanPropertyUtil.getPropertyByName(p.getType(), p.getReferencedField());
//                        Object referencedPropertyValue = BeanPropertyUtil.getPropertyValue(propValue, referencedProperty.getName());
//                        if (referencedPropertyValue != null) {
//                            if (referencedProperty.isLocalizable()) {
//                                //TODO: need to implement
////                                throw new UnsupportedOperationException("Need to implement.");
//
//                                String localizationForeignKeyProp = referencedProperty.getLocalizationForeignKeyProperty();
//                                List<StringCulture> strings = (List<StringCulture>) BeanPropertyUtil.getPropertyValue(example,
//                                        referencedProperty.getName());
//                                if (strings != null && !strings.isEmpty()) {
//                                    String value = strings.get(0).getValue();
//                                    if (!Strings.isEmpty(value)) {
//                                        String alias = "sc_" + p.getName();
//                                        String queryParam = alias + "_VALUE";
//                                        StringBuilder subquery = new StringBuilder();
//                                        subquery.append("SELECT ").append(alias).append(".id.id FROM ").append(StringCulture.class.getSimpleName()).
//                                                append(" as ").append(alias).append(" WHERE ").append(alias).append(".value like :").append(queryParam);
//                                        queryParameters.put(queryParam, "%" + value + "%");
//
//                                        query.append(" AND a.").append(p.getName()).append(".").append(localizationForeignKeyProp).append(" IN (").
//                                                append(subquery).append(")");
//                                    }
//                                }
//                            } else if (!referencedProperty.isBookReference()) {
//                                //simple property
//                                query.append(" AND a.").append(p.getName()).append(".").append(referencedProperty.getName());
//                                String queryParam = p.getName() + "_" + referencedProperty.getName();
//                                query.append(" like :").append(queryParam);
//                                queryParameters.put(queryParam, "%" + referencedPropertyValue + "%");
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//
//        //filter by localizable values
//        for (Property p : BeanPropertyUtil.getProperties(bookType)) {
//            if (p.isLocalizable()) {
//                String localizationForeignKeyProp = p.getLocalizationForeignKeyProperty();
//                List<StringCulture> strings = (List<StringCulture>) BeanPropertyUtil.getPropertyValue(example, p.getName());
//                if (strings != null && !strings.isEmpty()) {
//                    String value = strings.get(0).getValue();
//                    if (!Strings.isEmpty(value)) {
//                        String alias = "sc_" + p.getName();
//                        String queryParam = alias + "_VALUE";
//                        StringBuilder subquery = new StringBuilder();
//                        subquery.append("SELECT ").append(alias).append(".id.id FROM ").append(StringCulture.class.getSimpleName()).
//                                append(" as ").append(alias).append(" WHERE ").append(alias).append(".value like :").append(queryParam);
//                        queryParameters.put(queryParam, "%" + value + "%");
//
//                        query.append(" AND a.").append(localizationForeignKeyProp).append(" IN (").
//                                append(subquery).append(")");
//                    }
//                }
//            }
//        }
//        query.append(" ");
//
//        //order by expression
//        if (!size) {
//            if (sortProp != null) {
//                if (sortProp.isLocalizable()) {
//                    query.append("ORDER BY sc.value");
//                } else if (sortProp.isBookReference()) {
//                    Class referencedBeanClass = sortProp.getType();
//                    String referencedPropName = sortProp.getReferencedField();
//                    Property referencedProperty = BeanPropertyUtil.getPropertyByName(referencedBeanClass, referencedPropName);
//                    if (referencedProperty.isLocalizable()) {
//                        query.append("ORDER BY sc.value");
//                    } else {
//                        query.append("ORDER BY ").append(sortProperty).append(".").append(referencedPropName);
//                    }
//                } else {
//                    //simple property
//                    query.append("ORDER BY ").append("a.").append(sortProperty);
//                }
//            } else {
//                if (sortProperty != null) {
//                    query.append("ORDER BY a.").append(sortProperty);
//                } else {
//                    //do not sort.
//                }
//            }
//            if (isAscending != null) {
//                query.append(" ").append(isAscending ? "ASC" : "DESC");
//            }
//        }
//
//        return new QueryWithParameters(query.toString(), queryParameters);
//    }
    private class QueryAndParameters {

        private String alias;

        private Class entityType;

        private String query;

        private Map<String, Object> parameters;

        public QueryAndParameters(String alias, Class entityType, String query, Map<String, Object> parameters) {
            this.alias = alias;
            this.entityType = entityType;
            this.query = query;
            this.parameters = parameters;
        }
    }

    private <T> Query queryForContent(Session session, T example, String sortProperty, Boolean isAscending, Locale locale, ShowBooksMode showBooksMode) {
        QueryAndParameters queryAndParameters = query(example, sortProperty, isAscending, locale, false, showBooksMode);
        Query query = createSQLQuery(session, queryAndParameters, false);
        applyParameters(query, queryAndParameters);
        return query;
    }

    private <T> Query queryForSize(Session session, T example, ShowBooksMode showBooksMode) {
        QueryAndParameters queryAndParameters = query(example, null, null, null, true, showBooksMode);
        Query query = createSQLQuery(session, queryAndParameters, true);
        applyParameters(query, queryAndParameters);
        return query;
    }
}
