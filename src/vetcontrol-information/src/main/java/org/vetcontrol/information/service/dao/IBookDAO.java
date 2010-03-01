/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.service.dao;

import java.io.Serializable;
import javax.ejb.Local;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.service.dao.IBookViewDAO;

/**
 *
 * @author Artem
 */
@Local
public interface IBookDAO extends IBookViewDAO {

    void setSequence(Sequence sequence);

    void saveOrUpdate(Serializable book);

    void disable(Serializable book);

}
