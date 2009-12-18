/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.fasade.pages;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.model.beans.User;
import org.vetcontrol.service.dao.users.IUserDAO;
import org.vetcontrol.service.dao.users.SuchRoleDoesNotExistException;
import org.vetcontrol.service.fasade.AbstractFasade;

/**
 *
 * @author Artem
 */
@Stateless(name="AddUpdateUserPageFasade")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AddUpdateUserPageFasade extends AbstractFasade {

    @EJB
    private IUserDAO userDAO;

    public User getUser(final String name) {
        return userDAO.findByName(name);
    }

    public void saveOrUpdate(final User user) throws SuchRoleDoesNotExistException {
        userDAO.prepare(user);
        userDAO.saveOrUpdate(user);
    }
}
