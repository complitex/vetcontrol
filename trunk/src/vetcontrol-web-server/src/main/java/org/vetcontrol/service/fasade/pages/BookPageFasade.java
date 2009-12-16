/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.fasade.pages;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.service.dao.books.IBookDAO;

/**
 *
 * @author Artem
 */
@Stateless(name="BookPageFasade")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class BookPageFasade extends AbstractFasade{

    @EJB
    private IBookDAO bookDAO;

    public List<Serializable> getBookContent(final Class bookType) {
        return bookDAO.getBookContent(bookType);
    }

}
