package org.vetcontrol.service;

import org.vetcontrol.entity.User;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.Resource;

import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.12.2009 5:40:24
 */
@Stateless(name = "UserProfileBean")
@RolesAllowed(SecurityRoles.AUTHORIZED)
public class UserProfileBean {
    @Resource
    private SessionContext sctx;

    @PersistenceContext
    private EntityManager entityManager;

    public User getCurrentUser(){
        String login = sctx.getCallerPrincipal().getName();

        return entityManager.createQuery("from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }
}
