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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.service.dao.AbstractGenericDAO;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.information.util.model.annotation.MappedProperty;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BookDAO extends AbstractGenericDAO<Serializable, Serializable> implements IBookDAO {

    private Sequence sequence;

    @EJB
    @Override
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public <T> List<T> getBookContent(Class<T> bookType) {

        CriteriaQuery query = getEntityManager().getCriteriaBuilder().createQuery();
        Root root = query.from(bookType);
        query.select(root);
        List<T> results = getEntityManager().createQuery(query).getResultList();

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

//                    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
//
//                    CriteriaQuery cq = cb.createQuery();
//                    Root r = cq.from(StringCulture.class);
//                    cq.where(cb.equal(r.get("id").get("id"), mappedProperty.getReadMethod().invoke(currentResult)));
//                    cq.select(r);
//                    List<StringCulture> subResults = getEntityManager().createQuery(cq).getResultList();
                    List<StringCulture> subResults = getEntityManager().createQuery("select s from StringCulture s where s.id.id = :id").setParameter("id", mappedProperty.getReadMethod().invoke(currentResult)).getResultList();

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
    public void saveBook(Serializable book) {

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
                if (localizableStrings != null && !localizableStrings.isEmpty()) {
                    if (localizableStrings.get(0).getId().getId() == 0) {
                        long ID = sequence.next();
                        for (StringCulture stringCulture : localizableStrings) {
                            stringCulture.getId().setId(ID);
                            getEntityManager().merge(stringCulture);
                        }
                        mappedProperty.getWriteMethod().invoke(book, ID);
                    }
                }
                for (StringCulture stringCulture : localizableStrings) {
                    getEntityManager().merge(stringCulture);
                }
            }
            getEntityManager().merge(book);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
