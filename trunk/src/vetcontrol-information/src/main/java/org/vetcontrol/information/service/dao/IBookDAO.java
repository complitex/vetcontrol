/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.service.dao;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.service.dao.GenericDAO;

/**
 *
 * @author Artem
 */
@Local
public interface IBookDAO extends GenericDAO<Serializable, Serializable> {

    void setSequence(Sequence sequence);

    <T> List<T> getBookContent(Class<T> bookType);

    void saveBook(Serializable book);

}
