/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.service.dao;

import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.service.dao.BookViewDAO;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
@Stateless
@RolesAllowed(SecurityRoles.INFORMATION_EDIT)
public class BookDAO extends BookViewDAO implements IBookDAO {

    private Sequence sequence;

    @EJB
    @Override
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public void saveOrUpdate(Serializable book) {
        try {
            Session session = HibernateSessionTransformer.getSession(getEntityManager());
            Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = BeanPropertyUtil.getMappedProperties(book.getClass());

            for (PropertyDescriptor prop : mappedProperties.keySet()) {
                saveOrUpdateLocalizableStrings(mappedProperties, prop, book, session);
            }
            session.saveOrUpdate(book);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveOrUpdateLocalizableStrings(Map<PropertyDescriptor, PropertyDescriptor> mappedProperties, PropertyDescriptor prop, 
            Serializable book, Session session) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor mappedProperty = mappedProperties.get(prop);
        Method getter = prop.getReadMethod();
        List<StringCulture> localizableStrings = (List<StringCulture>) getter.invoke(book);
        if (!localizableStrings.isEmpty()) {
            if (localizableStrings.get(0).getId().getId() == null) {
                long ID = sequence.next();
                for (StringCulture culture : localizableStrings) {
                    culture.getId().setId(ID);
//                    if (Strings.isEmpty(culture.getValue())) {
//                        culture.setValue(null);
//                    }
//                    getEntityManager().merge(culture);
                }
                mappedProperty.getWriteMethod().invoke(book, ID);
            }
        }
        for (StringCulture culture : localizableStrings) {
            session.saveOrUpdate(culture);
        }
    }
    
}
