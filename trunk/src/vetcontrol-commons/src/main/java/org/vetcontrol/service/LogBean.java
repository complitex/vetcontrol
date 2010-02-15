package org.vetcontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.User;
import org.vetcontrol.util.DateUtil;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.MessageFormat;
import java.util.Date;

import static org.vetcontrol.entity.Log.*;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.01.2010 13:26:15
 */
@Stateless(name = "LogBean")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class LogBean {
    private static final Logger log = LoggerFactory.getLogger(LogBean.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private SessionContext sctx;

    /**
     * Use MessageFormat. http://java.sun.com/j2se/1.5.0/docs/api/java/text/MessageFormat.html
     * @param module Module Type
     * @param event Event Type
     * @param controllerClass Controller Class
     * @param modelClass Model class
     * @param description pattern
     * @param args arguments for pattern
     */
    public void info(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description, Object... args){
        log(module, event, controllerClass, modelClass, STATUS.OK, MessageFormat.format(description, args), DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void error(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description, Object... args){
        log(module, event, controllerClass, modelClass, STATUS.ERROR, MessageFormat.format(description, args), DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void info(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description){
        log(module, event, controllerClass, modelClass, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void error(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description){
        log(module, event, controllerClass, modelClass, STATUS.ERROR, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void info(MODULE module, EVENT event, Class controllerClass, String description){
        log(module, event, controllerClass, null, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void error(MODULE module, EVENT event, Class controllerClass, String description){
        log(module, event, controllerClass, null, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser());
    }

    public void info(String login, MODULE module, EVENT event, Class controllerClass, String description){
        log(module, event, controllerClass, null, STATUS.OK, description, DateUtil.getCurrentDate(), getUser(login));
    }

    public void error(String login, MODULE module, EVENT event, Class controllerClass, String description){
        log(module, event, controllerClass, null, STATUS.ERROR, description, DateUtil.getCurrentDate(), getUser(login));
    }
       
    private void log(MODULE module, EVENT event, Class controllerClass, Class modelClass, Log.STATUS status,
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
        try {
            return entityManager.createQuery("from User u where u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private User getCurrentUser(){
        try {
            return getUser(sctx.getCallerPrincipal().getName());
        } catch (Exception e) {
            return null;
        }
    }
}
