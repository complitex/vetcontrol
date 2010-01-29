package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.01.2010 13:53:38
 */
@Entity
@Table(name = "log")
public class Log implements Serializable{
    public static enum MODULE {
        COMMONS, USER, DOCUMENT, INFORMATION, REPORT, SYNC_CLIENT, SYNC_SERVER
    }

    public static enum EVENT {
        SYSTEM_START, SYSTEM_STOP, USER_LOGIN, USER_LOGOUT, LIST, VIEW, CREATE, EDIT, REMOVE
    }

    public static enum STATUS {
        OK, ERROR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "controller_class")
    private String controllerClass;

    @Column(name = "model_class")
    private String modelClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "module")
    private MODULE module;

    @Enumerated(EnumType.STRING)
    @Column(name = "event")
    private EVENT event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private STATUS status;

    @Column(name = "description")
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public MODULE getModule() {
        return module;
    }

    public void setModule(MODULE module) {
        this.module = module;
    }

    public EVENT getEvent() {
        return event;
    }

    public void setEvent(EVENT event) {
        this.event = event;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
