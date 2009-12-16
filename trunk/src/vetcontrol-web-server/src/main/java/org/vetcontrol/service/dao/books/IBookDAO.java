/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao.books;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;
import org.vetcontrol.service.dao.GenericDAO;

/**
 *
 * @author Artem
 */
@Local
public interface IBookDAO extends GenericDAO<Serializable, Serializable> {

    List<Serializable> getBookContent(Class<? extends Serializable> bookType);

}
