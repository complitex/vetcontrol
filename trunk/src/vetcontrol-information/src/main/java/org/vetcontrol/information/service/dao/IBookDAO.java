/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.service.dao;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import org.vetcontrol.information.service.generator.Sequence;

/**
 *
 * @author Artem
 */
@Local
public interface IBookDAO {

    void setSequence(Sequence sequence);

    <T> List<T> getContent(T example, int first, int count);

    <T> Long size(T example);

    void saveOrUpdate(Serializable book);

    void setEntityManager(EntityManager em);

}
