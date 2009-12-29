/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.service.fasade.pages;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.service.fasade.AbstractFasade;

/**
 *
 * @author Artem
 */
@Stateless(name="AddUpdateBookEntryPageFasade")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AddUpdateBookEntryPageFasade extends AbstractFasade {

    @EJB
    private IBookDAO bookDAO;

    public void saveOrUpdate(final Serializable bookEntry) {
        bookDAO.saveOrUpdate(bookEntry);
    }

    public <T> List<T> getAll(Class<T> bookType){
        try {
            T example = bookType.newInstance();
            return bookDAO.getContent(example, 0,
                bookDAO.size(example).intValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
