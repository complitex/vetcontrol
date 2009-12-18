/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.fasade.app;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.model.beans.User;
import org.vetcontrol.service.dao.users.IUserDAO;
import org.vetcontrol.service.fasade.AbstractFasade;

/**
 *
 * @author Artem
 */
@Stateless(name = "AuthenticateUserFasade")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AuthenticateUserFasade extends AbstractFasade{

    @EJB
    private IUserDAO userDAO;


    public User getUserByName(final String name) {
        return userDAO.findByName(name);
    }
}
