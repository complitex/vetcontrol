package org.vetcontrol.user.service;

import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 21:32:16
 */
@Stateless(name = "UserBean")
@RolesAllowed(SecurityRoles.USER_EDIT)
public class UserBean {
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"unchecked"})
    public List<User> getUsers(){
        return entityManager.createQuery("from User").getResultList();
    }

    public User getUser(long id){
        return entityManager.find(User.class, id);
    }

    @SuppressWarnings({"unchecked"})
    public List<Department> getDepartments(){
        return entityManager.createQuery("from Department").getResultList();
    }

    public void save(User user){
        for (UserGroup userGroup : user.getUserGroups()){
            if (userGroup.getId() == null){
                userGroup.setUser(user);
            }
        }

        if (user.getId() == null){
            entityManager.persist(user);
        }else{
            User currentUser = entityManager.find(User.class, user.getId());
            for (UserGroup db : currentUser.getUserGroups()){
                boolean delete = true;
                for (UserGroup model : user.getUserGroups() ){
                    if (model.getUserGroup().equals(db.getUserGroup())){
                        delete = false;
                        break;
                    }
                }

                if (delete){
                    entityManager.remove(db);
                }
            }

            entityManager.merge(user);
        }
    }

    public boolean containsLogin(String login){
        return (Long)entityManager.createQuery("select count(u) from User u where u.login = :login")
                .setParameter("login",login)
                .getSingleResult() > 0;

    }
}
