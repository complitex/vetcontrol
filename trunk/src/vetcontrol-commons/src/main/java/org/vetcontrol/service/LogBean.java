package org.vetcontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.User;
import org.vetcontrol.util.DateUtil;

import javax.annotation.Resource;
import javax.ejb.*;
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

    private static final Logger logger = LoggerFactory.getLogger(LogBean.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private SessionContext sctx;
    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    /**
     * Use MessageFormat. http://java.sun.com/j2se/1.5.0/docs/api/java/text/MessageFormat.html
     * @param module Module Type
     * @param event Event Type
     * @param controllerClass Controller Class
     * @param modelClass Model class
     * @param description pattern
     * @param args arguments for pattern
     */
    public void info(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description, Object... args) {
        log(module, event, controllerClass, modelClass, STATUS.OK, MessageFormat.format(description, args), DateUtil.getCurrentDate(),
                getCurrentUser(), getCurrentClient());
    }

    public void info(Client client, MODULE module, EVENT event, Class controllerClass, Class modelClass, String description, Object... args) {
        log(module, event, controllerClass, modelClass, STATUS.OK, MessageFormat.format(description, args), DateUtil.getCurrentDate(),
                getCurrentUser(), client);
    }

    public void error(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description, Object... args) {
        log(module, event, controllerClass, modelClass, STATUS.ERROR, MessageFormat.format(description, args), DateUtil.getCurrentDate(),
                getCurrentUser(), getCurrentClient());
    }

    public void error(Client client, MODULE module, EVENT event, Class controllerClass, Class modelClass, String description, Object... args) {
        log(module, event, controllerClass, modelClass, STATUS.ERROR, MessageFormat.format(description, args), DateUtil.getCurrentDate(),
                getCurrentUser(), client);
    }

    public void info(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description) {
        log(module, event, controllerClass, modelClass, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser(), getCurrentClient());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void infoTxRequired(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description) {
        log(module, event, controllerClass, modelClass, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser(), getCurrentClient());
    }

    public void error(MODULE module, EVENT event, Class controllerClass, Class modelClass, String description) {
        log(module, event, controllerClass, modelClass, STATUS.ERROR, description, DateUtil.getCurrentDate(), getCurrentUser(), getCurrentClient());
    }

    public void info(MODULE module, EVENT event, Class controllerClass, String description) {
        log(module, event, controllerClass, null, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser(), getCurrentClient());
    }

    public void error(MODULE module, EVENT event, Class controllerClass, String description) {
        log(module, event, controllerClass, null, STATUS.OK, description, DateUtil.getCurrentDate(), getCurrentUser(), getCurrentClient());
    }

    public void info(String login, MODULE module, EVENT event, Class controllerClass, Class modelClass, String description) {
        log(module, event, controllerClass, modelClass, STATUS.OK, description, DateUtil.getCurrentDate(), getUser(login), getCurrentClient());
    }

    public void error(String login, MODULE module, EVENT event, Class controllerClass, Class modelClass, String description) {
        log(module, event, controllerClass, modelClass, STATUS.ERROR, description, DateUtil.getCurrentDate(), getUser(login), getCurrentClient());
    }

    public void info(String login, MODULE module, EVENT event, Class controllerClass, String description) {
        log(module, event, controllerClass, null, STATUS.OK, description, DateUtil.getCurrentDate(), getUser(login), getCurrentClient());
    }

    public void error(String login, MODULE module, EVENT event, Class controllerClass, String description) {
        log(module, event, controllerClass, null, STATUS.ERROR, description, DateUtil.getCurrentDate(), getUser(login), getCurrentClient());
    }

    private void log(MODULE module, EVENT event, Class controllerClass, Class modelClass, Log.STATUS status,
            String description, Date date, User user, Client client) {
        Log log = new Log();

        log.setDate(date);
        log.setUser(user);
        log.setClient(client);
        if (controllerClass != null) {
            log.setControllerClass(controllerClass.getName());
        }
        if (modelClass != null) {
            log.setModelClass(modelClass.getName());
        }
        log.setModule(module);
        log.setEvent(event);
        log.setStatus(status);
        log.setDescription(description);

        try {
            entityManager.persist(log);
        } catch (Exception e) {
            LogBean.logger.error("Ошибка сохранения лога в базу данных", e);
        }
    }

    private User getUser(String login) {
        try {
            return entityManager.createQuery("from User u where u.login = :login", User.class).setParameter("login", login).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private User getCurrentUser() {
        try {
            return getUser(sctx.getCallerPrincipal().getName());
        } catch (Exception e) {
            return null;
        }
    }

    public Date getLastDate(MODULE module, EVENT event, STATUS status) {
        try {
            return entityManager.createQuery("select max(l.date) from Log l where l.module = :module "
                    + "and l.event = :event and l.status = :status", Date.class).setParameter("module", module).setParameter("event", event).setParameter("status", status).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public STATUS getLastStatus(MODULE module, EVENT event, Class modelClass) {
        if (modelClass == null) {
            throw new IllegalArgumentException("Model class can't be null");
        }
        try {
            return entityManager.createQuery("SELECT l.status FROM Log l WHERE l.module = :module AND l.event = :event AND l.modelClass = :modelClass "
                    + "AND l.date = (SELECT MAX(l2.date) FROM Log l2 WHERE l2.module = :module AND l2.event = :event AND l2.modelClass = :modelClass)",
                    STATUS.class).
                    setParameter("module", module).
                    setParameter("event", event).
                    setParameter("modelClass", modelClass.getName()).
                    getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private Client getCurrentClient() {
        try {
            return clientBean.getCurrentClient();
        } catch (Exception e) {
            return null;
        }

    }
}
