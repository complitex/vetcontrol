package org.vetcontrol.user.service;

import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.Job;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 21:32:16
 */
@Stateless(name = "UserBean")
@RolesAllowed(SecurityRoles.USER_EDIT)
public class UserBean {

    public static enum OrderBy {

        LOGIN, FIRST_NAME, LAST_NAME, MIDDLE_NAME, JOB, DEPARTMENT
    }
    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getUsers(int first, int count, OrderBy orderBy, boolean asc, String filter, Locale locale) {
        String select = "SELECT DISTINCT u FROM User u left join u.department.namesMap dn left join u.job.namesMap jn ";

        String where = "";
        if (filter != null) {
            where += "WHERE upper(u.login) like :filter or upper(u.firstName) like :filter "
                    + "or upper(u.lastName) like :filter or upper(u.middleName) like :filter "
                    + "or upper(dn.value) like :filter or upper(jn.value) like :filter";
        }

        String order = "";
        switch (orderBy) {
            case LOGIN:
                order = " order by u.login";
                break;
            case FIRST_NAME:
                order = " order by u.firstName";
                break;
            case LAST_NAME:
                order = " order by u.lastName";
                break;
            case MIDDLE_NAME:
                order = " order by u.middleName";
                break;
            case JOB:
                order = " order by jn.value";
                break;
            case DEPARTMENT:
                order = " order by dn.value";
                break;
        }

        TypedQuery<User> query = entityManager.createQuery(select + where + order + (asc ? " asc" : " desc"), User.class);
                
        if (filter != null) {
            query.setParameter("filter", "%" + filter.toUpperCase() + "%");
        }

        return query.setFirstResult(first).setMaxResults(count).getResultList();
    }

    public Long getUserCount(String filter) {
        String select = "SELECT COUNT(DISTINCT u) FROM User u left join u.department.namesMap dn left join u.job.namesMap jn ";

        String where = "";
        if (filter != null) {
            where += "WHERE upper(u.login) like :filter or upper(u.firstName) like :filter "
                    + "or upper(u.lastName) like :filter or upper(u.middleName) like :filter "
                    + "or upper(dn.value) like :filter or upper(jn.value) like :filter";
        }

        TypedQuery<Long> query = entityManager.createQuery(select + where, Long.class);

        if (filter != null) {
            query.setParameter("filter", "%" + filter.toUpperCase() + "%");
        }

        return query.getSingleResult();
    }

    public User getUser(long id) {
        return entityManager.find(User.class, id);
    }

    public List<Department> getDepartments() {
        return entityManager.createQuery("from Department", Department.class).getResultList();
    }

    public List<Job> getJobs(){
        return entityManager.createQuery("from Job", Job.class).getResultList();
    }

    public boolean isUserAuthChanged(User localUser) {
        if (localUser.getId() == null) {
            return false;
        }

        User dbUser = entityManager.find(User.class, localUser.getId());

        return localUser.getUserGroups().size() != dbUser.getUserGroups().size()
                || !localUser.getPassword().equals(dbUser.getPassword())
                || !localUser.getUserGroups().containsAll(dbUser.getUserGroups());
    }

    public void save(User user) {
        for (UserGroup userGroup : user.getUserGroups()) {
            if (userGroup.getId() == null) {
                userGroup.setUser(user);
            }
        }

        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            User currentUser = entityManager.find(User.class, user.getId());
            for (UserGroup db : currentUser.getUserGroups()) {
                boolean delete = true;
                for (UserGroup model : user.getUserGroups()) {
                    if (model.getSecurityGroup().equals(db.getSecurityGroup())) {
                        delete = false;
                        break;
                    }
                }

                if (delete) {
                    entityManager.remove(db);
                }
            }

            entityManager.merge(user);
        }
    }

    public boolean containsLogin(String login) {
        return entityManager.createQuery("select count(u) from User u where u.login = :login", Long.class).setParameter("login", login).getSingleResult() > 0;

    }
}
