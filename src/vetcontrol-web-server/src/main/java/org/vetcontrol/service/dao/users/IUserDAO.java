/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao.users;

import javax.ejb.Local;
import org.vetcontrol.model.beans.User;
import org.vetcontrol.service.dao.GenericDAO;

/**
 *
 * @author Artem
 */
@Local
public interface IUserDAO extends GenericDAO<User, String> {

    void prepare(User user) throws SuchRoleDoesNotExistException;

    User findByName(String name);

}
