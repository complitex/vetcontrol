/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Artem
 */
public interface GenericDAO<T extends Serializable, ID extends Serializable> {

    T findById(ID id);

    T saveOrUpdate(T entity);

    void remove(T entity);

    List<T> findAll();

}
