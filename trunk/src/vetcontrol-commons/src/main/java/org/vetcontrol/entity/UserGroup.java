package org.vetcontrol.entity;

import org.vetcontrol.sync.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
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
public class UserGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlID @XmlJavaTypeAdapter(LongAdapter.class)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "usergroup", nullable = false)
    private SecurityGroup securityGroup;

    @Column(name = "login", nullable = false)
    private String login;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Long getId() {
        return id;
    }

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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into `usergroup` (id, `usergroup`, login, updated)" +
                " value (:id, :usergroup, :login, :updated)")
                .setParameter("id", id)
                .setParameter("usergroup", securityGroup.name())
                .setParameter("login", login)
                .setParameter("updated", updated);    
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserGroup other = (UserGroup) obj;
        if (this.securityGroup != other.securityGroup && (this.securityGroup == null || !this.securityGroup.equals(other.securityGroup))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return securityGroup == null ? 0 : securityGroup.name().hashCode();
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
