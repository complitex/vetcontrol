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
@Stateless(name="BookPageFasade")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BookPageFasade extends AbstractFasade{

    private IBookDAO bookDAO;

    @EJB
    public void setBookDAO(IBookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public <T> List<T> getBookContent(Class<T> bookType, int first, int count) {
        return bookDAO.getBookContent(bookType, first, count);
    }
    
    public <T> List<T> getContent(T example, int first, int count) {
        return bookDAO.getContent(example, first, count);
    }

    public <T> Long size(T example){
        return bookDAO.size(example);
    }
}
