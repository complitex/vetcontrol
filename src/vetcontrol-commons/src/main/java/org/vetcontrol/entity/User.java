package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 16:13:49
 *
 * Модель пользователя
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "login", length = 32, unique = true, nullable = false)
    private String login;
    @Column(name = "_password", length = 32, nullable = false)
    private String password;
    @Transient
    private String changePassword;
    @Column(name = "first_name", length = 45)
    private String firstName;
    @Column(name = "middle_name", length = 45)
    private String middleName;
    @Column(name = "last_name", length = 45)
    private String lastName;
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroup> userGroups = new ArrayList<UserGroup>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;
    @Column(name = "locale", length = 2)
    private String locale;
    @Column(name = "page_size")
    private Integer pageSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[hash: ").append(Integer.toHexString(hashCode())).append(", id: ").append(id).append(", login: ").append(login).append(", firstName: ").append(firstName).append(", lastName: ").append(lastName).append(", middleName: ").append(middleName).append(", userGroups: ").append(userGroups).append(", department: ").append(department).append("]").toString();
    }

    @Transient
    public String getFullName() {
        return ((lastName != null) ? lastName + " " : "")
                + ((firstName != null) ? firstName + " " : "")
                + ((middleName != null) ? middleName : "");
    }

    @Transient
    public String getShortName() {
        return ((lastName != null) ? lastName + " " : "")
                + ((firstName != null) ? firstName.substring(0, 1) + ". " : "")
                + ((middleName != null) ? middleName.substring(0, 1) : ".");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.login == null) ? (other.login != null) : !this.login.equals(other.login)) {
            return false;
        }
        if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password)) {
            return false;
        }
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.middleName == null) ? (other.middleName != null) : !this.middleName.equals(other.middleName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if (this.job != null && other.job != null) {
            if (this.job.getId() != other.job.getId() && (this.job.getId() == null || !this.job.getId().equals(other.job.getId()))) {
                return false;
            }
        }
        if (this.department != null && other.department != null) {
            if (this.department.getId() != other.department.getId() && (this.department.getId() == null || !this.department.getId().equals(other.department.getId()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + (this.login != null ? this.login.hashCode() : 0);
        hash = 37 * hash + (this.password != null ? this.password.hashCode() : 0);
        hash = 37 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 37 * hash + (this.middleName != null ? this.middleName.hashCode() : 0);
        hash = 37 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 37 * hash + (this.job != null ? (this.job.getId() != null ? this.job.getId().hashCode() : 0) : 0);
        hash = 37 * hash + (this.department != null ? (this.department.getId() != null ? this.department.getId().hashCode() : 0) : 0);
        return hash;
    }
}
