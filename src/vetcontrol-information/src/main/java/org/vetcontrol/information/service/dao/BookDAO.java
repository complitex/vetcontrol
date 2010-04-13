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
import java.io.Serializable;
import java.util.List;
import javax.persistence.TemporalType;
import org.hibernate.Session;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.util.book.Property;
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
            saveOrUpdateLocalizableStrings(book, session);
            session.saveOrUpdate(book);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveOrUpdateLocalizableStrings(Serializable book, Session session) {
        try {
            for (Property prop : BeanPropertyUtil.getProperties(book.getClass())) {
                if (prop.isLocalizable()) {
                    List<StringCulture> localizableStrings = (List<StringCulture>) BeanPropertyUtil.getPropertyValue(book, prop.getName());
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
                            BeanPropertyUtil.setPropertyValue(book, prop.getLocalizationForeignKeyProperty(), ID);
                        }
                    }
                    for (StringCulture culture : localizableStrings) {
                        session.saveOrUpdate(culture);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disable(Serializable book) {
        switchActiveState(book, true);
    }

    @Override
    public void enable(Serializable book) {
        switchActiveState(book, false);
    }

    private void switchActiveState(Serializable book, boolean disabled) {
        try {
            StringBuilder updateQuery = new StringBuilder().append("UPDATE ").
                    append(book.getClass().getSimpleName()).
                    append(" a SET a.").
                    append(BeanPropertyUtil.getDisabledPropertyName()).
                    append(" = :disabled").
                    append(", a.").
                    append(BeanPropertyUtil.getVersionPropertyName()).
                    append(" = :updated").
                    append(" WHERE a.id = :id");
            getEntityManager().createQuery(updateQuery.toString()).
                    setParameter("updated", DateUtil.getCurrentDate(), TemporalType.TIMESTAMP).
                    setParameter("disabled", disabled).
                    setParameter("id", BeanPropertyUtil.getPropertyValue(book, "id")).
                    executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
