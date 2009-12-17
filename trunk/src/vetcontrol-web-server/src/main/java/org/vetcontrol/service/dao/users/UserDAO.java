/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.dao.users;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.model.beans.Role;
import org.vetcontrol.model.beans.Roles;
import org.vetcontrol.model.beans.User;
import org.vetcontrol.service.dao.AbstractGenericDAO;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserDAO extends AbstractGenericDAO<User, String> implements IUserDAO {

    @Override
    public void prepare(User user) throws SuchRoleDoesNotExistException {
        for (Role role : user.getRoles()) {
            if (getEntityManager().find(Roles.class, role.getId().getRole()) == null) {
                throw new SuchRoleDoesNotExistException("", role.getId().getRole());
            }
        }
    }

    @Override
    public User findByName(String name) {
        return findById(name);
    }
}
