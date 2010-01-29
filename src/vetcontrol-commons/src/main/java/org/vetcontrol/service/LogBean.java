package org.vetcontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.User;
import org.vetcontrol.util.DateUtil;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.01.2010 13:26:15
 */
@Stateless(name = "LogBean")
public class LogBean {
    private static final Logger log = LoggerFactory.getLogger(LogBean.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private SessionContext sctx;

    public void info(Log.MODULE module, Log.EVENT event, Class controllerClass, Class modelClass, String description){
        log(module, event, controllerClass, modelClass, Log.STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void error(Log.MODULE module, Log.EVENT event, Class controllerClass, Class modelClass, String description){
        log(module, event, controllerClass, modelClass, Log.STATUS.ERROR, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void info(Log.MODULE module, Log.EVENT event, Class controllerClass, String description){
        log(module, event, controllerClass, null, Log.STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void error(Log.MODULE module, Log.EVENT event, Class controllerClass, String description){
        log(module, event, controllerClass, null, Log.STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void info(Log.MODULE module, Log.EVENT event, Class controllerClass, String description, String login){
        log(module, event, controllerClass, null, Log.STATUS.OK, description, DateUtil.getCurrentDate(), getUser(login));
    }

    public void error(Log.MODULE module, Log.EVENT event, Class controllerClass, String description, String login){
        log(module, event, controllerClass, null, Log.STATUS.ERROR, description, DateUtil.getCurrentDate(), getUser(login));
    }

    private void log(Log.MODULE module, Log.EVENT event, Class controllerClass, Class modelClass, Log.STATUS status,
                     String description, Date date, User user){
        Log log = new Log();

        log.setDate(date);
        log.setUser(user);
        if (controllerClass != null){
            log.setControllerClass(controllerClass.getName());
        }
        if (modelClass != null){
            log.setModelClass(modelClass.getName());
        }
        log.setModule(module);
        log.setEvent(event);
        log.setStatus(status);
        log.setDescription(description);

        try {
            entityManager.persist(log);
        } catch (Exception e) {
            LogBean.log.error("Ошибка сохранения лога в базу данных", e);
        }
    }

    private User getUser(String login){
        return entityManager.createQuery("from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    private User getCurrentUser(){
        try {
            return getUser(sctx.getCallerPrincipal().getName());
        } catch (Exception e) {
            return null;
        }
    }
}
