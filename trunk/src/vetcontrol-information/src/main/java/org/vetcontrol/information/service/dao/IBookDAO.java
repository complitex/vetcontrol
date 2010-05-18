/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.service.dao;

import java.io.Serializable;
import javax.ejb.Local;
import org.vetcontrol.service.dao.IBookViewDAO;

/**
 *
 * @author Artem
 */
@Local
public interface IBookDAO extends IBookViewDAO {

    void saveOrUpdate(Serializable book);

    void saveAsNew(Serializable book);

//    void disable(Serializable book);
//
//    void enable(Serializable book);

    void disable(Long id, Class bookClass);

    void enable(Long id, Class bookClass);

}
