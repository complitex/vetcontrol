package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 17:03:03
 *
 * Модель группы привилегий
 */
@Entity
@Table(name = "usergroup")
public class UserGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "usergroup", nullable = false)
    private SecurityGroup securityGroup;

    @ManyToOne
    @JoinColumn(name = "login", referencedColumnName = "login", nullable = false)
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                .append(", userGroup: ").append(securityGroup).append("]")
                .toString();
    }
}
