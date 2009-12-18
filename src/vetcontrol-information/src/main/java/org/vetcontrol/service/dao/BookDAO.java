/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vetcontrol.service.dao.AbstractGenericDAO;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BookDAO extends AbstractGenericDAO<Serializable, Serializable> implements IBookDAO {

    public List<Serializable> getBookContent(Class<? extends Serializable> bookType) {
        CriteriaQuery query = getEntityManager().getCriteriaBuilder().createQuery();
        Root root = query.from(bookType);
        query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

}
