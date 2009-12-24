package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 17:03:03
 */
@Entity
@Table(name = "usergroup")
public class UserGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "usergroup", nullable = false)
    private String userGroup;

    @ManyToOne
    @JoinColumn(name = "login", referencedColumnName = "login", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }        
}
