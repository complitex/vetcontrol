package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;
import org.vetcontrol.sync.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 17:03:03
 *
 * Модель группы привилегий
 */
@Entity
@Table(name = "usergroup")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserGroup implements IUpdated, ILongId {
    @Id
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
    @XmlID @XmlJavaTypeAdapter(LongAdapter.class)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "usergroup", nullable = false)
    private SecurityGroup securityGroup;

    @Column(name = "login")
    private String login;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public SecurityGroup getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(SecurityGroup securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGroup)) return false;

        UserGroup userGroup = (UserGroup) o;

        if (id != null ? !id.equals(userGroup.id) : userGroup.id != null) return false;
        if (login != null ? !login.equals(userGroup.login) : userGroup.login != null) return false;
        if (securityGroup != userGroup.securityGroup) return false;
        if (updated != null ? !updated.equals(userGroup.updated) : userGroup.updated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (securityGroup != null ? securityGroup.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[hash: ").append(Integer.toHexString(hashCode()))
                .append(", id: ").append(id)
                .append(", login: ").append(login)
                .append(", userGroup: ").append(securityGroup).append("]")
                .toString();
    }
}
