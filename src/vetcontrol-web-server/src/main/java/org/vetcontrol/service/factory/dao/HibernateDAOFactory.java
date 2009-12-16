/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.factory.dao;

import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.service.dao.AbstractGenericDAO;
import org.vetcontrol.service.dao.books.BookDAO;
import org.vetcontrol.service.dao.books.IBookDAO;
import org.vetcontrol.service.dao.users.IUserDAO;
import org.vetcontrol.service.dao.users.UserDAO;

/**
 *
 * @author Artem
 */
public class HibernateDAOFactory extends DAOFactory {

    private Map<Class, AbstractGenericDAO> daos = new HashMap<Class, AbstractGenericDAO>();

    @Override
    public IUserDAO getUserDAO() {
        return getDAO(UserDAO.class);
    }

    @Override
    public IBookDAO getBookDAO() {
        return getDAO(BookDAO.class);
    }

    private synchronized <T> T getDAO(Class<T> daoClass) {
        try {
            AbstractGenericDAO dao = daos.get(daoClass);
            if (dao == null) {
                dao = (AbstractGenericDAO) daoClass.newInstance();
                daos.put(daoClass, dao);
            }
            return (T)dao;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Can not instantiate DAO: " + daoClass, e);
        }
    }
}
