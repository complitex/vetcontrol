package org.vetcontrol.logging.service;

import org.vetcontrol.entity.Log;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.01.2010 11:54:14
 */
public class LogFilter {
    private Long id;

    private Date date;

    private String login;

    private String controllerClass;

    private String modelClass;

    private Log.MODULE module;

    private Log.EVENT event;

    private Log.STATUS status;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(String controllerClass) {
        this.controllerClass = controllerClass;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public Log.MODULE getModule() {
        return module;
    }

    public void setModule(Log.MODULE module) {
        this.module = module;
    }

    public Log.EVENT getEvent() {
        return event;
    }

    public void setEvent(Log.EVENT event) {
        this.event = event;
    }

    public Log.STATUS getStatus() {
        return status;
    }

    public void setStatus(Log.STATUS status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
