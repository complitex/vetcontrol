package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.01.2010 13:53:38
 */
@Entity
@Table(name = "log")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Log implements Serializable {

    public static enum MODULE {

        COMMONS, USER, DOCUMENT, INFORMATION, REPORT, SYNC_CLIENT, SYNC_SERVER
    }

    public static enum EVENT {

        SYSTEM_START, SYSTEM_STOP, USER_LOGIN, USER_LOGOUT, LIST, VIEW, CREATE, EDIT, CREATE_AS_NEW, REMOVE, SYNC, SYNC_COMMIT, DISABLE, ENABLE,
        SYNC_UPDATED, SYNC_DELETED
    }

    public static enum STATUS {

        OK, ERROR
    }

    @Id
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @XmlIDREF
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

    @ManyToOne
    @JoinColumn(name = "client_id")
    @XmlIDREF
    private Client client;

    /**
     * Change details. Relevant for EDIT logs only.
     * @return
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "change_detail", joinColumns = @JoinColumn(name = "log_id"))
    @GenericGenerator(name = "TableGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator",
    parameters = {
        @Parameter(name = "segment_value", value = "change_detail"),
        @Parameter(name = "table_name", value = "generator"),
        @Parameter(name = "segment_column_name", value = "generatorName"),
        @Parameter(name = "value_column_name", value = "generatorValue"),
        @Parameter(name = "initial_value", value = "0"),
        @Parameter(name = "increment_size", value = "1")})
    @CollectionId(columns = @Column(name = "change_detail_id"), generator = "TableGenerator", type = @Type(type = "long"))
    private Collection<Change> changeDetails = new ArrayList<Change>();

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Collection<Change> getChangeDetails() {
        return changeDetails;
    }

    public void setChangeDetails(Collection<Change> changeDetails) {
        this.changeDetails = changeDetails;
    }
}
