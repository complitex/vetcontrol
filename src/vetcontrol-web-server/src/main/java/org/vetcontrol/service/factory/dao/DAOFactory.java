/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.factory.dao;

import org.vetcontrol.service.dao.books.IBookDAO;
import org.vetcontrol.service.dao.users.IUserDAO;

/**
 *
 * @author Artem
 */
public abstract class DAOFactory {

    private static final HibernateDAOFactory FACTORY = new HibernateDAOFactory();

    public static DAOFactory get() {
        try {
            return FACTORY;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Couldn't create DAOFactory: " + FACTORY);
        }
    }

    //DAOs

    public abstract IUserDAO getUserDAO();
    public abstract IBookDAO getBookDAO();
}
